package com.example.checklist;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.checklist.adapters.EditContactListAdapter;
import com.example.checklist.async.ContactListUpdater;
import com.example.checklist.async.EditContactListDownloader;

public class EditContactList extends Activity {

	public EditContactListAdapter adapter;
	public ListView contactlistView;
	public ProgressBar spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_contact_list);

		//initializing progress bar
		spinner = (ProgressBar)findViewById(R.id.progressBar);
		spinner.setVisibility(View.GONE);

		ConnectivityManager connMgr = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (!(networkInfo != null && networkInfo.isConnected())) {
			Utilities.showToast(getBaseContext(), (CharSequence)"No network connection", Toast.LENGTH_SHORT);
		} else {

			String url = "http://checklist.host56.com/checklist/contact_list_all.php";
			contactlistView = (ListView)findViewById(R.id.contact_list_view_edit); 
			EditContactListDownloader cListDownloader = 
					new EditContactListDownloader(getApplicationContext(), adapter, contactlistView, this, spinner);
			cListDownloader.execute(url);       
		} 		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_contact_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.go_back) {
			close();
			return true;
		}else if(id == R.id.refresh_contactlist) {
			refreshEditContactList();
			return true;
		}else if(id== R.id.add_contact){
			///closing this activity
			finish();
			
			///opening new contact activity
			Intent intent = new Intent(getApplicationContext(),NewContact.class);
			startActivityForResult(intent, 1);			
		}

		return super.onOptionsItemSelected(item);
	}



	public void close(){
		//kill this activity
		Log.d("contact list edit","destroying edit contact list activity_1");
//		setResult(RESULT_OK);
		EditContactList.this.finish();
	}


	public void setVisibility(int id,Boolean visibility) {
		setResult(RESULT_OK);
		//inform server about the change
		ConnectivityManager connMgr = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Log.d("conact list","going to update visibility of "+id+"(id) as "+visibility);
			String url = "http://checklist.host56.com/checklist/update_contact.php";
			String visString = "0";
			if(visibility){
				visString="1";
			}
			url+="?id="+id+"&visibility="+visString;


			ContactListUpdater cListUpdater = new ContactListUpdater();
			cListUpdater.execute(url);

		} else {
			Utilities.showToast(getBaseContext(), (CharSequence)"No network connection", Toast.LENGTH_SHORT);
		} 
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == 1) {
			// Make sure the request was successful
			Log.d("contact list edit","Received result from single contact view (result code = "+resultCode+")");
			if (resultCode == RESULT_OK) {
				this.refreshEditContactList();
				setResult(RESULT_OK);
			}
		}
	}


	public void refreshEditContactList() {
		Log.d("edit contact list", "started refreshing");
		ConnectivityManager connMgr = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (!(networkInfo != null && networkInfo.isConnected())) {
			Utilities.showToast(getBaseContext(), (CharSequence)"No network connection", Toast.LENGTH_SHORT);
		} else {

			String url = "http://checklist.host56.com/checklist/contact_list_all.php";
			contactlistView = (ListView)findViewById(R.id.contact_list_view_edit); 
			EditContactListDownloader cListDownloader = 
					new EditContactListDownloader(getApplicationContext(), adapter, contactlistView, this, spinner);
			cListDownloader.execute(url);       
		} 	
	}


}
