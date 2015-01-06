package com.example.checklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.checklist.utilities.Utilities;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;


public class GMapsActivity extends Activity {

   @Override
   public void onCreate(Bundle savedInstanceState) {

       super.onCreate(savedInstanceState);
       setContentView(R.layout.gmap_layout);
       

       GoogleMap map = ((MapFragment) getFragmentManager()
               .findFragmentById(R.id.map)).getMap();
    // Get the message from the intent
	    Intent intent = getIntent();
	    String title = intent.getStringExtra("title");
	    String description = intent.getStringExtra("description");
	    Float lat = intent.getFloatExtra("lattitude", 0);
	    Float lon = intent.getFloatExtra("longitude", 0);
       
       ///sample values
//       float lat = Float.valueOf("-33.867");
//       float lon = Float.valueOf("151.206");
//       String description = "The most beutiful city in kerala.";
//       String title = "Kozhikode";
       
       Utilities.spotOnMap(lat, lon, map, title, description);
//       String text = title+" "+description+" "+lat+" "+lon;
//       Utilities.showToast(getApplicationContext(), (CharSequence)text, Toast.LENGTH_LONG);
   }
   
}
