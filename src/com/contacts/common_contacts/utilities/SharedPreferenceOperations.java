package com.contacts.common_contacts.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceOperations {
	
	public void savePreferences(String key, String value, Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}


	public String readPreferences(String key, Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String value = sharedPreferences.getString(key, "");
		return value;
	}
}
