package com.example.checklist.adapters;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.checklist.R;
import com.example.checklist.utilities.Utilities;


public class ImageAdapter extends BaseAdapter {
	/** The parent context */
	protected Context myContext;
	// Put some images to project-folder: /res/drawable/
	// format: jpg, gif, png, bmp, ...
	protected ArrayList<Uri> myImagePaths;

	/** Simple Constructor saving the 'parent' context. */
	public ImageAdapter(Context c, ArrayList<Uri> imageIds) {
		this.myContext = c;
		this.myImagePaths = imageIds;
	}

	// inherited abstract methods - must be implemented
	// Returns count of images, and individual IDs
	public int getCount() {
		try{
			return this.myImagePaths.size();
		}catch(Exception e){
			return 0;
		}
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	// Returns a new ImageView to be displayed,
	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, 
			ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) myContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.gallery_element_layout, parent, false);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewGalleryItem);
		try{
			File f = new File(myImagePaths.get(position).toString());
			Bitmap bm = Utilities.decodeFile(f);
			imageView.setImageBitmap(bm);
//			if(bm==null){
//				rowView.setLayoutParams(new LayoutParams(0,0));
//			}
		}catch(NullPointerException e){
//			rowView.setLayoutParams(new LayoutParams(0,0));
			Log.println(1, "Exception", e.toString());
		}
		return rowView;
	}
}// ImageAdapter
