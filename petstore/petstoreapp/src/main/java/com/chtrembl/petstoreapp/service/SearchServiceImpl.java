package com.chtrembl.petstoreapp.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import com.chtrembl.petstoreapp.model.ContainerEnvironment;
import com.chtrembl.petstoreapp.model.SearchResponse;
import com.chtrembl.petstoreapp.model.User;
import com.chtrembl.petstoreapp.model.Value;
import com.chtrembl.petstoreapp.model.WebPages;
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
	private User sessionUser;

	@Autowired
	private ContainerEnvironment containerEnvironment;

	private WebClient bingSearchWebClient = null;

	@PostConstruct
	public void initialize() {
		this.bingSearchWebClient = WebClient.builder().baseUrl(this.containerEnvironment.getBingSearchURL())
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
}
