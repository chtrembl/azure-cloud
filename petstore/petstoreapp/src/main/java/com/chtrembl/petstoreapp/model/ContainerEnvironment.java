package com.chtrembl.petstoreapp.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.applicationinsights.core.dependencies.google.common.io.CharStreams;

import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Singleton to store container state
 */
@SuppressWarnings("serial")
@Component
public class ContainerEnvironment implements Serializable {
	private static Logger logger = LoggerFactory.getLogger(ContainerEnvironment.class);
	private String containerHostName = null;
	private String appVersion = null;
	private String appDate = null;
	private String year = null;

	private boolean securityEnabled = false;

	@Value("${petstore.service.pet.url:}")
	private String petStorePetServiceURL;

	@Value("${petstore.service.product.url:}")
	private String petStoreProductServiceURL;

	@Value("${petstore.service.order.url:}")
	private String petStoreOrderServiceURL;

	@Value("${petstore.service.subscription.key:}")
	private String petStoreServicesSubscriptionKey;

	@Value("${petstore.apim.host:}")
	private String petstoreAPIMHost;

	@Value("${ga.tracking.id:}")
	private String gaTrackingId;

	@PostConstruct
	private void initialize() throws JoranException {
		// LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		try {
			this.setContainerHostName(
					InetAddress.getLocalHost().getHostAddress() + "/" + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			this.setContainerHostName("unknown");
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			InputStream resourcee = new ClassPathResource("static/content/version.json").getInputStream();
			String text = null;
			try (final Reader reader = new InputStreamReader(resourcee)) {
				text = CharStreams.toString(reader);
			}

			Version version = objectMapper.readValue(text, Version.class);
			this.setAppVersion(version.getVersion());
			this.setAppDate(version.getDate());
		} catch (IOException e) {
			logger.info("error parsing file " + e.getMessage());
			this.setAppVersion("unknown");
			this.setAppDate("unknown");
		}

		this.setYear(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

		// fix this at some point so it doesnt need to be done in the filter each
		// time...
		// context.putProperty("appVersion", this.getAppVersion());
		// context.putProperty("appDate", this.getAppDate());
		// context.putProperty("containerHostName", this.getContainerHostName());
	}

	public String getContainerHostName() {
		return containerHostName;
	}

	public void setContainerHostName(String containerHostName) {
		this.containerHostName = containerHostName;
	}

	public String getAppVersion() {
		if ("version".equals(this.appVersion) || this.appVersion == null) {
			return String.valueOf(System.currentTimeMillis());
		}
		return this.appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getPetStoreServicesSubscriptionKey() {
		return petStoreServicesSubscriptionKey;
	}

	public void setPetStoreServicesSubscriptionKey(String petStoreServicesSubscriptionKey) {
		this.petStoreServicesSubscriptionKey = petStoreServicesSubscriptionKey;
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public boolean isSecurityEnabled() {
		return securityEnabled;
	}

	public void setSecurityEnabled(boolean securityEnabled) {
		this.securityEnabled = securityEnabled;
	}

	public String getPetStorePetServiceURL() {
		return petStorePetServiceURL;
	}

	public String getPetStoreProductServiceURL() {
		return petStoreProductServiceURL;
	}

	public String getPetStoreOrderServiceURL() {
		return petStoreOrderServiceURL;
	}

	public String getPetstoreAPIMHost() {
		return petstoreAPIMHost;
	}

	public String getGaTrackingId() {
		return gaTrackingId;
	}
}
