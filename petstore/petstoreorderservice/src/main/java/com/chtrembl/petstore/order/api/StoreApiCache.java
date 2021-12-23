package com.chtrembl.petstore.order.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.chtrembl.petstore.order.model.Order;

@Component
@EnableScheduling
public class StoreApiCache {
	static final Logger log = LoggerFactory.getLogger(StoreApiCache.class);

	@Autowired
	@Qualifier(value = "cacheManager")
	private CacheManager cacheManager;

	@Cacheable("orders")
	public Order getOrder(String id) {
		log.info(String.format("Creating new order id:%s and caching it", id));
		return new Order();
	}

	// wipe this every 12 hours... 60 secs * 60 mins * 12 hrs * 1000 (1 sec in ms)
	@Scheduled(fixedRate=60*60*12*1000)
	public void evictAllcachesAtIntervals() {
		log.info("evictAllcachesAtIntervals...");

		// should probably wipe when an order is complete or dangling, but for
		// simplicity in this pet store guide, just wipe everything on a set interval...
		this.cacheManager.getCacheNames().stream().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
	}
}
