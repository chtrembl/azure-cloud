package com.dazhar.petstoreapp.service;

import org.springframework.stereotype.Service;

import com.dazhar.petstoreapp.model.WebPages;

@Service
public interface SearchService {
	public WebPages bingSearch(String query);
}
