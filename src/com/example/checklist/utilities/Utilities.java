package com.example.checklist.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.checklist.ContactList;
import com.example.checklist.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Utilities {
	public static final int CONTACT_REFRESHED_NOTIFICATION_ID=1;


	public static void showToast(Context context, CharSequence text, int duration ){
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	public static void spotOnMap(float lat, float lon, GoogleMap map, String title, String description){
		// Get a handle to the Map Fragment
		LatLng location = new LatLng(lat, lon);

		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

		map.addMarker(new MarkerOptions()
		.title(title)
		.snippet(description)
		.position(location));
	}

	public static ArrayList<Uri> getImagePaths(Activity activity){
		final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
		final String orderBy = MediaStore.Images.Media._ID;
		//Stores all the images from the gallery in Cursor
		Cursor cursor = activity.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		//Total number of images
		int count = cursor.getCount();

		//Create an array to store path to all the images
		ArrayList<Uri> arrPath = new ArrayList<Uri>();

		for (int i = 0; i < count; i++) {
			cursor.moveToPosition(i);
			int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			//Store the path of the image
			String pathStr = cursor.getString(dataColumnIndex);
			Uri uri = Uri.parse(pathStr);
			arrPath.add(uri);
		}
		return arrPath; 
	}


	public static Bitmap decodeFile(File f) {

		// decode image size

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		o.inDither = false; // Disable Dithering mode

		o.inPurgeable = true; // Tell to gc that whether it needs free memory,
		// the Bitmap can be cleared

		o.inInputShareable = true; // Which kind of reference will be used to
		// recover the Bitmap data after being
		// clear, when it will be used in the future
		try {
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Find the correct scale value. It should be the power of 2.
		final int REQUIRED_SIZE = 200;
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		//        int scale = 1;
		while (true) {
			if (width_tmp / 1.5 < REQUIRED_SIZE && height_tmp / 1.5 < REQUIRED_SIZE)
				break;
			width_tmp /= 1.5;
			height_tmp /= 1.5;
			//            scale *= 1.5;
		}

		// decode with inSampleSize
		//        BitmapFactory.Options o2 = new BitmapFactory.Options();
		// o2.inSampleSize=scale;
		o.inDither = false; // Disable Dithering mode

		o.inPurgeable = true; // Tell to gc that whether it needs free memory,
		// the Bitmap can be cleared

		o.inInputShareable = true; // Which kind of reference will be used to
		// recover the Bitmap data after being
		// clear, when it will be used in the future
		// return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		try {

			//          return BitmapFactory.decodeStream(new FileInputStream(f), null,
			//                  null);
			Bitmap bitmap= BitmapFactory.decodeStream(new FileInputStream(f), null, null);
			System.out.println(" IW " + width_tmp);
			System.out.println("IHH " + height_tmp);           
			int iW = width_tmp;
			int iH = height_tmp;

			return Bitmap.createScaledBitmap(bitmap, iW, iH, true);

		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
			// clearCache();

			// System.out.println("bitmap creating success");
			System.gc();
			return null;
			// System.runFinalization();
			// Runtime.getRuntime().gc();
			// System.gc();
			// decodeFile(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

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
			////avoidable
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
			line =  line.substring(0, 7);

			if(line.equals("success")){
				result = true;
			}else{
				result = false;
			}

			Log.d("contact list","updating completed  "+result+"(status) ");

		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
			result = false;
		}



		return result;
	}

	public static String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{

		String res = "";
		StringBuffer buffer = new StringBuffer();
		InputStream inputStream = response.getEntity().getContent();
		int contentLength = (int) response.getEntity().getContentLength(); //getting content length…..


		if (contentLength < 0){
		}
		else{
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
		}
		return res;
	}


	public static boolean checkNetwork(Context c) {
		Activity activity = (Activity)c;
		ConnectivityManager connMgr = 
				(ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		} 
	}


	public static void createNotification(String contentTitle, String contentText, Context mContext) {
		
		Intent resultIntent = new Intent(mContext, ContactList.class);
		resultIntent.putExtra("update_request", true);

		PendingIntent resultPendingIntent =
		    PendingIntent.getActivity(
		    mContext,
		    0,
		    resultIntent,
		    PendingIntent.FLAG_CANCEL_CURRENT
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

}
