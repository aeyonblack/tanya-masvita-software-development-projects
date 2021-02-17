package com.acronym.playit;

import android.app.Application;

import com.acronym.playit.helpers.QuizOpenHelper;

public class Playit extends Application {

    private static QuizOpenHelper db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = new QuizOpenHelper(getApplicationContext());
    }

    public static QuizOpenHelper getDb() {
        return db;
    }

}
