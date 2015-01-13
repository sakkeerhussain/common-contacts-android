package com.contacts.common_contacts;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.contacts.common_contacts.utilities.Utilities;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService {
	private Handler handler;
	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler();
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
		String collapseKey = intent.getStringExtra("collapse_key");


		if(collapseKey!=null){
			if(collapseKey.equals("update_contact_list")){
				String mes = extras.getString("message");
				showNotification("Contact list updated", mes);
				Log.i("GCM", "Received : (" +messageType+")  "+extras.getString("message"));
			}else{
				Log.i("GCM", "Received : {(collapse key) "+collapseKey+"} (" +messageType+")  "+extras.getString("message"));
			}
		}else{
			Log.i("GCM", "Received Null collapse key: (" +messageType+")  "+extras.getString("message"));
		}

		GcmBroadcastReceiver.completeWakefulIntent(intent);

	}

	public void showNotification(final String title, final String mes){
		handler.post(new Runnable() {
			public void run() {
				Utilities.createNotification(title, mes, getApplicationContext());
			}
		});

	}
}
