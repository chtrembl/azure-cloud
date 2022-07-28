package com.chtrembl.petstoreapp.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Value implements Serializable {
	public String id = null;
	public String name = null;
	public String url = null;
	public String isFamilyFriendly = null;
	public String displayUrl = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIsFamilyFriendly() {
		return isFamilyFriendly;
	}

	public void setIsFamilyFriendly(String isFamilyFriendly) {
		this.isFamilyFriendly = isFamilyFriendly;
	}

	public String getDisplayUrl() {
		return displayUrl;
	}

	public void setDisplayUrl(String displayUrl) {
		this.displayUrl = displayUrl;
	}
}
