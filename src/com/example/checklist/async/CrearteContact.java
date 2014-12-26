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

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.example.checklist.Utilities;
import com.example.checklist.models.ContactItem;

public class CrearteContact extends AsyncTask<ContactItem, Void, Boolean> {

	@Override
	protected Boolean doInBackground(ContactItem... params) {
		ContactItem c = params[0];
		
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
		HttpPost httppost = new HttpPost("http://checklist.host56.com/checklist/insert_contact.php");
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
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

		try{
			string_response = string_response.substring(0, 7); 
		}catch(StringIndexOutOfBoundsException e){
			System.out.print("responce string : "+ string_response);
		}catch (Exception e) {
			System.out.print("responce string : "+ string_response);
			e.printStackTrace();
		}

		if(string_response.equals("success")){
			Log.d("contact new ", "new contact created successfully");
			return true;
		}else{
			Log.e("contact new ", "new contact creation failed");
			return false;
		}
	}

}
