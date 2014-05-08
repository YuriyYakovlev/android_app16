package com.yay.iloveua.provider;

import android.net.Uri;


public interface QuizContract {
    String CONTENT_AUTHORITY = "com.yay.iloveua";
    Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    String SEARCH_URI = "search";
    
    interface Tables {
    	String QUESTION = "QUESTION";
        String EVENT = "EVENT";
    }

	enum DBType {
		INT(" INTEGER,"),
		FLOAT(" FLOAT,"),
		TEXT(" TEXT,");
		
		private String name;
        
        DBType(String name) {
        	this.name = name;
        }
        
        public String getName() {
        	return name;
        }
	}
    
}
