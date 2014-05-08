package com.yay.iloveua.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yay.iloveua.Config;



public class DatabaseController {

	private static final String DATABASE_NAME = "data.db";
	private static final String DATABASE_PATH = "/data/data/com.yay.iloveua/databases/";
	private static final int DATABASE_VERSION = 18; // version is production = 17

	private SQLiteDatabase db;
	private OpenHelper openHelper;

	public DatabaseController(Context context) {
		if(getCurrentDbVersion(context) != DATABASE_VERSION) {
			copyDbFileFromAssets(context);
		}
		openHelper = new OpenHelper(context);
		db = openHelper.getReadableDatabase();
		Log.i(Config.LOG_TAG, "Db version " + DATABASE_VERSION + " is opened");
	}

	private int getCurrentDbVersion(Context context) {
		return ServiceLocator.getInstance(context).getService(PreferencesManager.class).getCurrentDbVersion();
	}

	private void setCurrentDbVersion(Context context, int dbVersion) {
		ServiceLocator.getInstance(context).getService(PreferencesManager.class).setCurrentDbVersion(dbVersion);
	}

	public SQLiteDatabase getReadableDatabase() {
		return openHelper.getReadableDatabase();
	}
	
	public SQLiteDatabase getWritableDatabase() {
		return openHelper.getWritableDatabase();
	}
	
	public void closeDb() {
		if(db != null && db.isOpen()) {
			db.close();
			Log.d(Config.LOG_TAG, "DB CLOSED");
		}
		if(openHelper != null) {
			openHelper.close();
			Log.d(Config.LOG_TAG, "OpenHelper closed");
		}
	}

	public void copyDbFileFromAssets(Context context) {
		InputStream assetsDB = null;
		try {
			File directory = new File(DATABASE_PATH);
			directory.mkdirs();
			
			assetsDB = context.getAssets().open(DATABASE_NAME);
			OutputStream dbOut = new FileOutputStream(new File(DATABASE_PATH + DATABASE_NAME), false);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = assetsDB.read(buffer)) > 0) {
				dbOut.write(buffer, 0, length);
			}
			dbOut.flush();
			dbOut.close();
			assetsDB.close();
			setCurrentDbVersion(context, DATABASE_VERSION);
			Log.i(Config.LOG_TAG, "New database file copied...");
		} catch (IOException e) {
			Log.e(Config.LOG_TAG, "Could not create new database...");
			e.printStackTrace();
		}
	}

	private static class OpenHelper extends SQLiteOpenHelper {
		
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		}
	}

}
