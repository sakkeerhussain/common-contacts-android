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

import com.contacts.common_contacts.EditContactList;
import com.contacts.common_contacts.adapters.EditContactListAdapter;
import com.contacts.common_contacts.models.ContactItem;
import com.contacts.common_contacts.utilities.Utilities;

public class EditContactListDownloader extends AsyncTask<String, Void , JSONArray> {

	private static Context context;
	private EditContactListAdapter adapter;
	private ArrayList<ContactItem> itemlist = new ArrayList<ContactItem>();
	private ListView listView;
//	private Toast toast;
	private EditContactList editCl;
	private ProgressBar spinner; 

	@SuppressLint("ShowToast")
	public EditContactListDownloader(Context c, EditContactListAdapter adptr, ListView lView, EditContactList editCl, ProgressBar spinner) {
		context = c;
		this.adapter = adptr;
		this.listView = lView;
		this.editCl = editCl;
		this.spinner = spinner;
		
		//initializations
//		toast = Toast.makeText(c, (CharSequence)"Loading contacts...", Toast.LENGTH_LONG);
	}

	@Override
	protected void onPreExecute() {       
		super.onPreExecute();
		spinner.setVisibility(View.VISIBLE);		
//		toast.show();
	}

	@Override
	protected JSONArray doInBackground(String... params) {
		String url = params[0];
		try {
			Log.d("contact list", "contact list (all) download started");
			return Utilities.getJSONfromURL(url, context);
		} catch (Exception e) {
			return null;
		}
	}

	protected void onPostExecute(JSONArray jsonArray) {
		Log.d("contact list", "contact list (all) download completed");
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
				Log.d("contact list", i +". ("+id+")"+name+" - "+phone_no+" - (visibility):"+visibility+"\n");

				ContactItem c = new ContactItem(id, name, phone_no, image, visibility);
				itemlist.add(c);
			}


		} catch (JSONException e) {
			Log.e("contact list", "Exception in post execute "+e.toString());
			e.printStackTrace();
		} catch(NullPointerException e){
			Log.e("contact list", "Empty contact list "+e.toString());
		}


		adapter = new EditContactListAdapter(itemlist, context, editCl);
//		toast.cancel();
		spinner.setVisibility(View.GONE);
		listView.setAdapter(adapter);
		Log.d("contact list", "contact list (all) displaying completed");
	}

}
