package com.example.checklist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.checklist.adapters.TileImageAdapter;
import com.example.checklist.utilities.Utilities;

public class ImageGalleryTileView extends Activity {
	GridView myGrid;
	ArrayList<Uri> imagePaths;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_galler_tile_view);

		myGrid = (GridView) findViewById(R.id.gallery_grid);
		imagePaths = Utilities.getImagePaths(this);
		final TileImageAdapter tImageAdapter = new TileImageAdapter(getApplicationContext(), imagePaths);
		myGrid.setAdapter(tImageAdapter);
		myGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 public void onItemClick(AdapterView<?> parent, final View view,int position, long id) {
				 Intent intent = new Intent(getApplicationContext(),FullScreenImageActivity.class);
				 intent.putExtra("position", position);
				 intent.putExtra("image_paths", imagePaths);
				 startActivity(intent);
			 }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_galler_tile_view, menu);
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
}
