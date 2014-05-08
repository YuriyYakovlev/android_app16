package com.yay.iloveua.provider;

import com.yay.iloveua.provider.QuizContract.DBType;


public enum EventColumns {
	_ID("_id", DBType.INT),
	DATE("date", DBType.INT),
	NAME("name", DBType.TEXT),
	DESCRIPTION("description", DBType.TEXT),
	IMAGE("image", DBType.TEXT),
	LINK("link", DBType.TEXT);

	private String columnName;
    private DBType type;

    EventColumns(String columnName, DBType type) {
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
