package com.contacts.common_contacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.contacts.common_contacts.async.CreateContact;
import com.contacts.common_contacts.models.ContactItem;
import com.contacts.common_contacts.utilities.Utilities;

public class NewContact extends Activity {

	///constants 
	final int PHOTO_WIDTH = 80;
	final int PHOTO_HEIGHT = 70;


	EditText txtName ;
	EditText txtPhoneNumber ;
	ImageView iv ;
	ProgressBar spinner;
	private Bitmap image;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_contact);

		///hiding spinner
		spinner = (ProgressBar)findViewById(R.id.progressBar);
		spinner.setVisibility(View.GONE);

		txtName = (EditText) findViewById(R.id.name);
		txtPhoneNumber = (EditText)findViewById(R.id.phone_no);
		iv = (ImageView) findViewById(R.id.imageView);
		if(savedInstanceState != null ){
			image = (Bitmap)savedInstanceState.getParcelable("image");
			iv.setImageBitmap(image);
			txtName.setText(savedInstanceState.getCharSequence("name"));
			txtPhoneNumber.setText(savedInstanceState.getCharSequence("phone_no"));
		}else{
			image = Utilities.getCircleImage(
					BitmapFactory.decodeResource(getResources(), R.drawable.sample_profile_pic), 240, 240);
			iv.setImageBitmap(image);
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_contact, menu);
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
		}else{
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable("image", image);
		outState.putString("name", txtName.getText().toString());
		outState.putString("phone_no", txtPhoneNumber.getText().toString());
	}

	///on cancel button click
	public void onCancel(View v){
		finish();
	}

	///on save button click	
	public void onSave(View v){
		///starting spinner
		spinner.setVisibility(View.GONE);

		String name = txtName.getText().toString();
		String phone = txtPhoneNumber.getText().toString();
		if(Utilities.isNetwork((Context)this)){
			ContactItem c = new ContactItem(0, name, phone, "", true );
			c.setBitmapImage(image);
			CreateContact ccObject = new CreateContact((Context)this, spinner);
			ccObject.execute(c);
		}
	}


	///starting image picker
	public void startImagePicker(View v){
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
				image = extras.getParcelable("data");
				image = Utilities.getCircleImage(image, 240, 240);
				iv.setImageBitmap(image);
				Log.d("contact new","image selected ");
			}
		}
	}
}
