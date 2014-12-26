package com.example.checklist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Utilities {
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



	public static JSONArray getJSONfromURL(String url){
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
				sb.append(line + "\n");
			}
			is.close();
			result=sb.toString();
			
			Log.d("contact list","feching completed ( "+result+" ) ");
			
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
	
	
	/*
	 * used to show confirm dialog
	 */
	
//	public boolean showDialog(Context c, String title, 
//			String message, String yesButtonText, String noButtonText){
//		//Ask the user if they want to quit
//        new AlertDialog.Builder(c)
//        .setIcon(android.R.drawable.ic_dialog_alert)
//        .setTitle(title)
//        .setMessage(message)
//        .setPositiveButton(yesButtonText, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//	
//	                //Stop the activity
//	                YourClass.this.finish();    
//            }
//
//        })
//        .setNegativeButton(noButtonText, null)
//        .show();
//        
//        
//        return true;
//	}
	
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
    
    
}
