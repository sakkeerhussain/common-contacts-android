package com.example.checklist.threds;

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
import android.util.Base64;
import android.util.Log;

import com.example.checklist.utilities.Utilities;

public class CreateContact extends Thread {
	/////fields
	private String contactName;
	private String phoneNumber;
	private Bitmap image;
	private boolean visibility;
	private boolean deleted;
	public  boolean success;
	public  boolean finished;

	////constructor
	public CreateContact() {
		contactName="";
		phoneNumber="";
		image = null;
		visibility=true;
		deleted=true;
		success = false;
		finished = false;
	}

	public CreateContact(String name, String phoneNumber, Bitmap image, boolean visibility,
			boolean deleted) {
		super();
		this.contactName = name;
		this.phoneNumber = phoneNumber;
		this.image = image;
		this.visibility = visibility;
		this.deleted = deleted;
		success = false;
		finished = false;
	}


	///getter and setter
	public String getContactName() {
		return contactName;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public void setContactName(String name) {
		this.contactName = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isVisibility() {
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public void run() {
		Log.e("thread","samplrafafaf aygedx");

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
		byte [] byte_arr = stream.toByteArray();
		String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
		ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("name", contactName));
		nameValuePairs.add(new BasicNameValuePair("phone_no", phoneNumber));
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
			success = true;
			Log.d("contact new ", "new contact created successfully");
		}else{
			success = false;
			Log.e("contact new ", "new contact creation failed");
		}
		finished = true;
	}

}
