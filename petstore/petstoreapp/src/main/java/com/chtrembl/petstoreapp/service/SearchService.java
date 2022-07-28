package com.chtrembl.petstoreapp.service;

import org.springframework.stereotype.Service;

import com.chtrembl.petstoreapp.model.WebPages;

@Service
public interface SearchService {
	public WebPages bingSearch(String query);
}
