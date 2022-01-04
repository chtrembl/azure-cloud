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
		} catch (Exception e) {
			log.error(String.format(
					"PetStoreOrderService error retrieving products from petstoreproductservice/v2/product/findByStatus?status=available %s",
					e.getMessage()));
			// product lookup cannot be done from this container...
			return products;
		}
		try {
			products = objectMapper.readValue(response.getBody(), new TypeReference<List<Product>>() {
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
