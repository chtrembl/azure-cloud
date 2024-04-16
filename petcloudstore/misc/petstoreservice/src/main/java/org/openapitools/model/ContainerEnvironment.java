package org.openapitools.model;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.microsoft.applicationinsights.TelemetryClient;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@SuppressWarnings("serial")
public class ContainerEnvironment implements Serializable {
	private String containerHostName = null;

	@Autowired
	private transient TelemetryClient telemetryClient;

	public String getContainerHostName() {
		return containerHostName;
	}

	public void setContainerHostName(String containerHostName) {
		this.containerHostName = containerHostName;
	}

	public TelemetryClient getTelemetryClient() {
		return this.telemetryClient;
	}
}
