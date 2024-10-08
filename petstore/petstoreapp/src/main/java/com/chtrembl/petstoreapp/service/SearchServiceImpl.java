package com.chtrembl.petstoreapp.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import com.chtrembl.petstoreapp.model.AudioData;
import com.chtrembl.petstoreapp.model.ContainerEnvironment;
import com.chtrembl.petstoreapp.model.SearchResponse;
import com.chtrembl.petstoreapp.model.Value;
import com.chtrembl.petstoreapp.model.WebPages;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SearchServiceImpl implements SearchService {
	private static Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

	final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);

	@Autowired
	private ContainerEnvironment containerEnvironment;

	private WebClient bingSearchWebClient = null;
	private WebClient audioSearchClient = null;

	@PostConstruct
	public void initialize() {
		this.objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true); // Allow single quotes
  
		this.bingSearchWebClient = WebClient.builder().baseUrl(this.containerEnvironment.getBingSearchURL())
				.build();
		this.audioSearchClient = WebClient.builder().baseUrl(this.containerEnvironment.getAudioSearchServiceURL())
				.build();
	}

	@Override
	public WebPages bingSearch(String query) {
		Exception e = null;

		try {
			String response = this.bingSearchWebClient.get().uri("v7.0/search?q=" + query)
					.accept(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.header("Ocp-Apim-Subscription-Key", this.containerEnvironment.getBingSearchSubscriptionKey())
					.header("Cache-Control", "no-cache").retrieve()
                    .bodyToMono(String.class)
                    .block();

			SearchResponse searchResponse = this.objectMapper.readValue(response, SearchResponse.class);
			return searchResponse.getWebPages();

		} catch (WebClientException wce) {
			e = wce;
		} catch (IllegalArgumentException iae) {
			e = iae;
		} catch (JsonMappingException jme) {
			e = jme;
		} catch (JsonProcessingException jpe) {
			e = jpe;
		}
		WebPages webpages = new WebPages();
		webpages.value = new Value[1];
		Value value = new Value();
		value.setName(e.getMessage());
		webpages.value[0] = value;

		return webpages;
	}

	@Override
	public List<AudioData> audioSearch() {
		Exception e = null;

		List<AudioData> audioData = new ArrayList<AudioData>();
		
		try {
			String response = this.audioSearchClient.get()
					.accept(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.header("x-functions-key", this.containerEnvironment.getAudioSearchSubscriptionKey())
					.header("Cache-Control", "no-cache").retrieve()
                    .bodyToMono(String.class)
                    .block();

			// Configure ObjectMapper to allow coercion of empty strings to null
			ObjectMapper objectMapper = this.objectMapper.copy();
			objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

			// Deserialize the JSON array response into a list of AudioData objects
			audioData = objectMapper.readValue(response, objectMapper.getTypeFactory().constructCollectionType(List.class, AudioData.class));

		} catch (WebClientException wce) {
			e = wce;
		} catch (IllegalArgumentException iae) {
			e = iae;
		} catch (JsonMappingException jme) {
			e = jme;
		} catch (JsonProcessingException jpe) {
			e = jpe;
		}

		if(e != null) {
			logger.error("Error with audioSearch", e.getMessage());
		}

		return audioData;
	}
}
