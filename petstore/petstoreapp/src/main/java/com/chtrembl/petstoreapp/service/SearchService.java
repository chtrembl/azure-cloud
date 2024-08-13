package com.chtrembl.petstoreapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chtrembl.petstoreapp.model.AudioData;
import com.chtrembl.petstoreapp.model.WebPages;

@Service
public interface SearchService {
	public WebPages bingSearch(String query);
	public List<AudioData> audioSearch();
}
