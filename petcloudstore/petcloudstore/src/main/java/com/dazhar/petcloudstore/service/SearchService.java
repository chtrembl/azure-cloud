package com.dazhar.petcloudstore.service;

import org.springframework.stereotype.Service;

import com.dazhar.petcloudstore.model.WebPages;

@Service
public interface SearchService {
	public WebPages bingSearch(String query);
}
