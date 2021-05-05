package com.chtrembl.petstoreapp.model;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@SuppressWarnings("serial")
public class PetStoreRequest implements Serializable {
	private static Logger logger = LoggerFactory.getLogger(PetStoreRequest.class);

	private String referer;

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}
}
