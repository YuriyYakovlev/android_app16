package com.yay.iloveua.models;

import android.database.Cursor;
import android.net.Uri;
import com.yay.iloveua.provider.EventColumns;
import com.yay.iloveua.provider.EventQuery;
import com.yay.iloveua.provider.QuizContract;


public class Event implements QuizContract {
	public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(Tables.EVENT).build();
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.interview.event";
    private int id;
    private long date;
    private String name;
    private String description;
    private String image;
    private String link;


    public Event() {
    	
    }
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
	
	public static Uri getContentUri() {
		return CONTENT_URI;
	}

	public static String getContentType() {
		return CONTENT_TYPE;
	}

	public static String getDefaultSort() {
		return DEFAULT_SORT;
	}

	/** Default "ORDER BY" clause. */
    public static final String DEFAULT_SORT = EventColumns._ID.getName() + " ASC";

    public static Uri buildEventUri(String id) {
        return CONTENT_URI.buildUpon().appendPath(EventColumns._ID.getName()).appendPath(id).build();
    }

    public static Uri buildEventsUri() {
        return CONTENT_URI.buildUpon().build();
    }

    public static Uri buildSearchUri(String query) {
        return CONTENT_URI.buildUpon().appendPath(SEARCH_URI).appendPath(query).build();
    }

    public static String getSearchQuery(Uri uri) {
        return uri.getPathSegments().get(2);
    }

    public static Event buildFromCursor(Cursor cursor) {
    	Event event = new Event();
        event.setId(cursor.getInt(EventQuery._ID));
        event.setDate(cursor.getLong(EventQuery.DATE));
        event.setName(cursor.getString(EventQuery.NAME));
        event.setDescription(cursor.getString(EventQuery.DESCRIPTION));
        event.setImage(cursor.getString(EventQuery.IMAGE));
        event.setLink(cursor.getString(EventQuery.LINK));
        return event;
    }

}
