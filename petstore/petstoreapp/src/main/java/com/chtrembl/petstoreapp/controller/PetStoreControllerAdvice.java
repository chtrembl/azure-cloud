package com.chtrembl.petstoreapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;

import com.chtrembl.petstoreapp.model.ContainerEnvironment;
import com.chtrembl.petstoreapp.model.User;
import com.nimbusds.jose.shaded.json.JSONArray;

@ControllerAdvice(assignableTypes = {WebAppController.class, RestAPIController.class})
public class PetStoreControllerAdvice {
	private static Logger logger = LoggerFactory.getLogger(PetStoreControllerAdvice.class);
    
    @Autowired
	private ContainerEnvironment containerEnvironment;

	@Autowired
	private User sessionUser;

	@Autowired
	private CacheManager currentUsersCacheManager;

   	@ModelAttribute
	public void setModel(HttpServletRequest request, Model model, OAuth2AuthenticationToken token) {
		CaffeineCache caffeineCache = (CaffeineCache) this.currentUsersCacheManager
				.getCache(ContainerEnvironment.CURRENT_USERS_HUB);
		com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();

		// this is used for n tier correlated Telemetry. Keep the same one for anonymous
		// sessions that get authenticated so they can persist for seamless flow of logs (public user to private) use the jSessionId for true JSESSION use
		if (this.sessionUser.getSessionId() == null) {
			String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
			this.sessionUser.setSessionId(sessionId);
			// put session in TTL cache so its there after initial login
			caffeineCache.put(this.sessionUser.getSessionId(), this.sessionUser.getName());
			this.containerEnvironment.sendCurrentUsers();
		}

		// put session in TTL cache to refresh TTL
		caffeineCache.put(this.sessionUser.getSessionId(), this.sessionUser.getName());

		if (token != null) {
			final OAuth2User user = token.getPrincipal();

			try {
				this.sessionUser.setEmail((String) ((JSONArray) user.getAttribute("emails")).get(0));
			} catch (Exception e) {
				logger.warn(String.format("PetStoreApp  %s logged in, however cannot get email associated: %s",
						this.sessionUser.getName(), e.getMessage()));
			}

			// this should really be done in the authentication/pre auth flow....
			this.sessionUser.setName((String) user.getAttributes().get("name"));

			if (!this.sessionUser.isInitialTelemetryRecorded()) {
				this.sessionUser.getTelemetryClient().trackEvent(
						String.format("PetStoreApp %s logged in, container host: %s", this.sessionUser.getName(),
								this.containerEnvironment.getContainerHostName()),
						this.sessionUser.getCustomEventProperties(), null);

				this.sessionUser.setInitialTelemetryRecorded(true);
			}
			model.addAttribute("claims", user.getAttributes());
			model.addAttribute("user", this.sessionUser.getName());
			model.addAttribute("grant_type", user.getAuthorities());
		}

		model.addAttribute("userName", this.sessionUser.getName());
		model.addAttribute("containerEnvironment", this.containerEnvironment);

		model.addAttribute("sessionId", this.sessionUser.getSessionId());

		model.addAttribute("appVersion", this.containerEnvironment.getAppVersion());

		model.addAttribute("cartSize", this.sessionUser.getCartCount());

		model.addAttribute("currentUsersOnSite", nativeCache.asMap().keySet().size());
		model.addAttribute("signalRNegotiationURL", this.containerEnvironment.getSignalRNegotiationURL());

		MDC.put("session_Id", this.sessionUser.getSessionId());

		model.addAttribute("sid", this.sessionUser.getSessionId());
		
	    HttpSession session = request.getSession(false);
		if (session != null) {
			String jsessionId = session.getId();
			this.sessionUser.setJSessionId(jsessionId);
		}

		String message = "";
		if(!this.sessionUser.getSessionId().equals(this.sessionUser.getJSessionId()))
		{
			message = " these are different because the public user ended up logging in and we maintain the original for n-tiered correlated telemtry";
		}
		logger.info("session id: " + this.sessionUser.getSessionId() + " jsession id: " + this.sessionUser.getJSessionId()+ message);
	}

}