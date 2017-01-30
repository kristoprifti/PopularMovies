package me.kristoprifti.android.popularmovies.handlers;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;

import java.lang.ref.WeakReference;

/**
 * Created by k.prifti on 30.1.2017 г..
 */

public class FavoriteMovieQueryHandler  extends AsyncQueryHandler {
    private WeakReference<AsyncQueryListener> mListener;

    /**
     * Interface to listen for completed query operations.
     */
    public interface AsyncQueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
    }

    public FavoriteMovieQueryHandler(Context context, AsyncQueryListener listener) {
        super(context.getContentResolver());
        setQueryListener(listener);
    }

    /**
     * Assign the given {@link AsyncQueryListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    private void setQueryListener(AsyncQueryListener listener) {
        mListener = new WeakReference<>(listener);
    }

    /** {@inheritDoc} */
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        final AsyncQueryListener listener = mListener.get();
        if (listener != null) {
            listener.onQueryComplete(token, cookie, cursor);
        } else if (cursor != null) {
            cursor.close();
        }
    }
}