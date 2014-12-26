package com.example.checklist.models;

public class ListItem {
	private int imageResource;
	private String mainString;
	private String description;
	
	
	
	public ListItem() {
		super();
	}
	
	public int getImageResource() {
		return imageResource;
	}
	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}
	public String getMainString() {
		return mainString;
	}
	public void setMainString(String mainString) {
		this.mainString = mainString;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
