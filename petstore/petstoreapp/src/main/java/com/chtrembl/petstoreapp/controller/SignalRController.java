package com.chtrembl.petstoreapp.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	private String hubName = "currentUsers";

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
		String hubUrl = this.containerEnvironment.getSignalRServiceURL() + "/client/?hub=currentUsers";
		String userId = this.sessionUser.getSessionId(); // optional
		String accessKey = this.containerEnvironment.generateJwt(hubUrl, userId);
		return new SignalRConnectionInfo(hubUrl, accessKey);
	}

	@GetMapping("/signalr/send")
	public void sendMessage() {
		String hubUrl = this.containerEnvironment.getSignalRServiceURL() + "/api/v1/hubs/" + hubName;
		String accessKey = this.containerEnvironment.generateJwt(hubUrl, null);

		CaffeineCache caffeineCache = (CaffeineCache) this.currentUsersCacheManager.getCache("currentUsers");
		com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
		int size = nativeCache.asMap().keySet().size();

		this.signalRWebClient.post().uri("/api/v1/hubs/" + hubName)
				.body(BodyInserters.fromPublisher(Mono.just(new SignalRMessage("newMessage", new Object[] { size })),
						SignalRMessage.class))
		.accept(MediaType.APPLICATION_JSON)
		.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
		.header("Cache-Control", "no-cache")
		.header("Authorization", "Bearer " + accessKey).retrieve()
				.bodyToMono(Object.class).block();
	}
}