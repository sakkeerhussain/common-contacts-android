package com.contacts.common_contacts.async;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;

import com.contacts.common_contacts.utilities.Constants;

public class PushUpdateMesssage extends AsyncTask<String, Void, Void> {
	
	Context cnxt=null;
	
	PushUpdateMesssage(Context cntx){
		this.cnxt = cntx;
	}

	@Override
	protected Void doInBackground(String... params) {
		String message = Constants.userName+" "+params[0];
		message = URLEncoder.encode(message);
		
		HttpClient httpclient = new DefaultHttpClient();
		String deviceId = Secure.getString(cnxt.getContentResolver(),
		                Secure.ANDROID_ID); 
		String extra = "?device_id="+deviceId+"&message="+message;
		HttpPost httppost = new HttpPost(Constants.URL_TO_PUSH_NOTIFICATION+extra);
		Log.d("GCM","Url sent  : "+Constants.URL_TO_PUSH_NOTIFICATION+extra);
		HttpResponse httpResponce =null;
		try {
			httpResponce =  httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("GCM","update notification sent to all other devices \n responce ("+httpResponce.toString()+")");
		return null;
	}

}
