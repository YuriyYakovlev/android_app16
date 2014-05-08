package com.yay.iloveua;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.yay.iloveua.core.PreferencesManager;
import com.yay.iloveua.core.ServiceLocator;
import com.yay.iloveua.util.UIUtils;


public class ResultsActivity extends SherlockActivity implements OnClickListener {
	//private String TAG = getClass().getSimpleName();
	private int idp;
	//private String wrongAnswers;
	private int score;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results_screen);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		    UIUtils.setTitle(this);
        }

		idp = getIntent().getIntExtra(Config.CATEGORY, 1);
		
		score = getIntent().getIntExtra(Config.SCORE, 0);
		//int highScore = ServiceLocator.getInstance(this).getService(PreferencesManager.class).getInt(Config.SCORE + idp, 0);
		//wrongAnswers = getIntent().getStringExtra(Config.WRONG_ANSWERS);
				
		TextView txtResult = (TextView) findViewById(R.id.txtResult); 
		txtResult.setTypeface(UIUtils.getTypefaceDefault());
		//TextView txtLink = (TextView) findViewById(R.id.txtLink); 
		//txtLink.setTypeface(UIUtils.getTypefaceDefault());
		
		//new PostDataTask().execute(); // TODO: remove
		
		//if(score > highScore) {
			ServiceLocator.getInstance(this).getService(PreferencesManager.class).setInt(Config.SCORE + idp, score);
			/*if(App.get().isOnline(true)) { // submit data to server
				new PostDataTask().execute();
			}*/
		//}
	    
		if(score > 50) { // good results
			final SpannableStringBuilder sb = new SpannableStringBuilder(String.format(getResources().getString(R.string.results_good), score));
	        final ForegroundColorSpan fcs1 = new ForegroundColorSpan(Color.rgb(71, 168, 214)); // Span to set text color to some RGB value
	        sb.setSpan(fcs1, 25, 28, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Set the text color for first 4 characters
	        txtResult.setText(sb);
	        
	        /*txtLink.setText(Config.CIKLUM_EMAIL);
	        txtLink.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final Intent emailIntent = new Intent(Intent.ACTION_SEND); 
					emailIntent.setType("text/plain"); 
					emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.CIKLUM_EMAIL}); 
					emailIntent.putExtra(Intent.EXTRA_SUBJECT, ServiceLocator.getInstance(ResultsActivity.this).getService(PreferencesManager.class).getString(Config.NAME, "") + " CV"); 
					startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				}
	        });*/
		} else { // bad results
			final SpannableStringBuilder sb = new SpannableStringBuilder(String.format(getResources().getString(R.string.results_bad), score));
	        final ForegroundColorSpan fcs1 = new ForegroundColorSpan(Color.rgb(255, 102, 0)); // Span to set text color to some RGB value
	        sb.setSpan(fcs1, 32, 35, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // Set the text color for first 4 characters
	        txtResult.setText(sb);
	        
	        /*txtLink.setText(Config.CIKLUM_EVENTS);
	        txtLink.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse("http://" + Config.CIKLUM_EVENTS)));
				}
	        });*/
		}
	  
   		Button btnOK = (Button)findViewById(R.id.btnOK);
   		btnOK.setTypeface(UIUtils.getTypefaceDefault());
   		btnOK.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if(view instanceof Button) {
			finish();
		}
	}
	
	/*private class PostDataTask extends AsyncTask<Intent, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(Intent... intents) {
			HttpHelper helper = new HttpHelper();
			JSONObject object = new JSONObject();
			try {
				try {
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					object.put("msisdn", tm.getLine1Number());
				} catch(Exception e) { Log.w(TAG, e.toString()); }
				
				object.put("email", ServiceLocator.getInstance(ResultsActivity.this).getService(PreferencesManager.class).getString(Config.EMAIL, ""));
				object.put("name", ServiceLocator.getInstance(ResultsActivity.this).getService(PreferencesManager.class).getString(Config.NAME, ""));
				object.put("country", ServiceLocator.getInstance(ResultsActivity.this).getService(PreferencesManager.class).getString(Config.COUNTRY, ""));
				object.put("city", ServiceLocator.getInstance(ResultsActivity.this).getService(PreferencesManager.class).getString(Config.CITY, ""));
				object.put("score", score);
				object.put("wrongAnswers", wrongAnswers);
				object.put("testName", Categories.findById(idp).getName());
				
				final String response = helper.doPost(object);
				Log.d(TAG, response);
				return true;
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		}

	}*/
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case android.R.id.home:
		        finish();
		        return true;
		    default:
		    	return super.onOptionsItemSelected(item);
	    }
	}    

}