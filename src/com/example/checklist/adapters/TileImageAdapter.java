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
import android.widget.ImageView;

import com.example.checklist.R;
import com.example.checklist.Utilities;

public class TileImageAdapter extends ImageAdapter{

	public TileImageAdapter(Context c, ArrayList<Uri> imagePaths) {
		super(c, imagePaths);
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, 
			ViewGroup parent) {
		View rowView=null;
		LayoutInflater inflater = (LayoutInflater) myContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = inflater.inflate(R.layout.tile_gallery_element_layout, parent, false);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewGalleryItem);
		try{	
			File f = new File(myImagePaths.get(position).toString());
			Bitmap bm = Utilities.decodeFile(f);
			imageView.setImageBitmap(bm);
//			if(bm==null){
//				rowView.setLayoutParams(new LayoutParams(0,0));
//			}
		}
		catch(Exception e){
//			rowView.setLayoutParams(new LayoutParams(0,0));
			Log.println(1, "Exception", e.toString());
		}
		return rowView;
	}
}
