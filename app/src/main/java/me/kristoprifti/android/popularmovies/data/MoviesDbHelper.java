package me.kristoprifti.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.kristoprifti.android.popularmovies.data.MoviesContract.MoviesEntry;
import me.kristoprifti.android.popularmovies.data.ReviewsContract.ReviewsEntry;
import me.kristoprifti.android.popularmovies.data.TrailersContract.TrailersEntry;

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

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReviewsEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + ReviewsEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesEntry.TABLE_NAME + "(" + MoviesEntry.COLUMN_MOVIE_ID + "));";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailersEntry.TABLE_NAME + " (" +
                TrailersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrailersEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +
                TrailersEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL" +
                "FOREIGN KEY(" + TrailersEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesEntry.TABLE_NAME + "(" + MoviesEntry.COLUMN_MOVIE_ID + "));";
        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailersEntry.TABLE_NAME);
        onCreate(db);
    }
}
