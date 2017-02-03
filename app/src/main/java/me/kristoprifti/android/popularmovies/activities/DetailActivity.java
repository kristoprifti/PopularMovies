package me.kristoprifti.android.popularmovies.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kristoprifti.android.popularmovies.R;
import me.kristoprifti.android.popularmovies.adapters.ReviewAdapter;
import me.kristoprifti.android.popularmovies.adapters.TrailerAdapter;
import me.kristoprifti.android.popularmovies.data.MoviesContract;
import me.kristoprifti.android.popularmovies.handlers.FavoriteMovieQueryHandler;
import me.kristoprifti.android.popularmovies.models.Movie;
import me.kristoprifti.android.popularmovies.models.Review;
import me.kristoprifti.android.popularmovies.models.Trailer;
import me.kristoprifti.android.popularmovies.utilities.MovieDBJsonUtils;
import me.kristoprifti.android.popularmovies.utilities.NetworkUtils;

public class DetailActivity extends AppCompatActivity implements
        NetworkUtils.OnDownloadComplete,
        TrailerAdapter.TrailerAdapterOnClickHandler,
        ReviewAdapter.ReviewAdapterOnClickHandler{

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMovieApp";
    private static final String TAG = "DetailActivity";
    private static FavoriteMovieQueryHandler sQueryHandler;
    private static final int TOKEN_CHECK_IF_FAVORITE = 111;
    private static final int TOKEN_ADD_TO_FAVORITES = 222;
    private static final int TOKEN_REMOVE_FROM_FAVORITES = 333;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_movie_full_title)
    TextView mMovieFulltitleTextView;
    @BindView(R.id.tv_movie_release_date)
    TextView mMovieReleaseDateTextView;
    @BindView(R.id.tv_movie_languge)
    TextView mMovieOriginalLanguageTextView;
    @BindView(R.id.tv_movie_popularity)
    TextView mMoviePopularityTextView;
    @BindView(R.id.tv_movie_votes)
    TextView mMovieVotesTextView;
    @BindView(R.id.tv_movie_rating)
    TextView mMovieRatingTextView;
    @BindView(R.id.tv_movie_overview)
    TextView mMovieOverviewTextView;
    @BindView(R.id.iv_movie_backdrop)
    ImageView mMovieBackdropImageView;
    @BindView(R.id.iv_movie_poster)
    ImageView mMoviePosterImageView;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.favoriteButton)
    FloatingActionButton addToFavorites;
    @BindView(R.id.trailersRecyclerView)
    RecyclerView trailersRecyclerView;
    @BindView(R.id.reviewsRecyclerView)
    RecyclerView reviewsRecyclerView;

    private ArrayList<Trailer> mTrailersList;
    private ArrayList<Review> mReviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActivityCompat.postponeEnterTransition(this);
        int colorPalette;

        Intent intent = getIntent();
        if (intent != null) {
            GridLayoutManager gridLayoutManagerTrailers = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
            LinearLayoutManager gridLayoutManagerReviews = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            mTrailersList = new ArrayList<>();
            mTrailerAdapter = new TrailerAdapter(DetailActivity.this);
            //apply this layout manager to the recyclerview
            trailersRecyclerView.setLayoutManager(gridLayoutManagerTrailers);
            trailersRecyclerView.setHasFixedSize(true);
            /* attaching the adapter to the recyclerview */
            trailersRecyclerView.setAdapter(mTrailerAdapter);

            mReviewsList = new ArrayList<>();
            mReviewAdapter = new ReviewAdapter(DetailActivity.this);
            //apply this layout manager to the recyclerview
            reviewsRecyclerView.setLayoutManager(gridLayoutManagerReviews);
            reviewsRecyclerView.setHasFixedSize(true);
            //* attaching the adapter to the recyclerview *//*
            reviewsRecyclerView.setAdapter(mReviewAdapter);

            if (intent.hasExtra(getString(R.string.intent_color_integer)) && intent.getIntExtra(getString(R.string.intent_color_integer), 0) != 0) {
                colorPalette = intent.getIntExtra(getString(R.string.intent_color_integer), 0);
            } else {
                colorPalette = R.color.colorPrimary;
            }
            colorizeActivity(colorPalette);

            if (intent.hasExtra(getString(R.string.intent_movie_object))) {
                Movie movie = intent.getParcelableExtra(getString(R.string.intent_movie_object));

                initializeQueryHandler(movie);

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(movie.getOriginalTitle());
                }

                displayMovieData(movie);

                if(savedInstanceState != null &&
                        savedInstanceState.containsKey(getString(R.string.reviews_key)) &&
                        savedInstanceState.containsKey(getString(R.string.trailers_key))) {
                    Log.d(TAG, "onCreate: onsavedinstancestate exists");
                    mTrailersList = savedInstanceState.getParcelableArrayList(getString(R.string.trailers_key));
                    mTrailerAdapter.setTrailersList(mTrailersList);
                    mReviewsList = savedInstanceState.getParcelableArrayList(getString(R.string.reviews_key));
                    mReviewAdapter.setReviewsList(mReviewsList);
                } else {
                    Log.d(TAG, "onCreate: onsavedinstancestate doesnt exist");
                    loadTrailersFromServer(movie.getMovieId());
                    loadReviewsFromServer(movie.getMovieId());
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: starts");
        outState.putParcelableArrayList(getString(R.string.reviews_key), mReviewsList);
        outState.putParcelableArrayList(getString(R.string.trailers_key), mTrailersList);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ends");
    }

    private void initializeQueryHandler(final Movie localMovie) {
        sQueryHandler = new FavoriteMovieQueryHandler(this, new FavoriteMovieQueryHandler.AsyncQueryListener() {
            @Override
            public void onQueryComplete(int token, Object cookie, final Cursor cursor) {
                // handle query complete with given cursor
                if (token == TOKEN_CHECK_IF_FAVORITE) {
                    if (cursor != null && cursor.getCount() > 0) {
                        addToFavorites.setImageResource(R.drawable.ic_favorite);
                    } else {
                        addToFavorites.setImageResource(R.drawable.ic_not_favorite);
                    }
                    addToFavorites.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (cursor != null && cursor.getCount() > 0) {
                                removeMovieFromFavorites(localMovie);
                            } else {
                                addMovieToFavorites(localMovie);
                            }
                        }
                    });
                }
            }

            @Override
            public void onInsertComplete(int token, Object cookie, Uri uri) {
                if(token == TOKEN_ADD_TO_FAVORITES){
                    addToFavorites.setImageResource(R.drawable.ic_favorite);
                }
            }

            @Override
            public void onDeleteComplete(int token, Object cookie, int result) {
                if(token == TOKEN_REMOVE_FROM_FAVORITES){
                    addToFavorites.setImageResource(R.drawable.ic_not_favorite);
                }
            }
        });

        sQueryHandler.startQuery(
                TOKEN_CHECK_IF_FAVORITE,
                null,
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,   // projection
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?", // selection
                new String[]{Integer.toString(localMovie.getMovieId())},   // selectionArgs
                null    // sort order
        );
    }

    private void removeMovieFromFavorites(final Movie movie) {
        sQueryHandler.startDelete(TOKEN_REMOVE_FROM_FAVORITES,
                null,
                MoviesContract.MoviesEntry.CONTENT_URI,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{Integer.toString(movie.getMovieId())});
    }

    private void addMovieToFavorites(final Movie movie) {
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getMovieId());
        values.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE, movie.getOriginalTitle());
        values.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING, movie.getRating());
        values.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_BACKDROP, movie.getBackdropPath());
        values.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER, movie.getPosterPath());
        values.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        values.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY, movie.getPopularity());
        values.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_VOTES, movie.getVoteCount());
        values.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        values.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_LANGUAGE, movie.getOriginalLanguage());

        sQueryHandler.startInsert(TOKEN_ADD_TO_FAVORITES,
                null,
                MoviesContract.MoviesEntry.CONTENT_URI,
                values);
    }

    private void displayMovieData(Movie movie) {
        mMovieFulltitleTextView.setText(movie.getOriginalTitle());
        mMovieReleaseDateTextView.append(movie.getReleaseDate());
        mMovieOriginalLanguageTextView.append(movie.getOriginalLanguage());
        mMoviePopularityTextView.append(String.valueOf(movie.getPopularity()));
        mMovieVotesTextView.append(String.valueOf(movie.getVoteCount()));
        mMovieRatingTextView.append(String.valueOf(movie.getRating()) + getString(R.string.out_of_ten));
        mMovieOverviewTextView.setText(movie.getOverview());
        Picasso.with(this).load(movie.getBackdropPath()).into(mMovieBackdropImageView);
        Picasso.with(this).load(movie.getPosterPath()).into(mMoviePosterImageView, new Callback() {
            @Override
            public void onSuccess() {
                ActivityCompat.startPostponedEnterTransition(DetailActivity.this);
            }

            @Override
            public void onError() {
                ActivityCompat.startPostponedEnterTransition(DetailActivity.this);
                mMoviePosterImageView.setImageResource(R.mipmap.ic_launcher);
            }
        });
    }

    //get color from Palette API
    private void colorizeActivity(int colorPalette) {
        //set the 500 color to the collapsing toolbar
        collapsingToolbarLayout.setContentScrimColor(colorPalette);
        collapsingToolbarLayout.setBackgroundColor(colorPalette);
        //check if the version of the android is API 21 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*
             *convert the 500 color to 700 for the status bar and assign it to the status bar
             * hsv[0] : Hue (0 .. 360)
             * hsv[1] : Saturation (0...1)
             * hsv[2] : Value (0...1)
             */
            float[] hsv = new float[3];
            Color.colorToHSV(colorPalette, hsv);
            hsv[2] *= 0.7f;
            int colorPrimaryDark = Color.HSVToColor(hsv);
            getWindow().setStatusBarColor(colorPrimaryDark);
        }
    }

    private void loadTrailersFromServer(int movieId){
        if(NetworkUtils.checkInternetConnection(this)){
            URL trailerRequestUrl = NetworkUtils.buildTrailerUrl(String.valueOf(movieId));
            try {
                NetworkUtils.getResponseFromHttpUrl(trailerRequestUrl, this, NetworkUtils.GET_TRAILER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadReviewsFromServer(int movieId){
        if(NetworkUtils.checkInternetConnection(this)){
            URL reviewRequestUrl = NetworkUtils.buildReviewUrl(String.valueOf(movieId));
            try {
                NetworkUtils.getResponseFromHttpUrl(reviewRequestUrl, this, NetworkUtils.GET_REVIEW);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ShareCompat is used to create an intent for sharing the title of one movie
     *
     * @return The Intent to use to start our share.
     */
    private Intent createShareForecastIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(MOVIE_SHARE_HASHTAG + getString(R.string.hashTag) + mMovieFulltitleTextView.getText().toString())
                .getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDownloadComplete(String responseString, int requestCode) {
        Log.d(TAG, "onDownloadComplete: starts");
        try {
            if(requestCode == NetworkUtils.GET_TRAILER){
                ArrayList<Trailer> mTrailersLocal = MovieDBJsonUtils.getSimpleTrailerStringsFromJson(responseString);
                if (mTrailersLocal != null) {
                    mTrailersList.addAll(mTrailersLocal);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTrailerAdapter.setTrailersList(mTrailersList);
                        }
                    });
                }
            } else if(requestCode == NetworkUtils.GET_REVIEW){
                ArrayList<Review> mReviewsLocal = MovieDBJsonUtils.getSimpleReviewStringsFromJson(responseString);
                if (mReviewsLocal != null) {
                    mReviewsList.addAll(mReviewsLocal);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mReviewAdapter.setReviewsList(mReviewsList);
                        }
                    });
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onDownloadComplete: ends");
    }

    @Override
    public void onClick(Trailer selectedTrailer) {

    }

    @Override
    public void onClick(Review selectedReview) {

    }
}