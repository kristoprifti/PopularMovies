package me.kristoprifti.android.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
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
import me.kristoprifti.android.popularmovies.data.ReviewsContract;
import me.kristoprifti.android.popularmovies.data.TrailersContract;
import me.kristoprifti.android.popularmovies.handlers.FavoriteMovieQueryHandler;
import me.kristoprifti.android.popularmovies.models.Movie;
import me.kristoprifti.android.popularmovies.models.Review;
import me.kristoprifti.android.popularmovies.models.Trailer;
import me.kristoprifti.android.popularmovies.utilities.MovieDBJsonUtils;
import me.kristoprifti.android.popularmovies.utilities.NetworkUtils;

/**
 * Created by k.prifti on 10.2.2017 г..
 */

public class DetailActivityFragment extends Fragment implements
        NetworkUtils.OnDownloadComplete,
        TrailerAdapter.TrailerAdapterOnClickHandler,
        ReviewAdapter.ReviewAdapterOnClickHandler{

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMovieApp";
    public static final String TAG = "DetailActivityFragment";

    private static FavoriteMovieQueryHandler sMovieQueryHandler;

    private static final int TOKEN_CHECK_IF_FAVORITE = 111;
    private static final int TRAILER_LOADER_ID = 222;
    private static final int REVIEW_LOADER_ID = 333;

    private static final int TOKEN_ADD_MOVIE_TO_FAVORITES = 200;
    private static final int TOKEN_REMOVE_MOVIE_FROM_FAVORITES = 300;

    private static final int TOKEN_ADD_TRAILER_TO_FAVORITES = 201;
    private static final int TOKEN_REMOVE_TRAILER_FROM_FAVORITES = 301;

    private static final int TOKEN_ADD_REVIEW_TO_FAVORITES = 202;
    private static final int TOKEN_REMOVE_REVIEW_FROM_FAVORITES = 302;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

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

    private boolean isFavorite = false;
    private int colorPalette;
    private Movie movie;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(getString(R.string.intent_movie_object));
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ButterKnife.bind(this, rootView);

        if(movie != null){
            //ActivityCompat.postponeEnterTransition(getActivity());

            GridLayoutManager gridLayoutManagerTrailers = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
            LinearLayoutManager gridLayoutManagerReviews = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

            mTrailersList = new ArrayList<>();
            mTrailerAdapter = new TrailerAdapter(this);
            //apply this layout manager to the recyclerview
            trailersRecyclerView.setLayoutManager(gridLayoutManagerTrailers);
            trailersRecyclerView.setHasFixedSize(true);
            /* attaching the adapter to the recyclerview */
            trailersRecyclerView.setAdapter(mTrailerAdapter);

            mReviewsList = new ArrayList<>();
            mReviewAdapter = new ReviewAdapter(this);
            //apply this layout manager to the recyclerview
            reviewsRecyclerView.setLayoutManager(gridLayoutManagerReviews);
            reviewsRecyclerView.setHasFixedSize(true);
            //* attaching the adapter to the recyclerview *//*
            reviewsRecyclerView.setAdapter(mReviewAdapter);

            displayMovieData(movie);
            initializeQueryHandlers(movie);

            if(savedInstanceState != null &&
                    savedInstanceState.containsKey(getString(R.string.reviews_key)) &&
                    savedInstanceState.containsKey(getString(R.string.trailers_key))) {
                Log.d(TAG, "onCreate: onsavedinstancestate exists");
                mTrailersList = savedInstanceState.getParcelableArrayList(getString(R.string.trailers_key));
                mTrailerAdapter.setTrailersList(mTrailersList);
                mReviewsList = savedInstanceState.getParcelableArrayList(getString(R.string.reviews_key));
                mReviewAdapter.setReviewsList(mReviewsList);
                if(savedInstanceState.getBoolean(getString(R.string.fab_favorite))){
                    addToFavorites.setImageResource(R.drawable.ic_favorite);
                    isFavorite = true;
                } else {
                    addToFavorites.setImageResource(R.drawable.ic_not_favorite);
                    isFavorite = false;
                }
                addToFavorites.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFavorite) {
                            removeMovieFromFavorites(movie);
                        } else {
                            addMovieToFavorites(movie, mTrailersList, mReviewsList);
                        }
                    }
                });
            } else {
                checkForFavorites(movie);
                Log.d(TAG, "onCreate: onsavedinstancestate doesnt exist");
            }
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                getActivity().supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: starts");
        outState.putParcelableArrayList(getString(R.string.reviews_key), mReviewsList);
        outState.putParcelableArrayList(getString(R.string.trailers_key), mTrailersList);
        outState.putBoolean(getString(R.string.fab_favorite), isFavorite);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ends");
    }

    private void initializeQueryHandlers(final Movie localMovie) {
        sMovieQueryHandler = new FavoriteMovieQueryHandler(getActivity(), new FavoriteMovieQueryHandler.AsyncQueryListener() {
            @Override
            public void onQueryComplete(int token, Object cookie, final Cursor cursor) {
                // handle query complete with given cursor
                switch (token){
                    case TOKEN_CHECK_IF_FAVORITE:
                        if (cursor != null && cursor.getCount() > 0) {
                            Log.d(TAG, "onQueryComplete: matching records:" + cursor.getCount());
                            addToFavorites.setImageResource(R.drawable.ic_favorite);
                            isFavorite = true;
                            Log.d(TAG, "onQueryComplete: start trailer query handler");
                            sMovieQueryHandler.startQuery(
                                    TRAILER_LOADER_ID,
                                    null,
                                    TrailersContract.TrailersEntry.CONTENT_URI,
                                    null,   // projection
                                    TrailersContract.TrailersEntry.COLUMN_MOVIE_ID + " = ?", // selection
                                    new String[]{Integer.toString(localMovie.getMovieId())},   // selectionArgs
                                    null    // sort order
                            );
                            Log.d(TAG, "onQueryComplete: start review query handler");
                            sMovieQueryHandler.startQuery(
                                    REVIEW_LOADER_ID,
                                    null,
                                    ReviewsContract.ReviewsEntry.CONTENT_URI,
                                    null,   // projection
                                    ReviewsContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ?", // selection
                                    new String[]{Integer.toString(localMovie.getMovieId())},   // selectionArgs
                                    null    // sort order
                            );
                        } else {
                            addToFavorites.setImageResource(R.drawable.ic_not_favorite);
                            loadTrailersFromServer(localMovie.getMovieId());
                            loadReviewsFromServer(localMovie.getMovieId());
                            isFavorite = false;
                        }
                        addToFavorites.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isFavorite) {
                                    removeMovieFromFavorites(localMovie);
                                } else {
                                    addMovieToFavorites(localMovie, mTrailersList, mReviewsList);
                                }
                            }
                        });
                        break;
                    case TRAILER_LOADER_ID:
                        if (cursor != null && cursor.moveToFirst()) {
                            Log.d(TAG, "onQueryComplete: there are trailers");
                            do {
                                Trailer trailer = new Trailer(cursor);
                                mTrailersList.add(trailer);
                            } while (cursor.moveToNext());
                            cursor.close();
                            mTrailerAdapter.setTrailersList(mTrailersList);
                        }
                        break;
                    case REVIEW_LOADER_ID:
                        if (cursor != null && cursor.moveToFirst()) {
                            Log.d(TAG, "onQueryComplete: there are reviews");
                            do {
                                Review review = new Review(cursor);
                                mReviewsList.add(review);
                            } while (cursor.moveToNext());
                            cursor.close();
                            mReviewAdapter.setReviewsList(mReviewsList);
                        }
                        break;
                    default:
                }
            }

            @Override
            public void onInsertComplete(int token, Object cookie, Uri uri) {
                switch (token){
                    case TOKEN_ADD_MOVIE_TO_FAVORITES:
                        addToFavorites.setImageResource(R.drawable.ic_favorite);
                        isFavorite = true;
                        break;
                    case TOKEN_ADD_TRAILER_TO_FAVORITES:
                        Log.d(TAG, "onInsertComplete: Trailers added in Favorites");
                        break;
                    case TOKEN_ADD_REVIEW_TO_FAVORITES:
                        Log.d(TAG, "onInsertComplete: Reviews added from favorites");
                        break;
                    default:
                }
            }

            @Override
            public void onDeleteComplete(int token, Object cookie, int result) {
                switch (token){
                    case TOKEN_REMOVE_MOVIE_FROM_FAVORITES:
                        addToFavorites.setImageResource(R.drawable.ic_not_favorite);
                        isFavorite = false;
                        break;
                    case TOKEN_REMOVE_TRAILER_FROM_FAVORITES:
                        Log.d(TAG, "onInsertComplete: Trailers removed from favorites");
                        break;
                    case TOKEN_REMOVE_REVIEW_FROM_FAVORITES:
                        Log.d(TAG, "onInsertComplete: Reviews removed from favorites");
                        break;
                    default:
                }
            }
        });
    }

    private void checkForFavorites(Movie localMovie){
        Log.d(TAG, "checkForFavorites: movie id:" + localMovie.getMovieId());
        sMovieQueryHandler.startQuery(
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
        sMovieQueryHandler.startDelete(TOKEN_REMOVE_MOVIE_FROM_FAVORITES,
                null,
                MoviesContract.MoviesEntry.CONTENT_URI,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{Integer.toString(movie.getMovieId())});

        sMovieQueryHandler.startDelete(TOKEN_REMOVE_TRAILER_FROM_FAVORITES,
                null,
                TrailersContract.TrailersEntry.CONTENT_URI,
                TrailersContract.TrailersEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{Integer.toString(movie.getMovieId())});

        sMovieQueryHandler.startDelete(TOKEN_REMOVE_REVIEW_FROM_FAVORITES,
                null,
                ReviewsContract.ReviewsEntry.CONTENT_URI,
                ReviewsContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{Integer.toString(movie.getMovieId())});
    }

    private void addMovieToFavorites(final Movie movie, final ArrayList<Trailer> trailersList, final ArrayList<Review> reviewsList) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getMovieId());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE, movie.getOriginalTitle());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING, movie.getRating());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_BACKDROP, movie.getBackdropPath());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER, movie.getPosterPath());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POPULARITY, movie.getPopularity());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_VOTES, movie.getVoteCount());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_LANGUAGE, movie.getOriginalLanguage());

        sMovieQueryHandler.startInsert(TOKEN_ADD_MOVIE_TO_FAVORITES,
                null,
                MoviesContract.MoviesEntry.CONTENT_URI,
                movieValues);

        for (int i = 0; i < trailersList.size(); i++) {
            ContentValues trailerValues = new ContentValues();
            trailerValues.put(TrailersContract.TrailersEntry.COLUMN_TRAILER_ID, trailersList.get(i).getTrailerId());
            trailerValues.put(TrailersContract.TrailersEntry.COLUMN_TRAILER_NAME, trailersList.get(i).getTrailerName());
            trailerValues.put(TrailersContract.TrailersEntry.COLUMN_TRAILER_KEY, trailersList.get(i).getTrailerKey());
            trailerValues.put(TrailersContract.TrailersEntry.COLUMN_MOVIE_ID, movie.getMovieId());

            sMovieQueryHandler.startInsert(TOKEN_ADD_TRAILER_TO_FAVORITES,
                    null,
                    TrailersContract.TrailersEntry.CONTENT_URI,
                    trailerValues);
        }

        for (int i = 0; i < reviewsList.size(); i++) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(ReviewsContract.ReviewsEntry.COLUMN_REVIEW_ID, reviewsList.get(i).getReviewId());
            reviewValues.put(ReviewsContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, reviewsList.get(i).getReviewAuthor());
            reviewValues.put(ReviewsContract.ReviewsEntry.COLUMN_REVIEW_CONTENT, reviewsList.get(i).getReviewContent());
            reviewValues.put(ReviewsContract.ReviewsEntry.COLUMN_MOVIE_ID, movie.getMovieId());

            sMovieQueryHandler.startInsert(TOKEN_ADD_REVIEW_TO_FAVORITES,
                    null,
                    ReviewsContract.ReviewsEntry.CONTENT_URI,
                    reviewValues);
        }
    }

    private void displayMovieData(Movie movie) {
        mMovieFulltitleTextView.setText(movie.getOriginalTitle());
        mMovieReleaseDateTextView.append(movie.getReleaseDate());
        mMovieOriginalLanguageTextView.append(movie.getOriginalLanguage());
        mMoviePopularityTextView.append(String.valueOf(movie.getPopularity()));
        mMovieVotesTextView.append(String.valueOf(movie.getVoteCount()));
        mMovieRatingTextView.append(String.valueOf(movie.getRating()) + getString(R.string.out_of_ten));
        mMovieOverviewTextView.setText(movie.getOverview());
        Picasso.with(getActivity()).load(movie.getBackdropPath()).into(mMovieBackdropImageView);
        Picasso.with(getActivity()).load(movie.getPosterPath()).into(mMoviePosterImageView, new Callback() {
            @Override
            public void onSuccess() {
                BitmapDrawable drawable = (BitmapDrawable) mMoviePosterImageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onGenerated(Palette palette) {
                        //work with the palette here
                        if(palette.getDarkVibrantColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary)) != 0) {
                            colorPalette = palette.getDarkVibrantColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                        } else {
                            colorPalette = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
                        }
                        colorizeActivity(colorPalette);
                    }
                });
                ActivityCompat.startPostponedEnterTransition(getActivity());
            }

            @Override
            public void onError() {
                colorPalette = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
                colorizeActivity(colorPalette);
                ActivityCompat.startPostponedEnterTransition(getActivity());
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
            getActivity().getWindow().setStatusBarColor(colorPrimaryDark);
        }
    }

    private void loadTrailersFromServer(int movieId){
        if(NetworkUtils.checkInternetConnection(getActivity())){
            URL trailerRequestUrl = NetworkUtils.buildTrailerUrl(String.valueOf(movieId));
            try {
                NetworkUtils.getResponseFromHttpUrl(trailerRequestUrl, this, NetworkUtils.GET_TRAILER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadReviewsFromServer(int movieId){
        if(NetworkUtils.checkInternetConnection(getActivity())){
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
        return ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText(MOVIE_SHARE_HASHTAG + getString(R.string.hashTag) + mMovieFulltitleTextView.getText().toString())
                .getIntent();
    }

    @Override
    public void onDownloadComplete(String responseString, int requestCode) {
        Log.d(TAG, "onDownloadComplete: starts");
        try {
            if(requestCode == NetworkUtils.GET_TRAILER){
                ArrayList<Trailer> mTrailersLocal = MovieDBJsonUtils.getSimpleTrailerStringsFromJson(responseString);
                if (mTrailersLocal != null) {
                    mTrailersList.addAll(mTrailersLocal);
                    getActivity().runOnUiThread(new Runnable() {
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
                    getActivity().runOnUiThread(new Runnable() {
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
    public void onClick(Trailer selectedTrailer, boolean canPlayVideo) {
        if(canPlayVideo) {
            Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                    NetworkUtils.YOUTUBE_API_KEY,
                    selectedTrailer.getTrailerKey(),//video id
                    100,     //after this time, video will start automatically
                    true,    //autoplay or not
                    false);  //lightbox mode or not; show the video in a small box
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Please install Youtube to play this video!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(Review selectedReview) {

    }
}
