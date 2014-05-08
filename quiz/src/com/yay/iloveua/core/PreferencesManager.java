package com.yay.iloveua.core;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesManager {
	public static final String CURRENT_DB_VERSION = "db_version";
	//public static final String POINTS_MAX = "points_max";
	//public static final String POINTS = "points";
	public static final String MAX_QUESTIONS = "max_questions";
	public static final String TIMER_DURATION = "timer_duration";
	
	private SharedPreferences _preferences;

	public PreferencesManager(SharedPreferences preferences) {
		_preferences = preferences;
	}

	public int getInt(String key, int defaultValue) {
		return getPreferences().getInt(key, defaultValue);
	}

	public int getCurrentDbVersion() {
		return getInt(CURRENT_DB_VERSION, 0);
	}
	
	public void setCurrentDbVersion(int dbVersion) {
		setInt(CURRENT_DB_VERSION, dbVersion);
	}

	/*public String getCurrentLocale() {
		return getString(CURRENT_LOCALE, "de");
	}

	public void setCurrentLocale(String locale) {
		setString(CURRENT_LOCALE, locale);
	}*/

	public void setInt(String key, int value) {
		Editor editor = getPreferences().edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public long getLong(String key, long defaultValue) {
		return getPreferences().getLong(key, defaultValue);
	}

	public void setLong(String key, long value) {
		Editor editor = getPreferences().edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public String getString(String key, String defaultValue) {
		return getPreferences().getString(key, defaultValue);
	}

	public void setString(String key, String value) {
		Editor editor = getPreferences().edit();
		editor.putString(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return getPreferences().getBoolean(key, defaultValue);
	}

	public void setBoolean(String key, boolean value) {
		Editor editor = getPreferences().edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public SharedPreferences getPreferences() {
		return _preferences;
	}
}
