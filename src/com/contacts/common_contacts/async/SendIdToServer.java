package com.contacts.common_contacts.async;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.contacts.common_contacts.utilities.Constants;

public class SendIdToServer extends AsyncTask<String, Void , String > {

//	ProgressDialog pd = null;
	Context ctx = null;

	public SendIdToServer(Context ctx) {
		//		  this.pd = pd;
		this.ctx = ctx;
	}

	@Override
	protected void onPreExecute() {
//		pd = ProgressDialog.show(this.ctx, "Please wait",
//				"Loading please wait..", true);
//		pd.setCancelable(true);

	}

	@Override
	protected String doInBackground(String... params) {
		String regId = params[0];
		String deviceId = params[1];
		try {
			HttpResponse response = null;
			HttpParams httpParameters = new BasicHttpParams();
			HttpClient client = new DefaultHttpClient(httpParameters);
			String url = Constants.URL_TO_GCM_REGISTRATION+"?" + "reg_id="
					+ regId+"&device_id="+deviceId;
			Log.i("Send URL:", url);
			HttpGet request = new HttpGet(url);

			response = client.execute(request);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String webServiceInfo = "";
			String str = "";

			while ((str = rd.readLine()) != null) {
				webServiceInfo += str;
			}

			////avoidable
			webServiceInfo=webServiceInfo.substring(0 , (webServiceInfo.length()- 148));
			webServiceInfo = webServiceInfo.trim();
			Log.d("gcm registration", "Responce from server : " + webServiceInfo);	




		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	protected void onPostExecute(String result) {
//		pd.dismiss();

	}

}
