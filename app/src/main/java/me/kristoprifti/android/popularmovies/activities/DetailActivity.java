package me.kristoprifti.android.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.kristoprifti.android.popularmovies.R;
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
    @BindView(R.id.mainWindow) CoordinatorLayout contentView;

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(getString(R.string.intent_movie_object))) {
                mMovie = intent.getParcelableExtra(getString(R.string.intent_movie_object));

                int colorPalette = 0;
                if(intent.hasExtra(getString(R.string.intent_color_integer))) {
                    colorPalette = intent.getIntExtra(getString(R.string.intent_color_integer), 0);
                }

                if(getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mMovie.getOriginalTitle());
                }

                if(colorPalette != 0) {
                    colorizeActivity(colorPalette);
                }

                mMovieFulltitleTextView.setText(mMovie.getOriginalTitle());
                mMovieReleaseDateTextView.append(mMovie.getReleaseDate());
                mMovieOriginalLanguageTextView.append(mMovie.getOriginalLanguage());
                mMoviePopularityTextView.append(String.valueOf(mMovie.getPopularity()));
                mMovieVotesTextView.append(String.valueOf(mMovie.getVoteCount()));
                mMovieRatingTextView.append(String.valueOf(mMovie.getRating()) + getString(R.string.out_of_ten));
                mMovieOverviewTextView.setText(mMovie.getOverview());
                Picasso.with(this).load(mMovie.getBackdropPath()).into(mMovieBackdropImageView);
                Picasso.with(this).load(mMovie.getPosterPath()).into(mMoviePosterImageView);
            }
        }
    }

    private void colorizeActivity(int colorPalette){
        contentView.setBackgroundColor(colorPalette);
    }

    /**
     * ShareCompat is used to create an intent for sharing the title of one movie
     *
     * @return The Intent to use to start our share.
     */
    private Intent createShareForecastIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(MOVIE_SHARE_HASHTAG + getString(R.string.hashTag) + mMovie.getOriginalTitle())
                .getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
        return true;
    }
}
