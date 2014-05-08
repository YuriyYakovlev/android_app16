package com.yay.iloveua;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.yay.iloveua.core.Categories;
import com.yay.iloveua.util.UIUtils;


public class WikiActivity extends SherlockActivity {
	private WebView webView;
	private String url;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wiki_screen);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		    UIUtils.setTitle(this);
        }

		url = getIntent().getStringExtra(Config.URL);

		/*TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
	    txtTitle.setText(getResources().getString(R.string.app_name));
		txtTitle.setTypeface(UIUtils.getTypefaceUa());*/
		
		webView = (WebView) findViewById(R.id.webview);
		webView.setWebViewClient(new Callback());  //HERE IS THE MAIN CHANGE
		
		WebSettings webSettings = webView.getSettings();
    	//webSettings.setJavaScriptEnabled(true);
    	webSettings.setUseWideViewPort(true);
    	webSettings.setSupportZoom(true);     
    	webSettings.setBuiltInZoomControls(true);
    	webView.setInitialScale(1);
		
    	webView.loadUrl(url);
	}
	
	private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (false);
        }
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	        if(webView.canGoBack()) {
	        	webView.goBack();
	        	return true;
	        }
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
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
