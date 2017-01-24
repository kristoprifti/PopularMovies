package me.kristoprifti.android.popularmovies.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kristoprifti.android.popularmovies.R;
import me.kristoprifti.android.popularmovies.data.MoviesContract;
import me.kristoprifti.android.popularmovies.models.Movie;

public class DetailActivity extends AppCompatActivity {

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMovieApp";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_movie_full_title) TextView mMovieFulltitleTextView;
    @BindView(R.id.tv_movie_release_date) TextView mMovieReleaseDateTextView;
    @BindView(R.id.tv_movie_languge) TextView mMovieOriginalLanguageTextView;
    @BindView(R.id.tv_movie_popularity) TextView mMoviePopularityTextView;
    @BindView(R.id.tv_movie_votes) TextView mMovieVotesTextView;
    @BindView(R.id.tv_movie_rating) TextView mMovieRatingTextView;
    @BindView(R.id.tv_movie_overview) TextView mMovieOverviewTextView;
    @BindView(R.id.iv_movie_backdrop) ImageView mMovieBackdropImageView;
    @BindView(R.id.iv_movie_poster) ImageView mMoviePosterImageView;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.favoriteButton) FloatingActionButton addToFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActivityCompat.postponeEnterTransition(this);
        int colorPalette;

        Intent intent = getIntent();
        if (intent != null) {
            if(intent.hasExtra(getString(R.string.intent_color_integer)) && intent.getIntExtra(getString(R.string.intent_color_integer), 0) != 0) {
                colorPalette = intent.getIntExtra(getString(R.string.intent_color_integer), 0);
            } else {
                colorPalette = R.color.colorPrimary;
            }
            colorizeActivity(colorPalette);

            if (intent.hasExtra(getString(R.string.intent_movie_object))) {
                final Movie movie = intent.getParcelableExtra(getString(R.string.intent_movie_object));

                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(movie.getOriginalTitle());
                }

                displayMovieData(movie);

                addToFavorites.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addMovieToFavorites(movie);
                    }
                });
            }
        }
    }

    private void addMovieToFavorites(final Movie movie) {
        new AsyncTask<Void, Void, Uri>() {
            @Override
            protected Uri doInBackground(Void... params) {
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

                return getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI,
                        values);
            }

            @Override
            protected void onPostExecute(Uri returnUri) {
                //add.setIcon(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                Toast.makeText(DetailActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        }.execute();
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

            }
        });
    }

    //get color from Palette API
    private void colorizeActivity(int colorPalette){
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
}
