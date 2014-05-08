package com.yay.iloveua.provider;

import com.yay.iloveua.provider.QuizContract.DBType;


public enum QuestionColumns {
	_ID("_id", DBType.INT),
	ANSWER("answer", DBType.TEXT),
	SOLUTIONS("solutions", DBType.TEXT),
	QUESTION("question", DBType.TEXT),
	IDP("idp", DBType.INT),
	IMAGE("image", DBType.TEXT);
    
	private String columnName;
    private DBType type;
    
    QuestionColumns(String columnName, DBType type) {
    	this.columnName = columnName;
    	this.type = type;
    }
    
    public String getName() {
    	return columnName;
    }
    public DBType getType() {
    	return type;
    }
}
