package com.example.checklist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.example.checklist.adapters.FullScreenImageAdapter;

public class FullScreenImageActivity extends Activity {

	//private Utilities utils;
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	private ArrayList<Uri> imageids;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_gallery_view_pager);


		viewPager = (ViewPager) findViewById(R.id.pager);
		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		imageids = Utilities.getImagePaths(this);
		adapter = new FullScreenImageAdapter(FullScreenImageActivity.this,imageids);

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);

		Button btnClose = (Button) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new View.OnClickListener() {            
			@Override
			public void onClick(View v) {
				FullScreenImageActivity.this.finish();
			}
		});

	}

}