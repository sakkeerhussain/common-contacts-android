package com.contacts.common_contacts.async;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.contacts.common_contacts.adapters.ContactListAdapter;
import com.contacts.common_contacts.models.ContactItem;
import com.contacts.common_contacts.utilities.Utilities;

public class ContactListDownloader extends AsyncTask<String, Void , JSONArray> {

	private Context context;
	private ContactListAdapter adapter;
	private ArrayList<ContactItem> itemlist = new ArrayList<ContactItem>();
	private ListView listView;
	//	private Toast toast;
	private ProgressBar spinner; 

	@SuppressLint("ShowToast")
	public ContactListDownloader(Context c, ContactListAdapter adptr, ListView lView, ProgressBar spinner) {
		context = c;
		adapter = adptr;
		listView = lView;
		this.spinner = spinner;

		//initializations
		//		toast = Toast.makeText(c, (CharSequence)"Loading contacts...", Toast.LENGTH_LONG);
	}

	@Override
	protected void onPreExecute() {       
		super.onPreExecute();
		//		listView.setVisibility(View.GONE);
		spinner.setVisibility(View.VISIBLE);
		//		toast.show();
	}

	@Override
	protected JSONArray doInBackground(String... params) {
		String url = params[0];
		try {
			Log.d("contact list", "contact list download started");
			
			
			
			
			return Utilities.getJSONfromURL(url, context);
		} catch (Exception e) {
			return null;
		}
	}

	protected void onPostExecute(JSONArray jsonArray) {
		Log.d("contact list", "contact list download completed");
		//        dialog.dismiss();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject jsonobject = jsonArray.getJSONObject(i);
				int id = jsonobject.getInt("id");
				String name=jsonobject .getString("name");
				String phone_no=jsonobject .getString("phone_no");
				String image = jsonobject.getString("image");
				String visStr = jsonobject.getString("visibility");
				Boolean visibility;
				if(visStr.equals("1")){
					visibility = true;
				}else{
					visibility = false;
				}
				if(visibility){
					Log.d("contact list", i +". ("+id+")"+name+" - "+phone_no+"\n");
	
					ContactItem c = new ContactItem(id, name, phone_no, image, visibility);
					itemlist.add(c);
				}
			}


		} catch (JSONException e) {
			Log.e("contact list", "Exception in post execute "+e.toString());
			e.printStackTrace();
		} catch(NullPointerException e){
			Log.e("contact list", "Empty contact list "+e.toString());
		}


		adapter = new ContactListAdapter(itemlist, context);
		listView.setAdapter(adapter);
		//		toast.cancel();
		spinner.setVisibility(View.GONE);
		//		listView.setVisibility(View.VISIBLE);
		Log.d("contact list", "contact list displaying completed");
		
		///creating notification
		//Utilities.createNotification("Contact List", "Contact List refreshed", context);

	}

}
