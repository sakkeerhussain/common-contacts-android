package com.example.checklist.adapters;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checklist.R;
import com.example.checklist.Utilities;


public class FullScreenImageAdapterSocialShare extends FullScreenImageAdapter{

	public FullScreenImageAdapterSocialShare(Activity activity,
			ArrayList<Uri> imageids) {
		super(activity, imageids);
	}
	
	  @Override
	    public Object instantiateItem(ViewGroup container, int position) {
	        ImageView imgDisplay;
	        TextView txtPosition;
	  
	        inflater = (LayoutInflater) _activity
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View viewLayout = inflater.inflate(R.layout.full_screen_image_layout, container,
	                false);
	  
	        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
	        txtPosition = (TextView)viewLayout.findViewById(R.id.txt_position); 
	         
	        try{
	        	File f = new File(this._imageids.get(position).toString());
				Bitmap bm = Utilities.decodeFile(f);
				imgDisplay.setImageBitmap(bm);
//				if(bm==null){
//					viewLayout.setLayoutParams(new LayoutParams(0,0));
//				}
			}catch(NullPointerException e){
//				viewLayout.setLayoutParams(new LayoutParams(0,0));
				Log.println(1, "Exception", e.toString());
			}
	        txtPosition.setText((CharSequence)((position+1)+" of "+_imageids.size()));
	         
	        ((ViewPager) container).addView(viewLayout);
	  
	        return viewLayout;
	    }
	     

}
