package com.example.checklist;

import com.example.checklist.async.ContactListUpdater;
import com.example.checklist.utilities.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SingleContact extends Activity {

	private int contactId;
	private String phone_no;
	private String name;
	private boolean visibility;
	private Bitmap image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_contact);
		TextView phoneNoTxtView = (TextView)findViewById(R.id.phone_no_single); 
		ToggleButton tb = (ToggleButton)findViewById(R.id.togglebutton);
		ImageView im = (ImageView)findViewById(R.id.imageView1); 

		///receiving data from parent activity
		Intent intent = getIntent();
		this.contactId = intent.getIntExtra("contact_id", 0);
		Log.d("contact","contact-"+contactId+" loading");
		this.phone_no = intent.getStringExtra("phone_no");
		this.name = intent.getStringExtra("name");
		this.visibility = intent.getBooleanExtra("visibility", false);
		this.image = (Bitmap) intent.getParcelableExtra("image");

		///setting values received in components
		phoneNoTxtView.setText((CharSequence)phone_no);
		tb.setChecked(visibility);
		setTitle((CharSequence) name);
		if(this.image!=null){
			im.setImageBitmap(this.image);
		}else{
			Log.d("contact","invalid image received");
		}

		Log.d("contact","contact - "+contactId+" Name : "+ name +" Phone : "+phone_no+" selected.");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.single_contact, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.go_back) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	public void onToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();

		setResult(RESULT_OK);
		
		if (on) {
			// Enable vibrate
			Log.d("contact","contact("+contactId+") enabled");
		} else {
			// Disable vibrate
			Log.d("contact","contact("+contactId+") disabled");
		}
		setVisibility(contactId, on);
	}


	public void setVisibility(int id,Boolean visibility) {
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


			ContactListUpdater cListUpdater = new ContactListUpdater(this, "One%20Contact%20edited");
			cListUpdater.execute(url);

		} else {
			Utilities.showToast(getBaseContext(), (CharSequence)"No network connection", Toast.LENGTH_SHORT);
		} 
	}

	public void deleteContact(View v){
		deleteContact(contactId);
		setResult(RESULT_OK);
		finish();
	}

	public void deleteContact(int id) {
		//inform server about the change
		ConnectivityManager connMgr = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Log.d("conact list","going to delete "+id+"(id)");
			String url = "http://checklist.host56.com/checklist/delete_contact.php";
			url+="?id="+id;

			ContactListUpdater cListUpdater = new ContactListUpdater(this,"One%20Contact%20deleted");
			cListUpdater.execute(url);

		} else {
			Utilities.showToast(getBaseContext(), (CharSequence)"No network connection", Toast.LENGTH_SHORT);
		} 
	}

}
