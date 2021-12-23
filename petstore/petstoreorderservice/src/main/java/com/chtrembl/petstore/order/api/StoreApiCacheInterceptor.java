package com.chtrembl.petstore.order.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheInterceptor;

@SuppressWarnings("serial")
public class StoreApiCacheInterceptor extends CacheInterceptor {
	@Autowired
	@Qualifier(value = "cacheManager")
	private CacheManager cacheManager;

	@Override
	protected void doPut(Cache cache, Object key, Object result) {
		// in case we want to do anything specific here...
		super.doPut(cache, key, result);
	}
}
