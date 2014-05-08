package com.yay.iloveua;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.yay.iloveua.core.ServiceLocator;


public class SplashActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGTH = 2000;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		new Handler().postDelayed(new Runnable() {
			public void run() {
		        /*boolean firstRun = ServiceLocator.getInstance(SplashActivity.this).getService(PreferencesManager.class).getBoolean(Config.FIRST_RUN, true);
		        if(firstRun) {
		        	Intent startIntent = new Intent(SplashActivity.this, RegistrationActivity.class);
					startActivity(startIntent);
		        } else {*/
					Intent startIntent = new Intent(SplashActivity.this, HomeActivity.class);
					startActivity(startIntent);
		        //}
				finish();
			}
		}, SPLASH_DISPLAY_LENGTH);
		ServiceLocator.getInstance(this.getBaseContext());
	}

}