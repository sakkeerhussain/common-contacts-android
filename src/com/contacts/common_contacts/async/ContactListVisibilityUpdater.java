package com.contacts.common_contacts.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.contacts.common_contacts.EditContactList;
import com.contacts.common_contacts.models.ContactUpdateRequestModel;
import com.contacts.common_contacts.utilities.Utilities;

public class ContactListVisibilityUpdater extends AsyncTask<ContactUpdateRequestModel, Void , Boolean> {

	private static Context context;
	private static ContactUpdateRequestModel cum;
	private static ProgressBar progressBar;

	public ContactListVisibilityUpdater(Context c, ProgressBar progressBar) {
		ContactListVisibilityUpdater.context = c;
		ContactListVisibilityUpdater.progressBar = progressBar;
	}

	@Override
	protected void onPreExecute() {       
		super.onPreExecute();
		try {
			progressBar.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Boolean doInBackground(ContactUpdateRequestModel... params) {
		if(!Utilities.isNetwork(context)){
			return false;
		}
		cum = params[0];
		String url = cum.getUrl();
		try {
			Log.d("contact list", "contact list updating started ");
			return Utilities.updateContactVisibility(url);
		} catch (Exception e) {
			return null;
		}
	}

	protected void onPostExecute(Boolean res) {
		
		try {
			progressBar.setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Activity activity = (Activity)context;
		if(res){
			Log.d("contact list", "contact list update completed");
			
			///pushing update message to all other devices
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if(cum.getAction().equals("edit_visibility")){
						Utilities.showToast(context, "Contact visibility update successfull.", Toast.LENGTH_SHORT);
						Log.d("contact list", "Contact visibility edit request compleated");
					}else{
						Log.d("contact list", "Contact update request successful (ContactRequestModel)"+cum.toString());						
					}
					PushUpdateMesssage pu = new PushUpdateMesssage(context);
					pu.execute(cum.getPushMessage());
				}
			});
			
		}else{
			Log.e("contact list", "contact list update failed");
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Utilities.showToast(context, "Contact visibility update failed.", Toast.LENGTH_SHORT);
					if(cum.getAction().equals("edit_visibility")){
						EditContactList editContactList  = (EditContactList) cum.getCaller();
						editContactList .displayContacts();
						Log.e("contact list", "Contact visibility edit request failed (Url)"+cum.getUrl());
					}else{
						Log.e("contact list", "Contact update request failed (ContactRequestModel)"+cum.toString());						
					}
				}
			});
		}
	}

}
