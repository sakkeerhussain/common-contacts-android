package com.example.checklist.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checklist.R;
import com.example.checklist.models.ContactItem;

public class ContactListAdapter extends ArrayAdapter<ContactItem> {

	private List<ContactItem> itemList;
	private Context context;

	public ContactListAdapter(List<ContactItem> itemList, Context ctx) {
		super(ctx, R.layout.contact_list_item_layout, itemList);
		this.itemList = itemList;
		this.context = ctx;       
	}

	public int getCount() {
		if (itemList != null)
			return itemList.size();
		return 0;
	}

	public ContactItem getItem(int position) {
		if (itemList != null)
			return itemList.get(position);
		return null;
	}

	public long getItemId(int position) {
		if (itemList != null)
			return itemList.get(position).hashCode();
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.contact_list_item_layout, parent, false);
		}

		ContactItem c = itemList.get(position);
		TextView name = (TextView) v.findViewById(R.id.name);
		name.setText(c.getName());

		TextView phoneNumber = (TextView) v.findViewById(R.id.phone_no);
		phoneNumber.setText(c.getPhone_no());

		ImageView profilePic = (ImageView) v.findViewById(R.id.contact_image);
		byte[] imageAsBytes = Base64.decode(c.getImage().getBytes(),Base64.DEFAULT);
		Bitmap bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
		if(bm!=null){
			profilePic.setImageBitmap(bm);
		}else{
			profilePic.setImageResource(R.drawable.profile_pic_sample);
		}
		return v;

	}

	public List<ContactItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<ContactItem> itemList) {
		this.itemList = itemList;
	}     
}
