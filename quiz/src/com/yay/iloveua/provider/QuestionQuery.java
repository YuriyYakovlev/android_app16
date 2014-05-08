package com.yay.iloveua.provider;

import com.yay.iloveua.provider.QuizContract.Tables;


public interface QuestionQuery {
    int _TOKEN = 0x1;
    String[] PROJECTION = {
    		Tables.QUESTION + "." + QuestionColumns._ID.getName(),
    		QuestionColumns.ANSWER.getName(),
    		QuestionColumns.SOLUTIONS.getName(),
    		QuestionColumns.QUESTION.getName(),
    		QuestionColumns.IDP.getName(),
    		QuestionColumns.IMAGE.getName()
    };

    int _ID = 0;
    int ANSWER = 1;
    int SOLUTIONS = 2;
    int QUESTION = 3;
    int IDP = 4;
    int IMAGE = 5;
    
}
