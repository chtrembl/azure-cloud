package com.chtrembl.petstoreapp.controller;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chtrembl.petstoreapp.model.Order;
import com.chtrembl.petstoreapp.model.User;
import com.chtrembl.petstoreapp.service.PetStoreService;

/**
 * REST controller to facilitate REST calls such as session keep alives
 * (progressive web apps)
 *
 */
@RestController
public class RestAPIController {
	private static Logger logger = LoggerFactory.getLogger(RestAPIController.class);

	@Autowired
	private User sessionUser;

	@Autowired
	private PetStoreService petStoreService;

	@GetMapping("/api/contactus")
	public String contactus() {

		this.sessionUser.getTelemetryClient().trackEvent(
				String.format("PetStoreApp user %s requesting contact us", this.sessionUser.getName()),
				this.sessionUser.getCustomEventProperties(), null);

		return "Please contact Azure PetStore at 401-555-5555. Thank you. Demo 6/13";
	}

	@GetMapping("/api/sessionid")
	public String sessionid() {

		return this.sessionUser.getSessionId();
	}

	// helper api call for soul machines dp demo... POST URL Encoding intermittent missing headers with POST/FORM Encoding hence the GET hack with UUID
	@GetMapping(value = "/api/updatecart", produces = MediaType.TEXT_HTML_VALUE)
	public String updatecart(Model model, @RequestParam Map<String, String> params, HttpServletRequest request) {
		logger.info("session: " + this.sessionUser.getSessionId());
		logger.info("jsession: " + this.sessionUser.getJSessionId());
		logger.info("csrf: " + this.sessionUser.getCsrfToken());
		
		if(params.get("csrf") == null || !params.get("csrf").equals(this.sessionUser.getCsrfToken()))
			{
			return "Invalid CSRF token";
		}

		this.sessionUser.getTelemetryClient().trackEvent(
				String.format("PetStoreApp user %s requesting update cart", this.sessionUser.getName()),
				this.sessionUser.getCustomEventProperties(), null);
	
		int cartCount = 1;

		String operator = params.get("operator");
		if (StringUtils.isNotEmpty(operator)) {
			if ("minus".equals(operator)) {
				cartCount = -1;
			}
		}

		this.petStoreService.updateOrder(Long.valueOf(params.get("productId")), cartCount, false);
		
		Order order = this.petStoreService.retrieveOrder(this.sessionUser.getSessionId());
		model.addAttribute("order", order);
		int cartSize = 0;
		if (order != null && order.getProducts() != null && !order.isComplete()) {
			cartSize = order.getProducts().size();
		}
		this.sessionUser.setCartCount(cartSize);

		return "success";
	}

	// helper api call for soul machines dp demo... POST URL Encoding intermittent missing headers with POST/FORM Encoding hence the GET hack with UUID
	@GetMapping(value = "/api/viewcart", produces = MediaType.TEXT_HTML_VALUE)
	public String viewcart() {
		logger.info("session: " + this.sessionUser.getSessionId());
		logger.info("jsession: " + this.sessionUser.getJSessionId());
		logger.info("csrf: " + this.sessionUser.getCsrfToken());
		
		this.sessionUser.getTelemetryClient().trackEvent(
				String.format("PetStoreApp user %s requesting view cart", this.sessionUser.getName()),
				this.sessionUser.getCustomEventProperties(), null);

		Order order = this.petStoreService.retrieveOrder(this.sessionUser.getSessionId());
		
		StringBuilder sb = new StringBuilder();
		sb.append("You do not have any items in your cart and/or your order has been completed.");

		if (order != null && order.getProducts() != null && !order.isComplete()) {
			sb = new StringBuilder();
			sb.append("Your order contains a ");
			for (int i = 0; i < order.getProducts().size(); i++) {
				sb.append(order.getProducts().get(i).getName()).append(" with a quantity ").append(order.getProducts().get(i).getQuantity());
				if (i < order.getProducts().size() - 1) {
					sb.append(", a ");
				}
			}
		}
		
		return sb.toString();
	}

	// helper api call for soul machines dp demo... POST URL Encoding intermittent missing headers with POST/FORM Encoding hence the GET hack with UUID
	@GetMapping(value = "/api/completecart", produces = MediaType.TEXT_HTML_VALUE)
	public String completecart(Model model, @RequestParam Map<String, String> params, HttpServletRequest request) {
		logger.info("session: " + this.sessionUser.getSessionId());
		logger.info("jsession: " + this.sessionUser.getJSessionId());
		logger.info("csrf: " + this.sessionUser.getCsrfToken());

		if(params.get("csrf") == null || !params.get("csrf").equals(this.sessionUser.getCsrfToken()))
		{
			return "Invalid CSRF token incoming was '" + params.get("csrf")+"'";
		}
		
		this.sessionUser.getTelemetryClient().trackEvent(
				String.format("PetStoreApp user %s requesting complete cart", this.sessionUser.getName()),
				this.sessionUser.getCustomEventProperties(), null);

		if(this.sessionUser.getSessionId().equals(this.sessionUser.getJSessionId()))
		{
			return "Please login to complete your order.";
		}

		try
		{
			this.petStoreService.updateOrder(0, 0, true);
			return "I just completed your order.";
		}
		catch (Exception e)
		{
			return "I'm sorry, I was unable to complete your order.";
		}
	}

	// helper api call for soul machines dp demo...
	@GetMapping(value = "/api/cartcount", produces = MediaType.TEXT_HTML_VALUE)
	public String cartcount() {
		logger.info("session: " + this.sessionUser.getSessionId());
		logger.info("jsession: " + this.sessionUser.getJSessionId());
		logger.info("csrf: " + this.sessionUser.getCsrfToken());
		
		this.sessionUser.getTelemetryClient().trackEvent(
				String.format("PetStoreApp user %s requesting cart count", this.sessionUser.getName()),
				this.sessionUser.getCustomEventProperties(), null);

		return String.valueOf(this.sessionUser.getCartCount());
	}
	
	@GetMapping(value = "/introspectionSimulation", produces = MediaType.APPLICATION_JSON_VALUE)
	public String introspectionSimulation(Model model, HttpServletRequest request,
			@RequestParam(name = "sessionIdToIntrospect") Optional<String> sessionIdToIntrospect) {
		boolean active = (sessionIdToIntrospect != null && sessionIdToIntrospect.isPresent()
				&& sessionIdToIntrospect.get() != null
				&& sessionIdToIntrospect.get().equals(request.getHeader("session-id")));

			return "{\n" + 
				"  \"active\": " + active + ",\n" + 
					"  \"scope\": \"read write email\",\n" + 
					"  \"client_id\": \""+request.getHeader("session-id")+"\",\n" + 
					"  \"username\": \""+request.getHeader("session-id")+"\",\n" + 
					"  \"exp\": 1911221039\n" + 
					"}";
		}
}
