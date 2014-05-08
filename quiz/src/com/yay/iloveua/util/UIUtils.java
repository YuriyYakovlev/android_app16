package com.yay.iloveua.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yay.iloveua.App;
import com.yay.iloveua.Config;


public class UIUtils {
    public static Typeface typefaceDefault;
    public static Typeface typefaceUa;
    private static final String SERVER_URL = "http://yakovlev.dp.ua/iloveua/";
    private static String S1 = "%s%s.jpg"; 
    
    public static Typeface getTypefaceDefault() {
		if(typefaceDefault == null) {
			typefaceDefault = Typeface.createFromAsset(App.get().getResources().getAssets(), "fonts/default.ttf");
		}
		return typefaceDefault;
	}

    public static Typeface getTypefaceUa() {
		if(typefaceUa == null) {
			typefaceUa = Typeface.createFromAsset(App.get().getResources().getAssets(), "fonts/ua.ttf");
		}
		return typefaceUa;
	}

    public static void setTitle(Activity activity) {
		int actionBarTitleId = -1;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            	actionBarTitleId = Class.forName("com.actionbarsherlock.R$id").getField("abs__action_bar_title").getInt(null);
            }
            else {
                // Use reflection to get the actionbar title TextView and set the custom font. May break in updates.
            	actionBarTitleId = Class.forName("com.android.internal.R$id").getField("action_bar_title").getInt(null);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if(actionBarTitleId != -1) {
        	TextView title = (TextView) activity.findViewById(actionBarTitleId); 
        	if(title != null) {
                title.setTypeface(UIUtils.getTypefaceUa());
            }
        }
    }
    public static void fetchDrawableOnThread(final String id, final View imageView) {
    	if(id != null && !"".equals(id)) {
    		if(App.get().getDrawableHashMap().containsKey(id)) {
	            imageView.setBackgroundDrawable(App.get().getDrawableHashMap().get(id).get());
	        }
	
	        final Handler handler = new Handler() {
	            @Override
	            public void handleMessage(Message message) {
	            	Drawable drawable = (Drawable) message.obj;
	            	if(drawable == null) {
	            		imageView.setVisibility(View.GONE);
	            	} else {
	            		imageView.setVisibility(View.VISIBLE);
	            		imageView.setBackgroundDrawable(drawable);
	            	}
	            }
	        };
	
	        Thread thread = new Thread() {
	            @Override
	            public void run() {
	                //TODO : set imageView to a "pending" image
	            	Drawable drawable = fetchDrawable(id);
	                Message message = handler.obtainMessage(1, drawable);
	                handler.sendMessage(message);
	            }
	        };
	        thread.start();
    	} else {
    		imageView.setVisibility(View.GONE);
    	}
    }
    
    public static Drawable fetchDrawable(String id) {
    	Drawable drawable = null;
    	if(id != null && !"".equals(id)) {
	    	// read from file cache
	    	File f = null;
	    	if(App.USE_SD) {
	        	f = new File(App.CACHE_DIR, id + ".jpg");
	        	Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
	        	if(bitmap != null) {
	        		drawable = new BitmapDrawable(App.get().getResources(), bitmap);
	        		return drawable;
	        	}
	    	} else {
	        	// read from memory cache
	        	if(App.get().getDrawableHashMap().containsKey(id)) {
	                return App.get().getDrawableHashMap().get(id).get();
	            }
	    	}
	    	if(App.get().isOnline(false)) {
		    	// fetch from URL
		        InputStream is = null;
		        try {
		            String url = String.format(S1, SERVER_URL, id);
		            URLConnection con = new URL(url).openConnection();
		            con.connect();
		            int fileLength = con.getContentLength();
		            is = con.getInputStream();
		        	if(is != null) {
		        		byte[] bytes = new byte[fileLength];
		                for(int i=0; i<fileLength; i++) {
		                    bytes[i] = (byte)is.read();
		                }
		                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, fileLength);
		                //Bitmap bitmap = BitmapFactory.decodeStream(is);
				    	if(bitmap != null) {
			                drawable = new BitmapDrawable(App.get().getResources(), bitmap);
			            	if(App.USE_SD) {
			            		writeFile(bitmap, f);
				            } else {
				            	if(drawable != null) {
				            		App.get().getDrawableHashMap().put(id, new SoftReference<Drawable>(drawable));
				            	}	            	
				            }
				    	}
		        	}
		        } catch (Exception e) {
		            Log.w(Config.LOG_TAG, "fetchDrawable failed", e);
		        } finally {
		            if(is != null) {
		                try { is.close(); } catch (IOException e) {	}
		            }
		        }
	    	}
    	}
        return drawable;
    }
    
    private static void writeFile(Bitmap bmp, File f) {
    	FileOutputStream out = null;
    	try {
    	    out = new FileOutputStream(f);
    	    if(out != null) {
    	    	bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	} finally { 
    	    try { if (out != null ) out.close(); }
    	    catch(Exception ex) {} 
    	}
   	}
    
}
