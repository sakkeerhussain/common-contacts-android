package com.contacts.common_contacts.models;

import android.content.Context;

public class ContactUpdateRequestModel {
	String url;
	Context caller;	
	String action;
	String pushMessage;
	ContactItem contact;
	
	public ContactUpdateRequestModel(String url, Context caller, String action, String pushMessage) {
		super();
		this.url = url;
		this.caller = caller;
		this.action = action;
		this.pushMessage = pushMessage;
		this.contact = null;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Context getCaller() {
		return caller;
	}
	public void setCaller(Context caller) {
		this.caller = caller;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPushMessage() {
		return pushMessage;
	}

	public void setPushMessage(String pushMessage) {
		this.pushMessage = pushMessage;
	}

	public ContactItem getContact() {
		return contact;
	}

	public void setContact(ContactItem contact) {
		this.contact = contact;
	}
	
	
}
