package com.yay.iloveua;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Map;

import com.yay.iloveua.util.LruCacheLinkedHashMap;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;


public class App extends Application {
	private String TAG = getClass().getSimpleName();
    private static App instance; 
    public static boolean USE_SD = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	public static String CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.iloveua/";
	private static Map<Object, SoftReference<Drawable>> drawableHashMap;
	    
    
	public static App get() {
		return instance;
	}
	
	public static boolean isUseSD() {
		 return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		drawableHashMap = Collections.synchronizedMap(new LruCacheLinkedHashMap(100));
		 if(isUseSD()) {
			 File preparedDir = new File(CACHE_DIR);
	         if(!preparedDir.exists()) {
	        	 preparedDir.mkdirs();
	         }
		 }
	}
	
	private Boolean isOnline;
    public boolean isOnline(boolean force) {
		if(isOnline == null || force) {
			try {
				final ConnectivityManager conMgr = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
				final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
				if(activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
				    isOnline = true;
				} else {
				    isOnline = false;
				} 
			} catch(Exception e) {
				Log.e(TAG, e.toString());
				isOnline = false;
			}
		}
		return isOnline;
	}
  
    public Map<Object, SoftReference<Drawable>> getDrawableHashMap() {
		return drawableHashMap;
	}
    
}
