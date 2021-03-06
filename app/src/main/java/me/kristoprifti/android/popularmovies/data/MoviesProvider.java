package me.kristoprifti.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by k.prifti on 24.1.2017 г..
 */

public class MoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int CODE_MOVIE = 100;
    static final int CODE_TRAILER = 101;
    static final int CODE_REVIEW = 102;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIE, CODE_MOVIE);
        matcher.addURI(authority, TrailersContract.PATH_TRAILER, CODE_TRAILER);
        matcher.addURI(authority, ReviewsContract.PATH_REVIEW, CODE_REVIEW);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CODE_TRAILER:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrailersContract.TrailersEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CODE_REVIEW:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ReviewsContract.ReviewsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(getContext() != null){
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_MOVIE:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case CODE_TRAILER:
                return TrailersContract.TrailersEntry.CONTENT_TYPE;
            case CODE_REVIEW:
                return ReviewsContract.ReviewsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Uri returnUri;
        long _id;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MoviesContract.MoviesEntry.buildMovieUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case CODE_TRAILER:
                _id = db.insert(TrailersContract.TrailersEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = TrailersContract.TrailersEntry.buildTrailerUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case CODE_REVIEW:
                _id = db.insert(ReviewsContract.ReviewsEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ReviewsContract.ReviewsEntry.buildReviewUri(_id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_TRAILER:
                rowsDeleted = db.delete(
                        TrailersContract.TrailersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_REVIEW:
                rowsDeleted = db.delete(
                        ReviewsContract.ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                rowsUpdated = db.update(MoviesContract.MoviesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CODE_TRAILER:
                rowsUpdated = db.update(TrailersContract.TrailersEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CODE_REVIEW:
                rowsUpdated = db.update(ReviewsContract.ReviewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
