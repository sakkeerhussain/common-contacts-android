package com.example.checklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.checklist.adapters.MainListAdapter;
import com.example.checklist.models.ListItem;
import com.example.checklist.utilities.Utilities;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//System.out.print("app created");
		
		final ListView listview = (ListView) findViewById(R.id.listView1);
	    String[] stringValues = new String[] { "Locate On Map", "Share In Social Media", "Image Gallery", "Contact List" };
	    ListItem[] values = new ListItem[4];
	    values[0] = new ListItem();
	    values[0].setMainString(stringValues[0]);
	    values[0].setImageResource(R.drawable.gmap_icon);
	    values[1] = new ListItem();
	    values[1].setMainString(stringValues[1]);
	    values[1].setImageResource(R.drawable.share_icon);
	    values[2] = new ListItem();
	    values[2].setMainString(stringValues[2]);
	    values[2].setImageResource(R.drawable.gallary_icon);
	    values[3] = new ListItem();
	    values[3].setMainString(stringValues[3]);
	    values[3].setImageResource(R.drawable.contact_icon);
	    

	    final MainListAdapter adapter = new MainListAdapter(getApplicationContext() , stringValues, values);
	    listview.setAdapter(adapter);
	    
	    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	@Override
	        public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
	    		switch (position) {
	    		
				case 0:
					Intent intentMap = new Intent(getApplicationContext(), GMapsActivity.class);
					intentMap.putExtra("lattitude", Float.valueOf("11.2764"));
					intentMap.putExtra("longitude", Float.valueOf("75.7632072"));
					intentMap.putExtra("title", "QBurst");
					intentMap.putExtra("description", "Technology Vendor of the Year");
					startActivity(intentMap);
					break;
				case 1 :
					Intent intentShare = new Intent(getApplicationContext(), SocialShare.class);
					startActivity(intentShare);
					break;
				case 2:
					Intent intentGallery = new Intent(getApplicationContext(), ImageGalleryTileView.class);
					startActivity(intentGallery);	
					break;
				case 3:	
					Intent intentContact = new Intent(getApplicationContext(), ContactList.class);
//					intentContact.putExtra("image_no", 0);
					startActivity(intentContact);
					break;
				default:
					break;
				}
	        }
	    });
	    
	    
	    
	  }



		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
	///events
	public void openMapActivity(View view){
		CharSequence text = "Open map Clicked";
		Utilities.showToast(getApplicationContext(), text, Toast.LENGTH_SHORT);
	}
	public void openShareActivity(View view){
		CharSequence text = "Open share Clicked";
		Utilities.showToast(getApplicationContext(), text, Toast.LENGTH_SHORT);
	}
	public void openGalleryActivity(View view){
		CharSequence text = "Open gallery Clicked";
		Utilities.showToast(getApplicationContext(), text, Toast.LENGTH_SHORT);
	}
	
}


