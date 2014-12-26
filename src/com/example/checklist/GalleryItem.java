package com.example.checklist;

public class GalleryItem {
	private int image;
	private String string;
	
	
	public GalleryItem(int image, String string) {
		super();
		this.image = image;
		this.string = string;
	}
	public int getImage() {
		return image;
	}
	public void setImage(int image) {
		this.image = image;
	}
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	} 
	
	
}
