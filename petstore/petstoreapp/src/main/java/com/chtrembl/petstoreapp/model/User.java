package com.chtrembl.petstoreapp.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.microsoft.applicationinsights.TelemetryClient;

/**
 * Session based for each user, each user will also have a unique Telemetry
 * Client instance.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@SuppressWarnings("serial")
public class User implements Serializable {
	private String name = "Guest";
	private String sessionId = null;
	private String email = null;

	// intentionally avoiding spring cache to ensure service calls are made each
	// time to show Telemetry with APIM requests
	private List<Pet> pets;

	// intentionally avoiding spring cache to ensure service calls are made each
	// time to show Telemetry with APIM requests
	private List<Product> products;

	@Autowired(required = false)
	private transient TelemetryClient telemetryClient;

	@Autowired
	private ContainerEnvironment containerEnvironment;

	private int cartCount = 0;

	private boolean initialTelemetryRecorded = false;

	@PostConstruct
	private void initialize() {
		if (this.telemetryClient == null) {
			this.telemetryClient = new com.chtrembl.petstoreapp.service.TelemetryClient();
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TelemetryClient getTelemetryClient() {
		return this.telemetryClient;
	}

	public List<Pet> getPets() {
		return pets;
	}

	public synchronized void setPets(List<Pet> pets) {
		this.pets = pets;
	}

	public List<Product> getProducts() {
		return products;
	}

	public synchronized void setProducts(List<Product> products) {
		this.products = products;
	}

	public int getCartCount() {
		return cartCount;
	}

	public void setCartCount(int cartCount) {
		this.cartCount = cartCount;
	}

	public boolean isInitialTelemetryRecorded() {
		return initialTelemetryRecorded;
	}

	public void setInitialTelemetryRecorded(boolean initialTelemetryRecorded) {
		this.initialTelemetryRecorded = initialTelemetryRecorded;
	}

	public Map<String, String> getCustomEventProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("session_Id", this.sessionId);
		properties.put("containerHostName", this.containerEnvironment.getAppDate());
		properties.put("containerHostName", this.containerEnvironment.getAppVersion());
		properties.put("containerHostName", this.containerEnvironment.getContainerHostName());
		return properties;
	}
}
