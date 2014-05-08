package com.yay.iloveua.provider;

import java.io.FileNotFoundException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.yay.iloveua.Config;
import com.yay.iloveua.core.DatabaseController;
import com.yay.iloveua.core.SelectionBuilder;
import com.yay.iloveua.core.ServiceLocator;
import com.yay.iloveua.models.Event;
import com.yay.iloveua.models.Question;
import com.yay.iloveua.provider.QuizContract.Tables;



//
public class DataProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int QUESTION = 100;
    private static final int QUESTION_ID = 200;
    private static final int QUESTION_IDP = 300;
    private static final int QUESTION_SEARCH = 400;

    private static final int EVENT = 500;
    private static final int EVENT_ID = 600;
    private static final int EVENT_DATE = 700;
    private static final int EVENT_SEARCH = 800;


    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = QuizContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, Tables.QUESTION, QUESTION);
        matcher.addURI(authority, Tables.QUESTION+"/"+QuestionColumns._ID.getName()+"/*", QUESTION_ID);
        matcher.addURI(authority, Tables.QUESTION+"/"+QuestionColumns.IDP.getName()+"/*", QUESTION_IDP);
        matcher.addURI(authority, Tables.QUESTION+"/"+Question.SEARCH_URI+"/*", QUESTION_SEARCH);

        matcher.addURI(authority, Tables.EVENT, EVENT);
        matcher.addURI(authority, Tables.EVENT+"/"+EventColumns._ID.getName()+"/*", EVENT_ID);
        matcher.addURI(authority, Tables.EVENT+"/"+EventColumns.DATE.getName()+"/*", EVENT_DATE);
        matcher.addURI(authority, Tables.EVENT+"/"+ Event.SEARCH_URI+"/*", EVENT_SEARCH);
 
        return matcher;
    }

    @Override
    public boolean onCreate() {
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case QUESTION:
                return Question.CONTENT_TYPE;
            case QUESTION_ID:
                return Question.CONTENT_TYPE;
            case QUESTION_IDP:
                return Question.CONTENT_TYPE;
            case QUESTION_SEARCH:
                return Question.CONTENT_TYPE;
            case EVENT:
                return Event.CONTENT_TYPE;
            case EVENT_ID:
                return Event.CONTENT_TYPE;
            case EVENT_DATE:
                return Event.CONTENT_TYPE;
            case EVENT_SEARCH:
                return Event.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = getReadableDatabase();

        final int match = sUriMatcher.match(uri);
        final SelectionBuilder builder = buildExpandedSelection(uri, match);
        return builder.where(selection, selectionArgs).query(db, projection, sortOrder);	// SelectionBuilder[table=ZQUESTION, selection=(ZDIFFICULTY=1), selectionArgs=[ZFLAG=0]]
    }

    /** {@inheritDoc} */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return uri;
    }

    /** {@inheritDoc} */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(Config.LOG_TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        return builder.where(selection, selectionArgs).update(db, values);
    }

    /** {@inheritDoc} */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        return builder.where(selection, selectionArgs).delete(db);
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
//
    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support {@link #insert},
     * {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
	        case QUESTION: {
	        	return builder.table(Tables.QUESTION);
            }
	        case QUESTION_ID: {
	        	final String id = Uri.decode(Question.getSearchQuery(uri));
	        	return builder.table(Tables.QUESTION).where(QuestionColumns._ID.getName() + "=" + id);
            }
	        case QUESTION_IDP: {
	        	final String idp = Uri.decode(Question.getSearchQuery(uri));
	        	return builder.table(Tables.QUESTION).where(QuestionColumns.IDP.getName() + "=" + idp);
            }
	        case QUESTION_SEARCH: {
	        	String query = Uri.decode(Question.getSearchQuery(uri));
	        	if(query != null && query.length() > 0) {
	        		String c = query.substring(0, 1);
	        		query = c.toUpperCase() + query.substring(1, query.length());
	        	}
	        	return builder.table(Tables.QUESTION).where(QuestionColumns.QUESTION.getName() + " like '" + query + "%'");
            }

            case EVENT: {
                return builder.table(Tables.EVENT);
            }
            case EVENT_ID: {
                final String id = Uri.decode(Event.getSearchQuery(uri));
                return builder.table(Tables.EVENT).where(EventColumns._ID.getName() + "=" + id);
            }
            case EVENT_DATE: {
                final String date = Uri.decode(Event.getSearchQuery(uri));
                return builder.table(Tables.EVENT).where(EventColumns.DATE.getName() + "=" + date);
            }
            case EVENT_SEARCH: {
                String query = Uri.decode(Event.getSearchQuery(uri));
                if(query != null && query.length() > 0) {
                    String c = query.substring(0, 1);
                    query = c.toUpperCase() + query.substring(1, query.length());
                }
                return builder.table(Tables.EVENT).where(EventColumns.NAME.getName() + " like '" + query +
                        "%'");
            }
	        default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * Build an advanced {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually only used by {@link #query}, since it
     * performs table joins useful for {@link Cursor} data.
     */
    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
	        case QUESTION: {
	        	return builder.table(Tables.QUESTION);
	        }
	        case QUESTION_ID: {
	        	final String id = Uri.decode(Question.getSearchQuery(uri));
	        	return builder.table(Tables.QUESTION).where(QuestionColumns._ID.getName() + "=" + id);
	        }
	        case QUESTION_IDP: {
	        	final String idp = Uri.decode(Question.getSearchQuery(uri));
	        	return builder.table(Tables.QUESTION).where(QuestionColumns.IDP.getName() + "=" + idp);
	        }
	        case QUESTION_SEARCH: {
	        	String query = Uri.decode(Question.getSearchQuery(uri));
	        	if(query != null && query.length() > 0) {
	        		String c = query.substring(0, 1);
	        		query = c.toUpperCase() + query.substring(1, query.length());
	        	}
	        	return builder.table(Tables.QUESTION).where(QuestionColumns.QUESTION.getName() + " like '" + query + "%'");
	        }
            case EVENT: {
                return builder.table(Tables.EVENT);
            }
            case EVENT_ID: {
                final String id = Uri.decode(Event.getSearchQuery(uri));
                return builder.table(Tables.EVENT).where(EventColumns._ID.getName() + "=" + id);
            }
            case EVENT_DATE: {
                final String date = Uri.decode(Event.getSearchQuery(uri));
                return builder.table(Tables.EVENT).where(EventColumns.DATE.getName() + "=" + date);
            }
            case EVENT_SEARCH: {
                String query = Uri.decode(Event.getSearchQuery(uri));
                if(query != null && query.length() > 0) {
                    String c = query.substring(0, 1);
                    query = c.toUpperCase() + query.substring(1, query.length());
                }
                return builder.table(Tables.EVENT).where(EventColumns.NAME.getName() + " like '" + query +
                        "%'");
            }

	        default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
    
    private SQLiteDatabase getReadableDatabase() {
    	DatabaseController dbController = ServiceLocator.getInstance(getContext()).getService(DatabaseController.class);
    	return dbController.getReadableDatabase();
    }
    
    private SQLiteDatabase getWritableDatabase() {
    	DatabaseController dbController = ServiceLocator.getInstance(getContext()).getService(DatabaseController.class);
    	return dbController.getWritableDatabase();
    }
  
}
