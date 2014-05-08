package com.yay.iloveua.models;

import android.database.Cursor;
import android.net.Uri;

import com.yay.iloveua.provider.QuestionColumns;
import com.yay.iloveua.provider.QuestionQuery;
import com.yay.iloveua.provider.QuizContract;


public class Question implements QuizContract {
	public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(Tables.QUESTION).build();
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.interview.question";
    public static final String FLAG_URL = "flag";
    public static final String FLAG_RESET_URL = "flag_reset";
    private int id;
    private String answer;
    private String solutions;
    private String question;
    private int flag;
    private int idp;
    private String image;
    
    
    public Question() {
    	
    }
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getSolutions() {
		return solutions;
	}

	public void setSolutions(String solutions) {
		this.solutions = solutions;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getIdp() {
		return idp;
	}

	public void setIdp(int idp) {
		this.idp = idp;
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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
    public static final String DEFAULT_SORT = QuestionColumns._ID.getName() + " ASC";

    public static Uri buildQuestionUri(String id) {
        return CONTENT_URI.buildUpon().appendPath(QuestionColumns._ID.getName()).appendPath(id).build();
    }

    public static Uri buildQuestionsUri(String idp) {
        return CONTENT_URI.buildUpon().appendPath(QuestionColumns.IDP.getName()).appendPath(idp).build();
    }

    public static Uri buildSearchUri(String query) {
        return CONTENT_URI.buildUpon().appendPath(SEARCH_URI).appendPath(query).build();
    }
    
    public static Uri buildFlagUri(String query) {
        return CONTENT_URI.buildUpon().appendPath(FLAG_URL).appendPath(query).build();
    }
    
    public static Uri buildFlagResetUri(String query) {
        return CONTENT_URI.buildUpon().appendPath(FLAG_RESET_URL).appendPath(query).build();
    }
    
    public static String getSearchQuery(Uri uri) {
        return uri.getPathSegments().get(2);
    }

    public static Question buildFromCursor(Cursor cursor) {
    	Question question = new Question();
    	question.setId(cursor.getInt(QuestionQuery._ID));
    	question.setAnswer(cursor.getString(QuestionQuery.ANSWER));
    	question.setSolutions(cursor.getString(QuestionQuery.SOLUTIONS));
    	question.setQuestion(cursor.getString(QuestionQuery.QUESTION));
    	question.setIdp(cursor.getInt(QuestionQuery.IDP));
    	question.setImage(cursor.getString(QuestionQuery.IMAGE));
    	return question;
    }

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
    
}
