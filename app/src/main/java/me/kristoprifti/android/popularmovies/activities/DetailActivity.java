package me.kristoprifti.android.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.kristoprifti.android.popularmovies.R;
import me.kristoprifti.android.popularmovies.models.Movie;

public class DetailActivity extends AppCompatActivity {

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMovieApp";

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView mMovieFulltitleTextView = (TextView) findViewById(R.id.tv_movie_full_title);
        TextView mMovieReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_release_date);
        TextView mMovieOriginalLanguageTextView = (TextView) findViewById(R.id.tv_movie_languge);
        TextView mMoviePopularityTextView = (TextView) findViewById(R.id.tv_movie_popularity);
        TextView mMovieVotesTextView = (TextView) findViewById(R.id.tv_movie_votes);
        TextView mMovieRatingTextView = (TextView) findViewById(R.id.tv_movie_rating);
        TextView mMovieOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);
        ImageView mMovieBackdropImageView = (ImageView) findViewById(R.id.iv_movie_backdrop);
        ImageView mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);
        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                mMovie = intent.getParcelableExtra(Intent.EXTRA_TEXT);

                if(getSupportActionBar() != null)
                    getSupportActionBar().setTitle(mMovie.getOriginalTitle());

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

    /**
     * Uses the ShareCompat Intent builder to create our Movie intent for sharing. We set the
     * type of content that we are sharing (just regular text), the text itself, and we return the
     * newly created Intent.
     *
     * @return The Intent to use to start our share.
     */
    private Intent createShareForecastIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mMovie.getOriginalTitle() + MOVIE_SHARE_HASHTAG)
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
