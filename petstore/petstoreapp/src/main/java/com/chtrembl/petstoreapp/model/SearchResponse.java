package com.chtrembl.petstoreapp.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SearchResponse implements Serializable {
	public WebPages webPages;

	public WebPages getWebPages() {
		return webPages;
	}

	public void setWebPages(WebPages webPages) {
		this.webPages = webPages;
	}
}
