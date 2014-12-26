package com.example.checklist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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

public class ContactList extends Activity {

	public ContactListAdapter adapter;
	public ListView contactlistView;
	public ProgressBar spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

		//initializing progress bar
		spinner = (ProgressBar)findViewById(R.id.progressBar);
		spinner.setVisibility(View.GONE);

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (!(networkInfo != null && networkInfo.isConnected())) {
			Utilities.showToast(getBaseContext(),
					(CharSequence) "No network connection", Toast.LENGTH_SHORT);
		} else {

			String url = "http://checklist.host56.com/checklist/contact_list.php";
			contactlistView = (ListView) findViewById(R.id.contact_list_view);
			ContactListDownloader cListDownloader = new ContactListDownloader(
					getBaseContext(), adapter, contactlistView, spinner);
			cListDownloader.execute(url);
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
	}

	public void refreshContactList() {
		Log.d("contact list", "started refreshing");



		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			String url = "http://checklist.host56.com/checklist/contact_list.php";
			contactlistView = (ListView) findViewById(R.id.contact_list_view);
			ContactListDownloader cListDownloader = new ContactListDownloader(
					getBaseContext(), adapter, contactlistView, spinner);
			cListDownloader.execute(url);

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
	    // Check which request we're responding to
		Log.d("contact list","edit returned. result code ( "+resultCode+" )" );
	    if (requestCode == 1) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	            this.refreshContactList();
	        }
	    }
	}
}
