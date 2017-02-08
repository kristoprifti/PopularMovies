package me.kristoprifti.android.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kristoprifti.android.popularmovies.R;
import me.kristoprifti.android.popularmovies.adapters.MovieAdapter;
import me.kristoprifti.android.popularmovies.data.PopularMoviesPreferences;
import me.kristoprifti.android.popularmovies.models.Movie;
import me.kristoprifti.android.popularmovies.utilities.MovieDBJsonUtils;
import me.kristoprifti.android.popularmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        NetworkUtils.OnDownloadComplete{

    private static final String TAG = MainActivity.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    private Snackbar snackbar;

    @BindView(R.id.rv_movies) RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.pb_load_more_indicator) ProgressBar mLoadingMoreIndicator;
    @BindView(R.id.mainView) FrameLayout mainView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nestedScrollView) NestedScrollView mNestedScrollView;

    private static final int MOVIE_LOADER_ID = 111;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    /* This ArrayList will hold and help cache our movies data */
    private ArrayList<Movie> mMoviesList;
    private int pageNumber = 1;

    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mMoviesList = new ArrayList<>();
        /*
         * A GridLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a grid list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the GridLayoutManager
         * constructor. In our case, we want a vertical list, so we pass in the constant from the
         * GridLayoutManager class for vertical lists, GridLayoutManager.VERTICAL.
         */
        int recyclerViewOrientation = GridLayoutManager.VERTICAL;
        final GridLayoutManager layoutManager;
        /*
         * check if the device is landscape or portrait mode
         * if its portrait show 2 columns if its landscape show 4
        */
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutManager = new GridLayoutManager(this, 3, recyclerViewOrientation, false);
        } else {
            layoutManager = new GridLayoutManager(this, 2, recyclerViewOrientation, false);
        }

        //apply this layout manager to the recyclerview
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * fixed size property set to true improves performance of recyclerview
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The MovieAdapter is responsible for linking our movie data with the Views that
         * will end up displaying our movie data.
         */
        mMovieAdapter = new MovieAdapter(this);

        /* attaching the adapter to the recyclerview */
        mRecyclerView.setAdapter(mMovieAdapter);

        if(savedInstanceState != null && savedInstanceState.containsKey(getString(R.string.movies_key))) {
            Log.d(TAG, "onCreate: there is savedinstancestate");
            mMoviesList = savedInstanceState.getParcelableArrayList(getString(R.string.movies_key));
            mMovieAdapter.setMoviesList(mMoviesList);

            final int[] position = savedInstanceState.getIntArray(getString(R.string.scroll_position_key));
            if(position != null)
                mNestedScrollView.post(new Runnable() {
                    public void run() {
                        mNestedScrollView.smoothScrollTo(position[0], position[1]);
                    }
                });
        } else if(!PopularMoviesPreferences
                .getPreferredSortType(MainActivity.this).equals(getString(R.string.pref_orderby_favorites))) {
            if (mMoviesList.size() == 0) {
                Log.d(TAG, "onCreate: load from server");
                loadMoviesFromServer();
            }
        } else {
            Log.d(TAG, "onCreate: load from loader");
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        }

        addScrollListenerToNestedScrollView();

        Log.d(TAG, "onCreate: registering on preference changed listener");
        /*
         * Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed.
         */
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        Log.d(TAG, "onCreate: ends");
    }

    private void addScrollListenerToNestedScrollView(){
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) &&
                        !PopularMoviesPreferences
                                .getPreferredSortType(MainActivity.this).equals(getString(R.string.pref_orderby_favorites))) {
                    Log.d(TAG, "onScrollChange: " + pageNumber);
                    if(loading){
                        loading = false;
                        pageNumber++;
                        loadMoviesFromServer();
                    }
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: starts");
        outState.putParcelableArrayList(getString(R.string.movies_key), mMoviesList);
        outState.putIntArray(getString(R.string.scroll_position_key),
                new int[]{ mNestedScrollView.getScrollX(), mNestedScrollView.getScrollY()});
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ends");
    }

    /**
     * Instantiate and return a new Loader the specific Loader ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(final int id, final Bundle loaderArgs) {
        Log.d(TAG, "onCreateLoader: starts");
        if(id == MOVIE_LOADER_ID){
            return new AsyncTaskLoader<ArrayList<Movie>>(this) {
                /**
                 * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
                 */
                @Override
                protected void onStartLoading() {
                    Log.d(TAG, "onStartLoading: starts");
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

                /**
                 * This is the method of the AsyncTaskLoader that will load and parse the JSON data
                 * from TheMovieDB in the background.
                 *
                 * @return Movie data from TheMovieDB as an ArrayList of Movie objects.
                 *         null if an error occurs
                 */
                @Override
                public ArrayList<Movie> loadInBackground() {
                    Log.d(TAG, "loadInBackground: starts");
                    return NetworkUtils.requestFavoriteMovies(MainActivity.this);
                }
            };
        } else {
            return null;
        }
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        Log.d(TAG, "onLoadFinished: starts");
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null && data.size() != 0) {
            mMoviesList.addAll(data);
            mMovieAdapter.setMoviesList(mMoviesList);
            showMovieDataView();
            Log.d(TAG, "onLoadFinished: show data");
        } else if (data == null || data.size() == 0){
            Log.d(TAG, "onLoadFinished: show empty message");
            showNoFavoritesMessage();
        } else {
            Log.d(TAG, "onLoadFinished: show error querying data");
            showErrorMessage();
        }
        Log.d(TAG, "onLoadFinished: ends");
        getSupportLoaderManager().destroyLoader(loader.getId());
    }

    /**
     * Called when a previously created loader is being reset
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        Log.d(TAG, "onLoaderReset: starts");
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        Log.d(TAG, "invalidateData: starts");
        mMoviesList = new ArrayList<>();
        mMovieAdapter.setMoviesList(null);
        Log.d(TAG, "invalidateData: ends");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PopularMoviesPreferences
                .getPreferredSortType(MainActivity.this).equals(getString(R.string.pref_orderby_favorites))){
            invalidateData();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param selectedMovie object representing movie details for a particular movie
     */
    @Override
    public void onClick(Movie selectedMovie, View view, int colorPalette) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(getString(R.string.intent_movie_object), selectedMovie);
        intentToStartDetailActivity.putExtra(getString(R.string.intent_color_integer), colorPalette);

        ImageView posterView = (ImageView) view.findViewById(R.id.iv_movie_poster);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, posterView, getString(R.string.poster_transition));
        startActivity(intentToStartDetailActivity, options.toBundle());
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     */
    private void showMovieDataView() {
        Log.d(TAG, "showMovieDataView: starts");
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
        if(snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
        }
        Log.d(TAG, "showMovieDataView: ends");
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     */
    private void showErrorMessage() {
        Log.d(TAG, "showErrorMessage: starts");
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        showSnackBar(getString(R.string.error_message), true);
        Log.d(TAG, "showErrorMessage: ends");
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     */
    private void showNoFavoritesMessage() {
        Log.d(TAG, "showNoFavoritesMessage: starts");
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        showSnackBar(getString(R.string.no_favorites_message), false);
        Log.d(TAG, "showNoFavoritesMessage: ends");
    }

    /**
     * OnStart is called when the Activity is coming into view.
     * if preferences have been changed we will initialize the loader again
     */
    @Override
    protected void onStart() {
        super.onStart();
        /*
         * If the preferences for sorting have changed since the user was last in
         * MainActivity, perform another query and set the flag to false.
         */
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(TAG, "onStart: preferences were updated");
            invalidateData();
            if(!PopularMoviesPreferences
                    .getPreferredSortType(MainActivity.this).equals(getString(R.string.pref_orderby_favorites))) {
                Log.d(TAG, "onStart: loader destroyed");
                loadMoviesFromServer();
            } else {
                Log.d(TAG, "onStart: loader started");
                getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
            }
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: starts");
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        Log.d(TAG, "onCreateOptionsMenu: ends");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: starts");
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        Log.d(TAG, "onOptionsItemSelected: ends");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        /*
         * Set this flag to true so that when control returns to MainActivity, it can refresh the
         * data.
         */
        PREFERENCES_HAVE_BEEN_UPDATED = true;
        pageNumber = 1;
    }

    private void showSnackBar(String message, boolean action){
        Log.d(TAG, "showSnackBar: starts");
        snackbar = Snackbar.make(mainView, message, Snackbar.LENGTH_INDEFINITE);
        if(action){
            snackbar.setAction(R.string.action_retry, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(PopularMoviesPreferences
                            .getPreferredSortType(MainActivity.this).equals(getString(R.string.pref_orderby_favorites))){
                        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                    } else {
                        loadMoviesFromServer();
                    }
                }
            });
            // Changing message text color
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        }

        // Changing snackbar background color
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Changing action button text color
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();
        Log.d(TAG, "showSnackBar: ends");
    }

    private void loadMoviesFromServer(){
        Log.d(TAG, "loadMoviesFromServer: starts");
        if(NetworkUtils.checkInternetConnection(this)){
            if(pageNumber > 1){
                mLoadingMoreIndicator.setVisibility(View.VISIBLE);
            } else {
                mLoadingIndicator.setVisibility(View.VISIBLE);
            }
            URL movieRequestUrl = NetworkUtils.buildUrl(PopularMoviesPreferences.getPreferredSortType(MainActivity.this), pageNumber);
            try {
                NetworkUtils.getResponseFromHttpUrl(movieRequestUrl, this, NetworkUtils.GET_MOVIE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showSnackBar(getString(R.string.no_internet), true);
        }
        Log.d(TAG, "loadMoviesFromServer: ends");
    }

    @Override
    public void onDownloadComplete(String responseString, int responseCode) {
        Log.d(TAG, "onDownloadComplete: starts");
        try {
            if(responseCode == NetworkUtils.GET_MOVIE){
                ArrayList<Movie> mMoviesLocal = MovieDBJsonUtils.getSimpleMovieStringsFromJson(responseString);
                if (mMoviesLocal != null) {
                    mMoviesList.addAll(mMoviesLocal);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMovieAdapter.setMoviesList(mMoviesList);
                            if(pageNumber > 1){
                                mLoadingMoreIndicator.setVisibility(View.INVISIBLE);
                            } else {
                                mLoadingIndicator.setVisibility(View.INVISIBLE);
                            }
                            showMovieDataView();
                        }
                    });
                    loading = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onDownloadComplete: ends");
    }
}
