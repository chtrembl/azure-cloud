package com.chtrembl.petstoreapp.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.microsoft.applicationinsights.core.dependencies.google.common.io.CharStreams;

import ch.qos.logback.core.joran.spi.JoranException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import reactor.core.publisher.Mono;

/**
 * Singleton to store container state
 */
@SuppressWarnings("serial")
@Component
@EnableScheduling
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

	@Value("${bing.search.url:https://api.bing.microsoft.com/}")
	private String bingSearchURL;

	@Value("${bing.search.subscription.key:}")
	private String bingSearchSubscriptionKey;

	@Value("#{T(java.util.Arrays).asList('${petstore.logging.additional-headers-to-log:}')}") 
	private List<String> additionalHeadersToLog;

	@Value("#{T(java.util.Arrays).asList('${petstore.logging.additional-headers-to-send:}')}") 
	private List<String> additionalHeadersToSend;

	@Value("${petstore.signalr.negotiation-url:}")
	private String signalRNegotiationURL;

	@Value("${petstore.signalr.service-url:}")
	private String signalRServiceURL;
	
	@Value("${petstore.signalr.key:}")
	private String signalRKey;

	private WebClient signalRWebClient = null;

	public static String CURRENT_USERS_HUB = "currentUsers";

	@Autowired
	private CacheManager currentUsersCacheManager;

	@PostConstruct
	private void initialize() throws JoranException {

		if (StringUtils.isNoneEmpty(this.getSignalRKey()) && StringUtils.isNoneEmpty(this.getSignalRNegotiationURL())
				&& StringUtils.isNoneEmpty(this.getSignalRServiceURL())) {
			this.signalRWebClient = WebClient.builder().baseUrl(this.getSignalRServiceURL()).build();
		}
		
		try {
			this.setContainerHostName(
					InetAddress.getLocalHost().getHostAddress() + "/" + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			this.setContainerHostName("unknown");
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			InputStream resource = new ClassPathResource("static/content/version.json").getInputStream();
			
		    byte[] bdata = FileCopyUtils.copyToByteArray(resource);
		    String text = new String(bdata, StandardCharsets.UTF_8);
	
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

	public String generateJwt(String audience, String userId) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		long expMillis = nowMillis + (30 * 30 * 1000);
		Date exp = new Date(expMillis);

		byte[] apiKeySecretBytes = this.getSignalRKey().getBytes(StandardCharsets.UTF_8);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		JwtBuilder builder = Jwts.builder().setAudience(audience).setIssuedAt(now).setExpiration(exp)
				.signWith(signingKey);

		if (userId != null) {
			builder.claim("nameid", userId);
		}

		return builder.compact();
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

	public String getBingSearchURL() {
		return bingSearchURL;
	}

	public String getBingSearchSubscriptionKey() {
		return bingSearchSubscriptionKey;
	}

	public List<String> getAdditionalHeadersToLog() {
		return additionalHeadersToLog;
	}

	public List<String> getAdditionalHeadersToSend() {
		return additionalHeadersToSend;
	}

	public String getSignalRNegotiationURL() {
		return signalRNegotiationURL;
	}

	public String getSignalRServiceURL() {
		return signalRServiceURL;
	}

	public String getSignalRKey() {
		return signalRKey;
	}

	@Scheduled(fixedRateString = "${petstore.signalr.update.fixedRate:60000}")
	public void sendCurrentUsers() {
		if (this.signalRWebClient == null) {
			return;
		}
		String hubUri = "/api/v1/hubs/" + ContainerEnvironment.CURRENT_USERS_HUB;
		String hubUrl = getSignalRServiceURL() + hubUri;
		String accessKey = generateJwt(hubUrl, null);

		CaffeineCache caffeineCache = (CaffeineCache) this.currentUsersCacheManager
				.getCache(ContainerEnvironment.CURRENT_USERS_HUB);
		com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
		int size = nativeCache.asMap().keySet().size();

		logger.info("@Scheduled sending current users of size " + size);

		this.signalRWebClient.post().uri(hubUri)
				.body(BodyInserters.fromPublisher(
						Mono.just(new SignalRMessage("currentUsersUpdated", new Object[] { size })),
						SignalRMessage.class))
				.accept(MediaType.APPLICATION_JSON).header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.header("Cache-Control", "no-cache").header("Authorization", "Bearer " + accessKey).retrieve()
				.bodyToMono(Object.class).block();
	}
}
