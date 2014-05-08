package com.yay.iloveua.provider;

import com.yay.iloveua.provider.QuizContract.Tables;


public interface EventQuery {
    int _TOKEN = 0x2;
    String[] PROJECTION = {
    		Tables.EVENT + "." + EventColumns._ID.getName(),
            EventColumns.DATE.getName(),
            EventColumns.NAME.getName(),
            EventColumns.DESCRIPTION.getName(),
            EventColumns.IMAGE.getName(),
            EventColumns.LINK.getName()
    };

    int _ID = 0;
    int DATE = 1;
    int NAME = 2;
    int DESCRIPTION = 3;
    int IMAGE = 4;
    int LINK = 5;
    
}
