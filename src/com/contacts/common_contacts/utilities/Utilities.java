package com.contacts.common_contacts.utilities;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.contacts.common_contacts.ContactList;
import com.contacts.common_contacts.R;
import com.contacts.common_contacts.models.ContactItem;

public class Utilities {
	public static final int CONTACT_REFRESHED_NOTIFICATION_ID=1;


	public static void showToast(Context context, CharSequence text, int duration ){
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	public static JSONArray getJSONfromURL(String url, Context context){
		//initialize
		InputStream is = null;
		String result = "";
		JSONArray jsonArray = null;

		//http post
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		}catch(Exception e){
			Log.e("log_tag", "Error in http connection "+e.toString());
		}


		//convert response to string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line );//+ "\n");
			}
			is.close();
			result=sb.toString();
			////TODO to remove server appended contents
			result=result.substring(0 , (result.length()- 148));
			result = result.trim();

			Log.d("contact list","feching completed ( "+result+" ) ");
			saveToContactFile(result, context);
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
		}

		//try parse the string to a JSON object
		try{
			jsonArray = new JSONArray(result);
		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data "+e.toString());
		}

		return jsonArray;
	}

	private static void saveToContactFile(String result, Context context) throws IOException {
		Constants.contactDirtyFlag = false;
		FileOutputStream fos = null ;
		try{	
			fos = context.openFileOutput(Constants.CONTACT_LIST_FILE_NAME, Context.MODE_PRIVATE);
			fos.write(result.getBytes());
			fos.close();
			Log.d("contact list file","contact list file "+Constants.CONTACT_LIST_FILE_NAME + "updated");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
				fos.close();
			}
		}
	}

	/*
	 * updating contact visibility
	 */
	public static Boolean updateContactVisibility(String url){
		//initialize
		InputStream is = null;
		Boolean result = false;

		//http post
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			Log.d("contact update", "url sent : "+url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		}catch(Exception e){
			Log.e("log_tag", "Error in http connection "+e.toString());
		}


		//convert response to string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			String line = null;
			line = reader.readLine();
			is.close();
			line =  line.substring(0, 7);    //line => responce from server

			if(line.equals("success")){
				result = true;
			}else{
				result = false;
			}

			Log.d("contact list","updating completed with responce: "+line+"  result (status)  : "+result);

		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
			result = false;
		}



		return result;
	}


	/*
	 * updating contact
	 */
	public static Boolean updateContact(String url, ContactItem contact){
		//initialize
		InputStream is = null;
		Boolean result = false;

		//http post
		try{
			HttpClient httpclient = new DefaultHttpClient();

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(contact.getId())));
			nameValuePairs.add(new BasicNameValuePair("name", contact.getName()));
			nameValuePairs.add(new BasicNameValuePair("phone_no", contact.getPhone_no()));
			String visibility;
			if(contact.getVisibility()){
				visibility = "1";
			}else{
				visibility = "0";
			}
			nameValuePairs.add(new BasicNameValuePair("visibility", visibility));
			if(contact.getBitmapImage() != null){
				Bitmap image = contact.getBitmapImage();
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] imageByteArray = stream.toByteArray();
				String imageStr = Base64.encodeToString(imageByteArray, Base64.DEFAULT);
				nameValuePairs.add(new BasicNameValuePair("image", imageStr));
			}else{
				nameValuePairs.add(new BasicNameValuePair("image", ""));
			}

			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			Log.d("contact update", "url sent : "+url);
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		}catch(Exception e){
			e.printStackTrace();
			Log.e("log_tag", "Error in http connection "+e.toString());
		}


		//convert response to string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			String line = null;
			line = reader.readLine();
			is.close();
			if(line.length() > 7 ){
				line =  line.substring(0, 7);    //line => responce from server
			}
			
			if(line.equals("success")){
				result = true;
			}else{
				result = false;
			}

			Log.d("contact list","updating completed with responce: "+line+"  result (status)  : "+result);

		}catch(Exception e){
			e.printStackTrace();
			Log.e("contact list", "Error converting result "+e.toString());
			result = false;
		}



		return result;
	}

	public static String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{

		StringBuffer buffer = new StringBuffer();
		InputStream inputStream = response.getEntity().getContent();
		int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..


		if (contentLength < 0){
			return "";
		}
		else{
			String res = "";
			byte[] data = new byte[512];
			int len = 0;
			try
			{
				while (-1 != (len = inputStream.read(data)) )
				{
					buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer…..
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				inputStream.close(); // closing the stream…..
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			res = buffer.toString();     // converting stringbuffer to string…..

			return res;
		}
	}


	public static boolean isNetwork(Context c) {
		Activity activity = (Activity)c;
		ConnectivityManager connMgr = 
				(ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			Utilities.showToast(c, "No network availiable", Toast.LENGTH_SHORT);
			return false;
		} 
	}


	public static void createNotification(String contentTitle, String contentText, Context mContext) {

		Intent resultIntent = new Intent(mContext, ContactList.class);
		resultIntent.putExtra("update_request", true);
		resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);


		PendingIntent resultPendingIntent =
				PendingIntent.getActivity(
						mContext,
						0,
						resultIntent,
						PendingIntent.FLAG_UPDATE_CURRENT
						);

		//Build the notification using Notification.Builder
		Notification.Builder builder = new Notification.Builder(mContext)
		.setSmallIcon(R.drawable.contact_icon)
		.setAutoCancel(true)
		.setContentTitle(contentTitle)
		.setContentIntent(resultPendingIntent)
		.setContentText(contentText);

		NotificationManager mNotificationManager =
				(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		//Get current notification
		Notification mNotification = builder.getNotification();

		//Show the notification
		mNotificationManager.notify(CONTACT_REFRESHED_NOTIFICATION_ID, mNotification);
	}

	public static Bitmap getCircleImage(Bitmap image ,int height, int width){
		Bitmap circleImage = Bitmap.createBitmap(width, height , Bitmap.Config.ARGB_8888);
		Bitmap imageWithSpecifiedWidthAndHeight = Bitmap.createScaledBitmap(image, width, height, true);
		BitmapShader shader = new BitmapShader (imageWithSpecifiedWidthAndHeight,  TileMode.CLAMP, TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setShader(shader);
		Canvas c = new Canvas(circleImage);
		c.drawCircle( width/2,height/2,  width/2, paint);
		return circleImage;
	}

}
