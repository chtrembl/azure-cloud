package com.chtrembl.petstoreapp.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WebPages implements Serializable {
	public String webSearchUrl = null;

	public Value[] value = null;

	public String getWebSearchUrl() {
		return webSearchUrl;
	}

	public void setWebSearchUrl(String webSearchUrl) {
		this.webSearchUrl = webSearchUrl;
	}

	public Value[] getValue() {
		return value;
	}

	public void setValue(Value[] value) {
		this.value = value;
	}
}
