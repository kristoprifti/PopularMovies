package me.kristoprifti.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database. This class is not necessary, but keeps
 * the code organized.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "me.kristoprifti.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //Table name for the database
        static final String TABLE_NAME = "movie";

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

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
