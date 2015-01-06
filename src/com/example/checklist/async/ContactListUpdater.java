package com.example.checklist.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.checklist.utilities.Utilities;

public class ContactListUpdater extends AsyncTask<String, Void , Boolean> {

	private static Context context;
	private static String message;

	public ContactListUpdater(Context c, String message) {
		ContactListUpdater.context = c;
		ContactListUpdater.message = message;
	}

	@Override
	protected void onPreExecute() {       
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String url = params[0];
		try {
			Log.d("contact list", "contact list updating started ");
			return Utilities.updateContactVisibility(url);
		} catch (Exception e) {
			return null;
		}
	}

	protected void onPostExecute(Boolean res) {
		if(res){
			Log.d("contact list", "contact list update completed");
			
			///pushing update message to all other devices
			Activity activity = (Activity)context;
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					PushUpdateMesssage pu = new PushUpdateMesssage(context);
					pu.execute(message);
				}
			});
			
		}else{
			Log.e("contact list", "contact list update failed");
		}
	}

}
