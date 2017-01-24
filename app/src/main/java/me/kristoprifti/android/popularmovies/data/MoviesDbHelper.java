package me.kristoprifti.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry;

/**
 * Manages a local database for movie data.
 */
class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our movie data.
         */
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_BACKDROP + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_RELEASE_DATE + " INTEGER NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_LANGUAGE + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_POPULARITY + " REAL NOT NULL, " +
                MoviesEntry.COLUMN_MOVIE_VOTES + " INTEGER NOT NULL" + ");";
        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
