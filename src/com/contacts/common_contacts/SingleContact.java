package com.contacts.common_contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.contacts.common_contacts.async.ContactListUpdater;
import com.contacts.common_contacts.async.ContactListVisibilityUpdater;
import com.contacts.common_contacts.models.ContactItem;
import com.contacts.common_contacts.models.ContactUpdateRequestModel;
import com.contacts.common_contacts.utilities.Constants;
import com.contacts.common_contacts.utilities.Utilities;

public class SingleContact extends Activity {

	///constants 
	final int PHOTO_WIDTH = 80;
	final int PHOTO_HEIGHT = 70;

	private int contactId;
	private String phone_no;
	private String name;
	private boolean visibility;
	private Bitmap image;
	public ProgressBar spinner;
	TextView phoneNoTxtView;
	TextView nameTxtView;

	ImageView iv ;
	CheckBox cb;
	Activity activity = this;
	boolean imageChangedFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_contact);
		phoneNoTxtView = (TextView)findViewById(R.id.phone_at_single_contact); 
		nameTxtView = (TextView)findViewById(R.id.name_at_single_contact); 
		cb = (CheckBox)findViewById(R.id.cbox_visibility);
		iv = (ImageView)findViewById(R.id.imageView1); 
		spinner = (ProgressBar)findViewById(R.id.progressBar);		

		displayContact();
	}

	public void displayContact() {
		//initializing progress bar
		try{
			spinner.setVisibility(View.GONE);
		}catch(NullPointerException e){
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		imageChangedFlag = false;

		///receiving data from parent activity
		Intent intent = getIntent();
		this.contactId = intent.getIntExtra("contact_id", 0);
		Log.d("contact","contact-"+contactId+" loading");
		this.phone_no = intent.getStringExtra("phone_no");
		this.name = intent.getStringExtra("name");
		this.visibility = intent.getBooleanExtra("visibility", false);
		this.image = (Bitmap) intent.getParcelableExtra("image");
		this.image = Utilities.getCircleImage(this.image, 240, 240);

		setValues(phone_no, name, visibility, image);

		Log.d("contact","contact - "+contactId+" Name : "+ name +" Phone : "+phone_no+" selected.");
	}

	public void setValues(String phoneNo, String name, Boolean visibility, Bitmap bm) {
		///setting values received in components
		phoneNoTxtView.setText((CharSequence)phoneNo);
		nameTxtView.setText((CharSequence)name);
		cb.setChecked(visibility);

		if (cb.isChecked()) {
			cb.setBackgroundResource(R.drawable.green_border);
		} else {
			cb.setBackgroundResource(R.drawable.red_border);
		}

		setTitle((CharSequence) name);

		if(bm!=null){
			iv.setImageBitmap(bm);
		}else{
			iv.setImageResource(R.drawable.sample_profile_pic);
			Log.d("contact","invalid image received");
		}
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
		boolean on = cb.isChecked();

		if (on) {
			cb.setBackgroundResource(R.drawable.green_border);
		} else {
			cb.setBackgroundResource(R.drawable.red_border);
		}
	}


	public void deleteContact(View v){
		openDeleteAlert();
	}

	public void saveContact(View v){
		String phone_no = phoneNoTxtView.getText().toString();
		String name = nameTxtView.getText().toString();
		boolean visibility = cb.isChecked();

		ContactItem contact = new ContactItem(contactId, name, phone_no, visibility);
		if(imageChangedFlag){
			contact.setBitmapImage(this.image);
		}else{
			contact.setBitmapImage(null);
		}

		String url = Constants.URL_TO_UPDATE_CONTACT;
		ContactListUpdater cListUpdater = new ContactListUpdater(this, spinner);
		ContactUpdateRequestModel curm = new ContactUpdateRequestModel(url, this, "edit", "edited a contact");
		curm.setContact(contact);
		cListUpdater.execute(curm);
	}

	public void deleteContact(int id) {
		//inform server about the change
		ConnectivityManager connMgr = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Log.d("conact list","going to delete "+id+"(id)");
			String url = Constants.URL_TO_DELETE_CONTACT;
			url+="?id="+id;

			ContactListVisibilityUpdater cListVisibilityUpdater = new ContactListVisibilityUpdater(this, spinner);
			cListVisibilityUpdater.execute(new ContactUpdateRequestModel(url, null, "delete", "deleted a contact"));

		} else {
			Utilities.showToast(getBaseContext(), (CharSequence)"No network connection", Toast.LENGTH_SHORT);
		} 
	}


	private void openDeleteAlert(){
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setMessage("Are you sure to delete ?");
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
				deleteContact(contactId);
				Constants.contactDirtyFlag = true;
				activity.finish();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				//No button clicked
				break;
			}
		}
	};

	///starting image picker
	public void startImagePicker(View v){
		imageChangedFlag = true;
		Intent intent = new Intent(Intent.ACTION_PICK, 
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("outputX", PHOTO_WIDTH);
		intent.putExtra("outputY", PHOTO_HEIGHT);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		if (requestCode == 1) {
			final Bundle extras = data.getExtras();

			if (extras != null) {
				this.image = extras.getParcelable("data");
				this.image = Utilities.getCircleImage(image, 240, 240);
				this.iv.setImageBitmap(image);
				Log.d("contact new","image selected ");
			}
		}
	}

}
