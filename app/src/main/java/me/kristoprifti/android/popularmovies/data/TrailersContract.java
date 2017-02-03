package me.kristoprifti.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by k.prifti on 1.2.2017 г..
 */

public class TrailersContract {

    static final String CONTENT_AUTHORITY = "me.kristoprifti.android.popularmovies";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_TRAILER = "trailer";

    public static final class TrailersEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        //Table name for the database
        static final String TABLE_NAME = "trailer";

        //all the field names declared as static final strings below
        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_TRAILER_NAME = "name";
        public static final String COLUMN_TRAILER_KEY = "key";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
