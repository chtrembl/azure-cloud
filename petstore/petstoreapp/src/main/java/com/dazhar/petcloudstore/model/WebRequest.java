package com.dazhar.petcloudstore.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@SuppressWarnings("serial")
public class WebRequest implements Serializable {
	private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

	@Autowired
	private ContainerEnvironment containerEnvironment;

	public MultiValueMap<String, String> getHeaders() {
		if(!StringUtils.isEmpty(this.containerEnvironment.getPetstoreAPIMHost()) && this.headers.get("host") == null)
		{
			this.headers.add("host", this.containerEnvironment.getPetstoreAPIMHost());
		}
		return this.headers;
	}

	public void addHeader(String headerKey, String headerValue) {
		this.headers.add(headerKey, headerValue);
	}
}
