package me.kristoprifti.android.popularmovies.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

import me.kristoprifti.android.popularmovies.R;
import me.kristoprifti.android.popularmovies.adapters.MovieAdapter;
import me.kristoprifti.android.popularmovies.data.PopularMoviesPreferences;
import me.kristoprifti.android.popularmovies.models.Movie;
import me.kristoprifti.android.popularmovies.utilities.MovieDBJsonUtils;
import me.kristoprifti.android.popularmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private static final int MOVIE_LOADER_ID = 0;

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    private FrameLayout mainView;

    /* This ArrayList will hold and help cache our movies data */
    private ArrayList<Movie> mMoviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * Reference to the recycler view in the layout file
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mainView = (FrameLayout) findViewById(R.id.mainView);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * A GridLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a grid list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the GridLayoutManager
         * constructor. In our case, we want a vertical list, so we pass in the constant from the
         * GridLayoutManager class for vertical lists, GridLayoutManager.VERTICAL.
         */
        int recyclerViewOrientation = GridLayoutManager.VERTICAL;

        GridLayoutManager layoutManager;
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

        /*
         * getting reference of the loading indicator in the layout file
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        if(savedInstanceState != null && savedInstanceState.containsKey(getString(R.string.movies_key)))
            mMoviesList = savedInstanceState.getParcelableArrayList(getString(R.string.movies_key));
        /*
         * initializing the loader
         */
        initLoader();

        Log.d(TAG, "onCreate: registering on preference changed listener");
        /*
         * Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed.
         */
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: starts");
        outState.putParcelableArrayList(getString(R.string.movies_key), mMoviesList);
        outState.putParcelable(getString(R.string.scroll_position), mRecyclerView.getLayoutManager().onSaveInstanceState());
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
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<ArrayList<Movie>>(this) {
            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if (mMoviesList != null) {
                    Log.d(TAG, "onStartLoading: list exists");
                    deliverResult(mMoviesList);
                } else {
                    Log.d(TAG, "onStartLoading: list doesnt exits");
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
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
                String orderByPreference = PopularMoviesPreferences
                        .getPreferredSortType(MainActivity.this);

                URL movieRequestUrl = NetworkUtils.buildUrl(orderByPreference);

                try {
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieRequestUrl);
                    return MovieDBJsonUtils
                            .getSimpleMovieStringsFromJson(jsonMovieResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(ArrayList<Movie> data) {
                mMoviesList = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMovieAdapter.setMoviesList(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showMovieDataView();
        }
    }

    /**
     * Called when a previously created loader is being reset
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        mMovieAdapter.setMoviesList(null);
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param selectedMovie object representing movie details for a particular movie
     */
    @Override
    public void onClick(Movie selectedMovie, View view) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, selectedMovie);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this, view.findViewById(R.id.iv_movie_poster), String.valueOf(R.string.poster_transition)).toBundle();
                startActivity(intentToStartDetailActivity, bundle);
            }
        } else {
            startActivity(intentToStartDetailActivity);
        }
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
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
            initLoader();
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
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            invalidateData();
            initLoader();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        /*
         * Set this flag to true so that when control returns to MainActivity, it can refresh the
         * data.
         */
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    /*Check if app is connected to the internet*/
    public void initLoader() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()){
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        } else {
            Snackbar snackbar = Snackbar
                    .make(mainView, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initLoader();
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent));

            // Changing snackbar background color
            ViewGroup group = (ViewGroup) snackbar.getView();
            group.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

            // Changing action button text color
            View snackbarView = snackbar.getView();
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();
        }
    }
}
