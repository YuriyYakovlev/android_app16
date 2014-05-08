package com.yay.iloveua.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.util.Log;


public class HttpHelper {
	

	public String doPost(JSONObject json) {
		String result = "";
		InputStream is = null; 

		// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(/*Config.CIKLUM_SUBMIT_URL*/);

	    try{
	        // Add your data
	        StringEntity se = new StringEntity(json.toString(), "UTF-8");  
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);
            
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	    } catch (ClientProtocolException e) {
	    	Log.e("ClientProtocolException", e+"");
	    } catch (IOException e) {
	    	Log.e("IO Exception", e+"");
	    }

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 100);
		    StringBuilder sb = new StringBuilder();
		    String line = null;
		    while((line = reader.readLine()) != null) {
		     	sb.append(line + "\n");
		    }
	        is.close();
	        result = sb.toString();
		} catch(Exception e) {
	       	Log.e(this.getClass().getName(), e.toString());
		}
		return result;
	}
	
	
}
