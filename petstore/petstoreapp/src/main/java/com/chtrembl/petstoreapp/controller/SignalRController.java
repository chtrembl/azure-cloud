package com.chtrembl.petstoreapp.controller;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.chtrembl.petstoreapp.model.ContainerEnvironment;
import com.chtrembl.petstoreapp.model.SignalRConnectionInfo;
import com.chtrembl.petstoreapp.model.SignalRMessage;
import com.chtrembl.petstoreapp.model.User;

import reactor.core.publisher.Mono;

/**
 * SignalRController
 */
@RestController
public class SignalRController {
	private static Logger logger = LoggerFactory.getLogger(WebAppController.class);

	private WebClient signalRWebClient = null;

	@Autowired
	private ContainerEnvironment containerEnvironment;

	@Autowired
	private CacheManager currentUsersCacheManager;

	@Autowired
	private User sessionUser;

	@PostConstruct
	public void initialize() {
		this.signalRWebClient = WebClient.builder().baseUrl(this.containerEnvironment.getSignalRServiceURL()).build();
	}

	@PostMapping(value = "/signalr/negotiate", produces = MediaType.APPLICATION_JSON_VALUE)
	public SignalRConnectionInfo negotiate() {
		if (this.signalRWebClient == null) {
			return null;
		}
		String hubUrl = this.containerEnvironment.getSignalRServiceURL() + "/client/?hub="
				+ ContainerEnvironment.CURRENT_USERS_HUB;
		String userId = this.sessionUser.getSessionId(); // optional
		String accessKey = this.containerEnvironment.generateJwt(hubUrl, userId);
		return new SignalRConnectionInfo(hubUrl, accessKey);
	}

	@PostMapping("/signalr/test")
	public void sendCurrentUsers(@RequestParam(required = false) String userId,
			@RequestParam(required = false) String mockSize) {
		if (this.signalRWebClient == null) {
			return;
		}
		String uri = "/api/v1/hubs/" + ContainerEnvironment.CURRENT_USERS_HUB;

		if (StringUtils.isNoneEmpty(userId)) {
			uri += "/users/" + userId;
		}

		String hubUrl = this.containerEnvironment.getSignalRServiceURL() + uri;

		String accessKey = this.containerEnvironment.generateJwt(hubUrl, null);

		CaffeineCache caffeineCache = (CaffeineCache) this.currentUsersCacheManager.getCache(ContainerEnvironment.CURRENT_USERS_HUB);
		com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
		int size = nativeCache.asMap().keySet().size();

		if (StringUtils.isNoneEmpty(mockSize) && Integer.valueOf(mockSize) >= 0) {
			size = Integer.valueOf(mockSize);
		}

		logger.info("test sending current users of size " + size);

		this.signalRWebClient.post().uri(uri)
				.body(BodyInserters.fromPublisher(
						Mono.just(new SignalRMessage("currentUsersUpdated", new Object[] { size })),
						SignalRMessage.class))
		.accept(MediaType.APPLICATION_JSON)
		.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
		.header("Cache-Control", "no-cache")
		.header("Authorization", "Bearer " + accessKey).retrieve()
				.bodyToMono(Object.class).block();
	}
}