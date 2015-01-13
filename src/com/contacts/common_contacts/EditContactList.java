package com.contacts.common_contacts;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.contacts.common_contacts.adapters.EditContactListAdapter;
import com.contacts.common_contacts.async.ContactListVisibilityUpdater;
import com.contacts.common_contacts.async.EditContactListDownloader;
import com.contacts.common_contacts.models.ContactItem;
import com.contacts.common_contacts.models.ContactUpdateRequestModel;
import com.contacts.common_contacts.utilities.Constants;
import com.contacts.common_contacts.utilities.Utilities;

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
		
		contactlistView = (ListView) findViewById(R.id.contact_list_view_edit);

		displayContacts();
	}

	public void displayContacts() {
		boolean isContactFileExists = true; 
		FileInputStream fis = null;
		try {
			fis = openFileInput(Constants.CONTACT_LIST_FILE_NAME);
		} catch (FileNotFoundException e) {
			isContactFileExists = false;
			e.printStackTrace();
		}

		if(fis == null) isContactFileExists = false;


		if(isContactFileExists){
			Log.d("contact list","contact list file fount");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				String contactList_String = br.readLine();				
				Log.d("contact list","read string from disk : "+contactList_String);
				displayContact(contactList_String);
			} catch (IOException e) {
				refreshEditContactList();
				e.printStackTrace();
			}catch (NullPointerException e) {
				refreshEditContactList();
				e.printStackTrace();
			}
		}else{
			Log.d("contact list","contact list file not fount");
			refreshEditContactList();
		}

		try{
			if(fis!=null){
				fis.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkForDirtyContact();
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
			startActivity(intent);			
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
		Constants.contactDirtyFlag = true;
		//TODO
		
		//inform server about the change
		ConnectivityManager connMgr = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Log.d("conact list","going to update visibility of "+id+"(id) as "+visibility);
			String url = Constants.URL_TO_UPDATE_CONTACT_VISIBILITY;
			String visString = "0";
			if(visibility){
				visString="1";
			}
			url+="?id="+id+"&visibility="+visString;


			ContactListVisibilityUpdater cListVisibilityUpdater = new ContactListVisibilityUpdater(this, spinner);
			cListVisibilityUpdater.execute(new ContactUpdateRequestModel(url, this, "edit_visibility", "edited a contact"));

		} else {
			Utilities.showToast(getBaseContext(), (CharSequence)"No network connection", Toast.LENGTH_SHORT);
		} 
	}


	private void checkForDirtyContact() {
		if ( Constants.contactDirtyFlag ) {
			this.refreshEditContactList();
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
			EditContactListDownloader cListDownloader = 
					new EditContactListDownloader(getApplicationContext(), adapter, contactlistView, this, spinner);
			cListDownloader.execute(Constants.URL_TO_GET_CONTACT_LIST);       
		} 	
	}


	void displayContact(String contactList_String){
		JSONArray jsonArray=null;
		try{
			jsonArray = new JSONArray(contactList_String);
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data "+e.toString());
		}

		///printing contacts
		ArrayList<ContactItem> itemlist = new ArrayList<ContactItem>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject jsonobject = jsonArray.getJSONObject(i);
				int id = jsonobject.getInt("id");
				String name=jsonobject .getString("name");
				String phone_no=jsonobject .getString("phone_no");
				String image = jsonobject.getString("image");
				String visStr = jsonobject.getString("visibility");
				Boolean visibility;
				if(visStr.equals("1")){
					visibility = true;
				}else{
					visibility = false;
				}
				Log.d("contact list", i +". ("+id+")"+name+" - "+phone_no+"\n");

				ContactItem c = new ContactItem(id, name, phone_no, image, visibility);
				itemlist.add(c);

			}


		} catch (JSONException e) {
			Log.e("contact list", "Exception in post execute "+e.toString());
			e.printStackTrace();
		} catch(NullPointerException e){
			Log.e("contact list", "Empty contact list "+e.toString());
		}


		adapter = new EditContactListAdapter(itemlist, getApplicationContext(), this);
		contactlistView.setAdapter(adapter);
		Log.d("contact list", "contact list displaying completed");
	}

}
