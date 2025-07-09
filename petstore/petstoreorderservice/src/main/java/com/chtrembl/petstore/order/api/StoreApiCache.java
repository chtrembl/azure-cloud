package com.chtrembl.petstore.order.api;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.chtrembl.petstore.order.model.Order;
import com.chtrembl.petstore.order.model.Product;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@EnableScheduling
public class StoreApiCache {
	static final Logger log = LoggerFactory.getLogger(StoreApiCache.class);

	private final ObjectMapper objectMapper;

	@Value("${petstore.service.product.url:}")
	private String petStoreProductServiceURL;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	@Qualifier(value = "cacheManager")
	private CacheManager cacheManager;

	@org.springframework.beans.factory.annotation.Autowired
	public StoreApiCache(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Cacheable("orders")
	public Order getOrder(String id) {
		log.info(String.format("PetStoreOrderService creating new order id:%s and caching it", id));
		return new Order();
	}

	@Cacheable("orders")
	public List<Product> getProducts() {
		log.info(String.format(
				"PetStoreOrderService retrieving products from %spetstoreproductservice/v2/product/findByStatus?status=available",
				this.petStoreProductServiceURL));
		List<Product> products = null;
		ResponseEntity<String> response = null;
		String responseBody = null;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
			headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
			headers.add("session-id", "PetStoreOrderService");
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			restTemplate.setRequestFactory(new CustomHttpComponentsClientHttpRequestFactory());
			response = restTemplate
					.exchange(String.format("%spetstoreproductservice/v2/product/findByStatus?status=available",
							this.petStoreProductServiceURL), HttpMethod.GET, entity, String.class);
			responseBody = response.getBody();
			throw new IOException("Simulated exception to test error handling"); // Simulate an error for testing
		} catch (Exception e) {
			log.error(String.format(
					"PetStoreOrderService error retrieving products from petstoreproductservice/v2/product/findByStatus?status=available %s",
					e.getMessage()));
			// hack if the network dns is not working...
			responseBody = "[{\"id\":1,\"category\":{\"id\":1,\"name\":\"Dog Toy\"},\"name\":\"Ball\",\"photoURL\":\"https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-toys/ball.jpg?raw=true\",\"tags\":[{\"id\":1,\"name\":\"small\"},{\"id\":2,\"name\":\"large\"}],\"status\":\"available\"},{\"id\":2,\"category\":{\"id\":1,\"name\":\"Dog Toy\"},\"name\":\"Ball Launcher\",\"photoURL\":\"https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-toys/ball-launcher.jpg?raw=true\",\"tags\":[{\"id\":1,\"name\":\"large\"}],\"status\":\"available\"},{\"id\":3,\"category\":{\"id\":1,\"name\":\"Dog Toy\"},\"name\":\"Plush Lamb\",\"photoURL\":\"https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-toys/plush-lamb.jpg?raw=true\",\"tags\":[{\"id\":1,\"name\":\"small\"},{\"id\":2,\"name\":\"large\"}],\"status\":\"available\"},{\"id\":4,\"category\":{\"id\":1,\"name\":\"Dog Toy\"},\"name\":\"Plush Moose\",\"photoURL\":\"https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-toys/plush-moose.jpg?raw=true\",\"tags\":[{\"id\":1,\"name\":\"small\"},{\"id\":2,\"name\":\"large\"}],\"status\":\"available\"},{\"id\":5,\"category\":{\"id\":1,\"name\":\"Dog Food\"},\"name\":\"Large Breed Dry Food\",\"photoURL\":\"https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-food/large-dog.jpg?raw=true\",\"tags\":[{\"id\":1,\"name\":\"large\"}],\"status\":\"available\"},{\"id\":6,\"category\":{\"id\":1,\"name\":\"Dog Food\"},\"name\":\"Small Breed Dry Food\",\"photoURL\":\"https://raw.githubusercontent.com/chtrembl/staticcontent/master/dog-food/small-dog.jpg?raw=true\",\"tags\":[{\"id\":1,\"name\":\"small\"}],\"status\":\"available\"},{\"id\":7,\"category\":{\"id\":1,\"name\":\"Cat Toy\"},\"name\":\"Mouse\",\"photoURL\":\"https://raw.githubusercontent.com/chtrembl/staticcontent/master/cat-toys/mouse.jpg?raw=true\",\"tags\":[{\"id\":1,\"name\":\"small\"},{\"id\":2,\"name\":\"large\"}],\"status\":\"available\"},{\"id\":8,\"category\":{\"id\":1,\"name\":\"Cat Toy\"},\"name\":\"Scratcher\",\"photoURL\":\"https://raw.githubusercontent.com/chtrembl/staticcontent/master/cat-toys/scratcher.jpg?raw=true\",\"tags\":[{\"id\":1,\"name\":\"small\"},{\"id\":2,\"name\":\"large\"}],\"status\":\"available\"},{\"id\":9,\"category\":{\"id\":1,\"name\":\"Cat Food\"},\"name\":\"All Sizes Cat Dry Food\",\"photoURL\":\"https://raw.githubusercontent.com/chtrembl/staticcontent/master/cat-food/cat.jpg?raw=true\",\"tags\":[{\"id\":1,\"name\":\"small\"},{\"id\":2,\"name\":\"large\"}],\"status\":\"available\"},{\"id\":10,\"category\":{\"id\":1,\"name\":\"Fish Toy\"},\"name\":\"Mangrove Ornament\",\"photoURL\":\"https://raw.githubusercontent.com/chtrembl/staticcontent/master/fish-toys/mangrove.jpg?raw=true\",\"tags\":[{\"id\":1,\"name\":\"small\"},{\"id\":2,\"name\":\"large\"}],\"status\":\"available\"},{\"id\":11,\"category\":{\"id\":1,\"name\":\"Fish Food\"},\"name\":\"All Sizes Fish Food\",\"photoURL\":\"https://raw.githubusercontent.com/chtrembl/staticcontent/master/fish-food/fish.jpg?raw=true\",\"tags\":[{\"id\":1,\"name\":\"small\"},{\"id\":2,\"name\":\"large\"}],\"status\":\"available\"}]";
		}
		try {
			products = objectMapper.readValue(responseBody, new TypeReference<List<Product>>() {
			});
		} catch (JsonParseException e1) {
			log.error(String.format(
					"PetStoreOrderService error retrieving products from %spetstoreproductservice/v2/product/findByStatus?status=available ",
					e1.getMessage()));
		} catch (JsonMappingException e1) {
			log.error(String.format(
					"PetStoreOrderService error retrieving products from %spetstoreproductservice/v2/product/findByStatus?status=available ",
					e1.getMessage()));
		} catch (IOException e1) {
			log.error(String.format(
					"PetStoreOrderService error retrieving products from %spetstoreproductservice/v2/product/findByStatus?status=available ",
					e1.getMessage()));
		}
		return products;
	}

	// wipe this every 12 hours... 60 secs * 60 mins * 12 hrs * 1000 (1 sec in ms)
	@Scheduled(fixedRate = 60 * 60 * 12 * 1000)
	public void evictAllcachesAtIntervals() {
		log.info("PetStoreOrderService evictAllcachesAtIntervals...");

		// should probably wipe when an order is complete or dangling, but for
		// simplicity in this pet store guide, just wipe everything on a set interval...
		this.cacheManager.getCacheNames().stream().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
	}

	private static final class CustomHttpComponentsClientHttpRequestFactory
			extends HttpComponentsClientHttpRequestFactory {

		@Override
		protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {

			if (HttpMethod.GET.equals(httpMethod)) {
				return new HttpEntityEnclosingGetRequestBase(uri);
			}
			return super.createHttpUriRequest(httpMethod, uri);
		}
	}

	private static final class HttpEntityEnclosingGetRequestBase extends HttpEntityEnclosingRequestBase {

		public HttpEntityEnclosingGetRequestBase(final URI uri) {
			super.setURI(uri);
		}

		@Override
		public String getMethod() {
			return HttpMethod.GET.name();
		}
	}
}
