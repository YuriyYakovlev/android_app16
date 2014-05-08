package com.yay.iloveua.core;

import java.lang.ref.WeakReference;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


/**
 * Slightly more abstract {@link AsyncQueryHandler} that helps keep a
 * {@link WeakReference} back to a listener. Will properly close any
 * {@link Cursor} if the listener ceases to exist.
 * <p>
 * This pattern can be used to perform background queries without leaking
 * {@link Context} objects.
 *
 * @hide pending API council review
 */
public class NotifyingAsyncQueryHandler extends AsyncQueryHandler {
    private WeakReference<AsyncQueryListener> mQueryListener;
    private WeakReference<AsyncUpdateListener> mUpdateListener;

    /**
     * Interface to listen for completed query operations.
     */
    public interface AsyncQueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
    }
    
    /**
     * Interface to listen for completed update operations.
     */
    public interface AsyncUpdateListener {
       	void onUpdateComplete(int token, Object cookie, int j);
    }
    

    public NotifyingAsyncQueryHandler(ContentResolver resolver, AsyncQueryListener listener) {
    	super(resolver);
    	setQueryListener(listener);
    }
   
    public NotifyingAsyncQueryHandler(ContentResolver resolver, AsyncQueryListener listener, AsyncUpdateListener listener2) {
        super(resolver);
        setQueryListener(listener);
        setUpdateListener(listener2);
    }
    
    /**
     * Assign the given {@link AsyncQueryListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setQueryListener(AsyncQueryListener listener) {
    	mQueryListener = new WeakReference<AsyncQueryListener>(listener);
    }

    /**
     * Clear any {@link AsyncQueryListener} set through
     * {@link #setQueryListener(AsyncQueryListener)}
     */
    public void clearQueryListener() {
    	mQueryListener = null;
    }

    /**
     * Begin an asynchronous query with the given arguments. When finished,
     * {@link AsyncQueryListener#onQueryComplete(int, Object, Cursor)} is
     * called if a valid {@link AsyncQueryListener} is present.
     */
    public void startQuery(Uri uri, String[] projection) {
        startQuery(-1, null, uri, projection, null, null, null);
    }

    /**
     * Begin an asynchronous query with the given arguments. When finished,
     * {@link AsyncQueryListener#onQueryComplete(int, Object, Cursor)} is called
     * if a valid {@link AsyncQueryListener} is present.
     *
     * @param token Unique identifier passed through to
     *            {@link AsyncQueryListener#onQueryComplete(int, Object, Cursor)}
     */
    public void startQuery(int token, Uri uri, String[] projection) {
        startQuery(token, null, uri, projection, null, null, null);
    }

    /**
     * Begin an asynchronous query with the given arguments. When finished,
     * {@link AsyncQueryListener#onQueryComplete(int, Object, Cursor)} is called
     * if a valid {@link AsyncQueryListener} is present.
     */
    public void startQuery(int token, Uri uri, String[] projection, String sortOrder) {
        startQuery(token, null, uri, projection, null, null, sortOrder);
    }

    /**
     * Begin an asynchronous query with the given arguments. When finished,
     * {@link AsyncQueryListener#onQueryComplete(int, Object, Cursor)} is called
     * if a valid {@link AsyncQueryListener} is present.
     */
    public void startQuery(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String orderBy) {
        startQuery(-1, null, uri, projection, selection, selectionArgs, orderBy);
    }

    
    /**
     * Assign the given {@link AsyncUpdateListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setUpdateListener(AsyncUpdateListener listener) {
    	mUpdateListener = new WeakReference<AsyncUpdateListener>(listener);
    }

    /**
     * Clear any {@link AsyncQueryListener} set through
     * {@link #setQueryListener(AsyncQueryListener)}
     */
    public void clearUpdateListener() {
    	mUpdateListener = null;
    }
    
    /**
     * Begin an asynchronous update with the given arguments.
     */
    public void startUpdate(Uri uri, ContentValues values) {
        startUpdate(-1, null, uri, values, null, null);
    }
    
    public void startUpdate(int token, Uri uri, ContentValues values) {
        startUpdate(token, null, uri, values, null, null);
    }

    public void startInsert(Uri uri, ContentValues values) {
        startInsert(-1, null, uri, values);
    }

    public void startInsert(int token, Uri uri, ContentValues values) {
        startInsert(token, null, uri, values);
    }

    public void startDelete(Uri uri) {
        startDelete(-1, null, uri, null, null);
    }

    public void startDelete(int token, Uri uri) {
        startDelete(token, null, uri, null, null);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        final AsyncQueryListener listener = mQueryListener == null ? null : mQueryListener.get();
        if (listener != null) {
            listener.onQueryComplete(token, cookie, cursor);
        } else if (cursor != null) {
            cursor.close();
        }
    }
    /** {@inheritDoc} */
    @Override
    protected void onUpdateComplete(int token, Object cookie, int j) {
        final AsyncUpdateListener listener = mUpdateListener == null ? null : mUpdateListener.get();
        if (listener != null) {
            listener.onUpdateComplete(token, cookie, j);
        }
    }
}
