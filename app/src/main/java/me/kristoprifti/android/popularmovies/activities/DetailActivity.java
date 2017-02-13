package me.kristoprifti.android.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.kristoprifti.android.popularmovies.R;
import me.kristoprifti.android.popularmovies.fragments.DetailActivityFragment;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(getString(R.string.intent_movie_object),
                    getIntent().getParcelableExtra(getString(R.string.intent_movie_object)));

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }
}