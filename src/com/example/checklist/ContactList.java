package com.example.checklist;

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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checklist.adapters.ContactListAdapter;
import com.example.checklist.async.ContactListDownloader;
import com.example.checklist.async.SendIdToServer;
import com.example.checklist.models.ContactItem;
import com.example.checklist.utilities.Constants;
import com.example.checklist.utilities.Utilities;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class ContactList extends Activity {

	public ContactListAdapter adapter;
	public ListView contactlistView;
	public ProgressBar spinner;

    GoogleCloudMessaging gcm;



	private String regId = "";
	private String deviceId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);


		//initializing progress bar
		spinner = (ProgressBar)findViewById(R.id.progressBar);
		contactlistView = (ListView) findViewById(R.id.contact_list_view);
		spinner.setVisibility(View.GONE);

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
				refreshContactList();
				e.printStackTrace();
			}catch (NullPointerException e) {
				refreshContactList();
				e.printStackTrace();
			}
		}else{
			Log.d("contact list","contact list file not fount");
			refreshContactList();
		}

		try{
			if(fis!=null){
				fis.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		///initial checking for gcm
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		///reading reg id and dev id
		regId = GCMRegistrar.getRegistrationId(this);
		deviceId = Secure.getString(getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID); 
		
		if (regId.equals("")) {
			Log.e("gcm registration", "Not already registered");
			getRegId();
			
		} else {
			Log.e("gcm registration", "Already registered reg id: "+regId);
		}
//		SendIdToServer sts = new SendIdToServer(this);
//		String[] params = new String[]{regId,deviceId};
//		sts.execute(params);
		
		

		contactlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//clicked on one contact - making a call
				TextView tv = (TextView) view.findViewById(R.id.phone_no);
				String phoneNumber = tv.getText().toString();
				String uri = "tel:" + phoneNumber.trim() ;
				Intent intent = new Intent(Intent.ACTION_DIAL);//CALL);
				intent.setData(Uri.parse(uri));
				Log.d("contact list","making call to "+phoneNumber);
				startActivity(intent);
			}
		});

	}
	

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("contact list","reached onresume");
		try{
			Intent intent = getIntent();
			if(intent.getBooleanExtra("update_request", false)){
				openUpdateAlert();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onResume();
	}

	public void refreshContactList() {
		Log.d("contact list", "started refreshing");



		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			ContactListDownloader cListDownloader = new ContactListDownloader(
					getApplicationContext(), adapter, contactlistView, spinner);
			cListDownloader.execute(Constants.URL_TO_GET_CONTACT_LIST);
		} else {
			Utilities.showToast(getBaseContext(),
					(CharSequence) "No network connection", Toast.LENGTH_SHORT);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.edit_contact) {
			loadEditContactPage();
			return true;
		}else if(id == R.id.refresh_contactlist) {
			refreshContactList();
			return true;
		}else if(id== R.id.add_contact){
			///opening new contact activity
			Intent intent = new Intent(getApplicationContext(),NewContact.class);
			startActivityForResult(intent, 1);			
		}

		return super.onOptionsItemSelected(item);
	}

	public void loadEditContactPage() {
		Intent intent = new Intent(getApplicationContext(),
				EditContactList.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding toresultIntent
		if (requestCode == 1) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				this.refreshContactList();
			}
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
				if(visibility){
					Log.d("contact list", i +". ("+id+")"+name+" - "+phone_no+"\n");

					ContactItem c = new ContactItem(id, name, phone_no, image, visibility);
					itemlist.add(c);
				}
			}


		} catch (JSONException e) {
			Log.e("contact list", "Exception in post execute "+e.toString());
			e.printStackTrace();
		} catch(NullPointerException e){
			Log.e("contact list", "Empty contact list "+e.toString());
		}


		adapter = new ContactListAdapter(itemlist, this);
		contactlistView.setAdapter(adapter);
		Log.d("contact list", "contact list displaying completed");
	}

	
    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regId = gcm.register(Constants.PROJECT_NUMBER);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            	Log.i("GCM","device registeted, device id :"+regId+" message : "+msg);
            	SendIdToServer sts = new SendIdToServer(getApplicationContext());
        		String[] params = new String[]{regId,deviceId};
        		sts.execute(params);
            }
        }.execute(null, null, null);
    }
    
    private void openUpdateAlert(){
    	AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("Update contact list now ?");
        ab.setPositiveButton("Yes", dialogClickListener);
        ab.setNegativeButton("No", dialogClickListener);
        ab.show();
    }
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                //Yes button clicked
            	refreshContactList();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                //No button clicked
                break;
            }
        }
    };
}
