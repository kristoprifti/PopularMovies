package me.kristoprifti.android.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database. This class is not necessary, but keeps
 * the code organized.
 */
class MoviesContract {

    static final class MoviesEntry implements BaseColumns{

        //Table name for the database
        static final String TABLE_NAME = "movies";

        //all the field names declared as static final strings below
        static final String COLUMN_MOVIE_ID = "movie_id";
        static final String COLUMN_MOVIE_TITLE = "title";
        static final String COLUMN_MOVIE_POSTER = "poster";
        static final String COLUMN_MOVIE_BACKDROP = "backdrop";
        static final String COLUMN_MOVIE_OVERVIEW = "overview";
        static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        static final String COLUMN_MOVIE_RATING = "rating";
        static final String COLUMN_MOVIE_LANGUAGE = "language";
        static final String COLUMN_MOVIE_POPULARITY = "popularity";
        static final String COLUMN_MOVIE_VOTES = "votes";
    }
}
