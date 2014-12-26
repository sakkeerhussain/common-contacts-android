package com.example.checklist.adapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checklist.EditContactList;
import com.example.checklist.R;
import com.example.checklist.SingleContact;
import com.example.checklist.models.ContactItem;

public class EditContactListAdapter extends ArrayAdapter<ContactItem> {

	private List<ContactItem> itemList;
	private Context context;
	private EditContactList editCL;

	public EditContactListAdapter(List<ContactItem> itemList, Context ctx, EditContactList ecl) {
		super(ctx, R.layout.contact_list_item_layout, itemList);
		this.itemList = itemList;
		this.context = ctx;       
		this.editCL = ecl;
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
			v = inflater.inflate(R.layout.edit_contact_list_item_layout, parent, false);
		}

		ContactItem c = itemList.get(position);
		
		TextView name = (TextView) v.findViewById(R.id.name);		
		name.setText(c.getName());

		TextView phoneNumber = (TextView) v.findViewById(R.id.phone_no);
		phoneNumber.setText(c.getPhone_no());
		
		CheckBox cbVisible = (CheckBox) v.findViewById(R.id.checkBoxVisibility);
		cbVisible.setChecked(c.getVisibility());
		int contactId[] = {c.getId(),position};
		cbVisible.setTag(contactId);
		cbVisible.setOnClickListener( new View.OnClickListener() {  
		     public void onClick(View v) {  
		      CheckBox cb = (CheckBox) v ;  
		      int contactId[] = (int[])cb.getTag();  
		      Log.d("contact list", "checked element with id: "+contactId[0]+" position: "+contactId[1]+" status: "+cb.isChecked());
		      itemList.get(contactId[1]).setVisibility(cb.isChecked());
		      editCL.setVisibility(contactId[0], cb.isChecked());
		     }  
		    }); 

		ImageView profilePic = (ImageView) v.findViewById(R.id.contact_image_edit);
		byte[] imageAsBytes = Base64.decode(c.getImage().getBytes(),Base64.DEFAULT);
		Bitmap bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
		if(bm!=null){
			profilePic.setImageBitmap(bm);
		}else{
			profilePic.setImageResource(R.drawable.profile_pic_sample);
		}
		
		///setting id of this contact as tag
		v.setTag(c.getId());
//		editCL.registerForContextMenu(v);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int contactId = (Integer)v.getTag();
				TextView tv = (TextView) v.findViewById(R.id.phone_no);
				String phoneNumber = tv.getText().toString();
				tv = (TextView) v.findViewById(R.id.name);
				String name =  tv.getText().toString();
				CheckBox cb = (CheckBox) v.findViewById(R.id.checkBoxVisibility);
				boolean visiblity = cb.isChecked();
				ImageView im = (ImageView) v.findViewById(R.id.contact_image_edit);
				im.buildDrawingCache();
				Bitmap bm = im.getDrawingCache();
//				ByteArrayOutputStream bs = new ByteArrayOutputStream();
//				b.compress(Bitmap.CompressFormat.PNG, 50, bs);
//				i.putExtra("byteArray", bs.toByteArray());
				 
///TODO
				Intent contact = new Intent(context, SingleContact.class);
				contact.putExtra("contact_id", contactId);
				contact.putExtra("phone_no", phoneNumber);
				contact.putExtra("name", name);
				contact.putExtra("visibility", visiblity);
				contact.putExtra("image", bm);
				
				editCL.startActivityForResult(contact, 1);
	
				Log.d("contact list","clicked on "+contactId+"(contact id) ");
			}

		} );
		return v;

	}

	public List<ContactItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<ContactItem> itemList) {
		this.itemList = itemList;
	}     
}
