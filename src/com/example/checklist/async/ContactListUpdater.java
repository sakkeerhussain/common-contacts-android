package com.example.checklist.async;

import android.os.AsyncTask;
import android.util.Log;

import com.example.checklist.Utilities;

public class ContactListUpdater extends AsyncTask<String, Void , Boolean> {

//	private static Context context;

	public ContactListUpdater() {
//		context = c;
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
		}else{
			Log.e("contact list", "contact list update failed");
		}
	}

}
