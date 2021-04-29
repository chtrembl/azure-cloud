package com.chtrembl.petstoreapp.model;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.microsoft.applicationinsights.TelemetryClient;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@SuppressWarnings("serial")
public class User implements Serializable {
	private String name = "Guest";
	private String sessionId = null;

	// intentionally avoiding spring cache to ensure service calls are made each
	// time to show telemetry with APIM requests
	private List<Pet> pets;

	@Autowired(required = false)
	private transient TelemetryClient telemetryClient;

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

	public TelemetryClient getTelemetryClient() {
		return this.telemetryClient;
	}

	public List<Pet> getPets() {
		return pets;
	}

	public synchronized void setPets(List<Pet> pets) {
		this.pets = pets;
	}
}
