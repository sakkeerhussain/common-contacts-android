package com.example.checklist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.checklist.adapters.FullScreenImageAdapter;
import com.example.checklist.adapters.FullScreenImageAdapterSocialShare;

public class SocialShare extends Activity {

	//private Uri imageUri;
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	private ArrayList<Uri> imageids;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_share);

		EditText editText = (EditText) findViewById(R.id.search);

		viewPager = (ViewPager) findViewById(R.id.pager_at_social_share);
		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		imageids = Utilities.getImagePaths(this);
		adapter = new FullScreenImageAdapterSocialShare(SocialShare.this,imageids);

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);

		editText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					openFacebookShare(null);
					openTwitterShare(null);
					handled = true;
				}
				return handled;
			}
		});


		//Utilities.showToast(getApplicationContext(), (CharSequence)imageUri.toString(), Toast.LENGTH_LONG);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.social_share, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	//events

	public void openFacebookShare(View view){
		Intent intent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
		if (intent != null) {
			/* We found the activity now start the activity */
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			//	        sharingIntent.setType("text/plain");
			sharingIntent.setType("image/jpg");
			int position = viewPager.getCurrentItem();
			Uri imageUri = imageids.get(position);
			sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
			sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Sample Subject");
			EditText editText = (EditText) findViewById(R.id.search);
			String textStr = editText.getText().toString();
			sharingIntent.putExtra(Intent.EXTRA_TEXT, textStr);
			//	        sharingIntent.putExtra(Intent.EXTRA_TITLE, textStr+" http://www.google");

			sharingIntent.setPackage("com.facebook.katana");
			startActivity(sharingIntent);
		} else {
			/* Bring user to the market or let them choose an app? */
			intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("market://details?id=" + "com.facebook.katana"));
			startActivity(intent);
		}
	}

	public void openTwitterShare(View view){
		Intent intent = getPackageManager().getLaunchIntentForPackage("com.twitter.android");
		if (intent != null) {
			/* We found the activity now start the activity */
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			//	        sharingIntent.setType("text/plain");
			sharingIntent.setType("image/jpg");
			int position = viewPager.getCurrentItem();
			Uri imageUri = imageids.get(position);
			sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
			sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
			sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Sample Subject");
			EditText editText = (EditText) findViewById(R.id.search);
			String textStr = editText.getText().toString();
			sharingIntent.putExtra(Intent.EXTRA_TEXT, textStr);
			sharingIntent.setPackage("com.twitter.android");
			startActivity(sharingIntent);
		} else {
			/* Bring user to the market or let them choose an app? */
			intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("market://details?id=" + "com.twitter.android"));
			startActivity(intent);
		}
	}

	public Uri copyImageToSdCard(int imageid){
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageid);
		File sd = Environment.getExternalStorageDirectory();
		String fileName = "test-"+imageid+".png";
		File dest = new File(sd, fileName);
		Uri imageUri = Uri.fromFile(dest);

		try {
			FileOutputStream out;
			out = new FileOutputStream(dest);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageUri;
	}
}
