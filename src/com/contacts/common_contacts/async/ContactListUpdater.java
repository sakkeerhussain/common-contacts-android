package com.contacts.common_contacts.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.contacts.common_contacts.SingleContact;
import com.contacts.common_contacts.models.ContactItem;
import com.contacts.common_contacts.models.ContactUpdateRequestModel;
import com.contacts.common_contacts.utilities.Constants;
import com.contacts.common_contacts.utilities.Utilities;

public class ContactListUpdater extends AsyncTask<ContactUpdateRequestModel, Void , Boolean> {

	private static Context context;
	private static ContactUpdateRequestModel cum;
	private static ProgressBar progressBar;

	public ContactListUpdater(Context c, ProgressBar progressBar) {
		ContactListUpdater.context = c;
		ContactListUpdater.progressBar = progressBar;
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
		ContactItem contact = cum.getContact();
		Boolean res;
		try {
			Log.d("contact list", "contact list updating started ");
			res = Utilities.updateContact(url, contact);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return res;
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
			
			//flaging contact updated
			Constants.contactDirtyFlag = true;

			///pushing update message to all other devices
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if(cum.getAction().equals("edit")){
						Utilities.showToast(context, "Contact updated successfully.", Toast.LENGTH_SHORT);
						Log.d("contact list", "Contact visibility edit request compleated successfully");
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
					Utilities.showToast(context, "Contact update failed.", Toast.LENGTH_SHORT);
					if(cum.getAction().equals("edit")){
						SingleContact singleContact  = (SingleContact) cum.getCaller();
						singleContact.displayContact();
						Log.e("contact list", "Contact edit request failed (Url)"+cum.getUrl());
					}else{
						Log.e("contact list", "Contact update request failed (ContactRequestModel)"+cum.toString());						
					}
				}
			});
		}
	}

}
