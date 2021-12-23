package com.chtrembl.petstoreapp.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;

import com.chtrembl.petstoreapp.model.ContainerEnvironment;
import com.chtrembl.petstoreapp.model.Pet;
import com.chtrembl.petstoreapp.model.User;
import com.chtrembl.petstoreapp.service.PetStoreService;
import com.microsoft.applicationinsights.telemetry.PageViewTelemetry;

/**
 * Web Controller for all of the model/presentation construction and various
 * endpoints
 */
@Controller
public class WebAppController {
	private static Logger logger = LoggerFactory.getLogger(WebAppController.class);

	@Autowired
	private ContainerEnvironment containerEnvironment;

	@Autowired
	private PetStoreService petStoreService;

	@Autowired
	private User sessionUser;

	@ModelAttribute
	public void setModel(HttpServletRequest request, Model model, OAuth2AuthenticationToken token) {

		// this is used for n tier correlated Telemetry. Keep the same one for anonymous
		// sessions that get authenticated
		if (this.sessionUser.getSessionId() == null) {
			this.sessionUser.setSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
		}

		if (token != null) {
			final OAuth2User user = token.getPrincipal();

			// this should really be done in the authentication/pre auth flow....
			this.sessionUser.setName((String) user.getAttributes().get("name"));

			this.sessionUser.getTelemetryClient().trackEvent(
					String.format("PetStoreApp %s logged in, container host: %s", this.sessionUser.getName(),
							this.containerEnvironment.getContainerHostName()),
					this.sessionUser.getCustomEventProperties(), null);

			model.addAttribute("user", this.sessionUser.getName());
			model.addAttribute("grant_type", user.getAuthorities());
			model.addAllAttributes(user.getAttributes());
		}

		model.addAttribute("userName", this.sessionUser.getName());
		model.addAttribute("containerEnvironment", this.containerEnvironment);

		model.addAttribute("sessionId", this.sessionUser.getSessionId());

		MDC.put("session_Id", this.sessionUser.getSessionId());
	}

	@GetMapping(value = "/login")
	public String login(Model model, HttpServletRequest request) throws URISyntaxException {
		logger.info("PetStoreApp /login requested, routing to login view...");

		PageViewTelemetry pageViewTelemetry = new PageViewTelemetry();
		pageViewTelemetry.setUrl(new URI(request.getRequestURL().toString()));
		pageViewTelemetry.setName("login");
		this.sessionUser.getTelemetryClient().trackPageView(pageViewTelemetry);
		return "login";
	}

	// multiple endpoints to generate some Telemetry and allowing for
	// differentiation
	@GetMapping(value = { "/dogbreeds", "/catbreeds", "/fishbreeds" })
	public String breeds(Model model, OAuth2AuthenticationToken token, HttpServletRequest request,
			@RequestParam(name = "category") String category) throws URISyntaxException {

		// quick validation, should really be done in validators, check for cross side
		// scripting etc....
		if (!"Dog".equals(category) && !"Cat".equals(category) && !"Fish".equals(category)) {
			return "home";
		}
		logger.info(String.format("PetStoreApp /breeds requested for %s, routing to breeds view...", category));

		model.addAttribute("pets", this.petStoreService.getPets(category));
		return "breeds";
	}

	@GetMapping(value = "/breeddetails")
	public String breedeetails(Model model, OAuth2AuthenticationToken token, HttpServletRequest request,
			@RequestParam(name = "category") String category, @RequestParam(name = "id") int id)
			throws URISyntaxException {

		// quick validation, should really be done in validators, check for cross side
		// scripting etc....
		if (!"Dog".equals(category) && !"Cat".equals(category) && !"Fish".equals(category)) {
			return "home";
		}

		if (null == this.sessionUser.getPets()) {
			this.petStoreService.getPets(category);
		}

		Pet pet = null;

		try {
			pet = this.sessionUser.getPets().get(id - 1);
		} catch (Exception npe) {
			this.sessionUser.getTelemetryClient().trackException(npe);
			pet = new Pet();
		}

		logger.info(String.format("PetStoreApp /breeddetails requested for %s, routing to dogbreeddetails view...",
				pet.getName()));

		model.addAttribute("pet", pet);

		return "breeddetails";
	}

	@GetMapping(value = "/products")
	public String products(Model model, OAuth2AuthenticationToken token, HttpServletRequest request,
			@RequestParam(name = "category") String category, @RequestParam(name = "id") int id)
			throws URISyntaxException {

		// quick validation, should really be done in validators, check for cross side
		// scripting etc....
		if (!"Toy".equals(category) && !"Food".equals(category)) {
			return "home";
		}
		logger.info(String.format("PetStoreApp /products requested for %s, routing to products view...", category));

		Pet pet = this.sessionUser.getPets().get(id - 1);

		model.addAttribute("products",
				this.petStoreService.getProducts(pet.getCategory().getName() + " " + category, pet.getTags()));
		return "products";
	}

	@GetMapping(value = "/claims")
	public String claims(Model model, OAuth2AuthenticationToken token, HttpServletRequest request)
			throws URISyntaxException {
		logger.info(String.format("PetStoreApp /claims requested for %s, routing to claims view...",
				this.sessionUser.getName()));
		return "claims";
	}

	@GetMapping(value = "/slowness")
	public String generateappinsightsslowness(Model model, OAuth2AuthenticationToken token, HttpServletRequest request)
			throws URISyntaxException, InterruptedException {
		logger.info("PetStoreApp simulating slowness, routing to home view...");

		PageViewTelemetry pageViewTelemetry = new PageViewTelemetry();
		pageViewTelemetry.setUrl(new URI(request.getRequestURL().toString()));
		pageViewTelemetry.setName("slow operation");
		this.sessionUser.getTelemetryClient().trackPageView(pageViewTelemetry);

		Thread.sleep(30000);

		return "home";
	}

	@GetMapping(value = "/exception")
	public String exception(Model model, OAuth2AuthenticationToken token, HttpServletRequest request)
			throws URISyntaxException, InterruptedException {

		NullPointerException npe = new NullPointerException();

		logger.info("PetStoreApp simulating NullPointerException, routing to home view..." + npe.getStackTrace());

		this.sessionUser.getTelemetryClient().trackException(npe);

		return "home";
	}

	@GetMapping(value = "/*")
	public String home(Model model, OAuth2AuthenticationToken token, HttpServletRequest request)
			throws URISyntaxException {
		logger.info(String.format("PetStoreApp %s requested and %s is being routed to home view session %s",
				request.getRequestURI(), this.sessionUser.getName(), this.sessionUser.getSessionId()));
		PageViewTelemetry pageViewTelemetry = new PageViewTelemetry();
		pageViewTelemetry.setUrl(new URI(request.getRequestURL().toString()));
		pageViewTelemetry.setName("default");
		this.sessionUser.getTelemetryClient().trackPageView(pageViewTelemetry);
		return "home";
	}
}
