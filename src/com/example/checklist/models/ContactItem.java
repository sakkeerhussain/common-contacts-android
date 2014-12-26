package com.example.checklist.models;

import android.graphics.Bitmap;


public class ContactItem {
	private int id;
	private String name;
	private String phone_no;
	private String image;
	private Bitmap bitmapImage; 
	private Boolean visibility;
	

	public Bitmap getBitmapImage() {
		return bitmapImage;
	}
	public void setBitmapImage(Bitmap bitmapImage) {
		this.bitmapImage = bitmapImage;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone_no() {
		return phone_no;
	}
	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}
	public Boolean getVisibility() {
		return visibility;
	}
	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}
	public ContactItem(int id, String name, String phone_no, String image, Boolean visibility) {
		super();
		this.id = id;
		this.image = image;
		this.name = name;
		this.phone_no = phone_no;
		this.visibility = visibility;
	}
	
	
}
