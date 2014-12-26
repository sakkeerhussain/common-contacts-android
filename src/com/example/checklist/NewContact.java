package com.example.checklist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.checklist.threds.CreateContact;

public class NewContact extends Activity {

	///constants 
	final int PHOTO_WIDTH = 100;
	final int PHOTO_HEIGHT = 100;


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
		spinner = (ProgressBar)findViewById(R.id.progressBarNewContact);
		spinner.setVisibility(View.GONE);

		txtName = (EditText) findViewById(R.id.name_new);
		txtPhoneNumber = (EditText)findViewById(R.id.phone_no_new);
		iv = (ImageView) findViewById(R.id.imageView_new);
		if(savedInstanceState != null ){
			image = (Bitmap)savedInstanceState.getParcelable("image");
			iv.setImageBitmap(image);
			txtName.setText(savedInstanceState.getCharSequence("name"));
			txtPhoneNumber.setText(savedInstanceState.getCharSequence("phone_no"));
		}else{
			iv.setImageResource(R.drawable.profile_pic_sample);
			image = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic_sample);
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
		CreateContact c = new CreateContact(name, phone, image, true, false );
		c.start();
		int i=0;
		while(!c.finished && i < 15){
			try {
				Thread.sleep(1000);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Log.e("i",String.valueOf(i));

		if(i>=15){
			Utilities.showToast(getApplicationContext(), (CharSequence)"Server not responding", Toast.LENGTH_SHORT);
		}else{


			if(c.success){
				Log.d("contact new ", "Contact created successfully");
				Utilities.showToast(getApplicationContext(), (CharSequence)"Contact created successfully", Toast.LENGTH_SHORT);
				setResult(Activity.RESULT_OK);
				finish();
			}else{
				Log.e("contact new ", "Contact creation failed");
				Utilities.showToast(getApplicationContext(), (CharSequence)"Contact creation failed", Toast.LENGTH_SHORT);
			}
		}
		spinner.setVisibility(View.GONE);
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
				iv.setImageBitmap(image);
				Log.d("contact new","image selected ");
			}
		}
	}
}
