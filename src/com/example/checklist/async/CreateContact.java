package com.example.checklist.async;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.checklist.models.ContactItem;
import com.example.checklist.utilities.Constants;
import com.example.checklist.utilities.Utilities;

public class CreateContact extends AsyncTask<ContactItem, Void, String> {

	private ProgressBar spinner;
	private Context ctx;
	public CreateContact(Context c, ProgressBar spinner){
		this.spinner = spinner;
		this.ctx = c ;
	}

	@Override
	protected void onPreExecute() {
		spinner.setVisibility(View.VISIBLE);
	}

	@Override
	protected String doInBackground(ContactItem... params) {
		ContactItem c = params[0];
		Log.d("contact new","going to add new contact "+c.getName()+"(name) "+c.getPhone_no()+"(phone)");

		Bitmap image = c.getBitmapImage();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
		byte [] byte_arr = stream.toByteArray();
		String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
		ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("name", c.getName()));
		nameValuePairs.add(new BasicNameValuePair("phone_no", c.getPhone_no()));
		nameValuePairs.add(new BasicNameValuePair("image", image_str));


		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL_TO_ADD_NEW_CONTACT);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String string_response = "";
		try {
			string_response = Utilities.convertResponseToString(response);
			Log.d("contact new", "new contact responce : "+string_response);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(string_response.length() > 148){
			string_response = string_response.substring(0,  (string_response.length()- 148));
		}

		String responseSubStr = "";
		try{
			responseSubStr = string_response.substring(0, 7); 
		}catch(StringIndexOutOfBoundsException e){
			System.out.print("responce string : "+ string_response);
		}catch (Exception e) {
			System.out.print("responce string : "+ string_response);
			e.printStackTrace();
		}

		if(responseSubStr.equals("success")){
			Log.d("contact new ", "new contact created successfully");
			return "success";
		}else{
			Log.e("contact new ", "new contact creation failed");
			return string_response;
		}
	}

	@Override
	protected void onPostExecute(String result) {
		spinner.setVisibility(View.GONE);
		if(result.equals("success")){
			Log.d("contact new ", "Contact created successfully");
			Utilities.showToast(ctx, (CharSequence)"Contact created successfully", Toast.LENGTH_SHORT);
			Activity activity = (Activity)ctx;
			activity.setResult(Activity.RESULT_OK);
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					PushUpdateMesssage pu = new PushUpdateMesssage(ctx);
					pu.execute("One%20contact%20added");
				}
			});

			activity.finish();

		}else{
			Log.e("contact new ", "Contact creation failed with message ("+result+")");
			if(result.equals("") || result == null){
				Utilities.showToast(ctx, (CharSequence)"Contact creation failed", Toast.LENGTH_LONG);
			}else{
				Utilities.showToast(ctx, (CharSequence)result, Toast.LENGTH_LONG);
			}
		}
	}
}
