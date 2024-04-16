package com.dazhar.petcloudstore.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dazhar.petcloudstore.model.Breed;
import com.dazhar.petcloudstore.model.Order;
import com.dazhar.petcloudstore.model.Pet;
import com.dazhar.petcloudstore.model.User;
import com.dazhar.petcloudstore.model.WebPages;
import com.dazhar.petcloudstore.repository.BreedRepository;
import com.dazhar.petcloudstore.service.PetStoreService;
import com.dazhar.petcloudstore.service.SearchService;
import com.microsoft.applicationinsights.telemetry.PageViewTelemetry;

/**
 * Web Controller for all of the model/presentation construction and various
 * endpoints
 */
@Controller
public class WebAppController {
	private static Logger logger = LoggerFactory.getLogger(WebAppController.class);

	@Autowired
	private PetStoreService petStoreService;

	@Autowired
	private SearchService searchService;

	@Autowired
	private User sessionUser;
	
	@Autowired(required = false)
	private BreedRepository breedRepository;
	

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

		// for stateless container(s), this container may not have products loaded, this
		// is a temp fix until we implement caching, specifically a distributed/redis
		// cache
		Collection<Pet> pets = this.petStoreService.getPets(category);

		Pet pet = new Pet();

		if (pets != null) {
			pet = this.sessionUser.getPets().get(id - 1);
		}

		model.addAttribute("products",
				this.petStoreService.getProducts(pet.getCategory().getName() + " " + category, pet.getTags()));
		return "products";
	}

	@GetMapping(value = "/cart")
	public String cart(Model model, OAuth2AuthenticationToken token, HttpServletRequest request) {
		Order order = this.petStoreService.retrieveOrder(this.sessionUser.getSessionId());
		model.addAttribute("order", order);
		int cartSize = 0;
		if (order != null && order.getProducts() != null && !order.isComplete()) {
			cartSize = order.getProducts().size();
		}
		this.sessionUser.setCartCount(cartSize);
		model.addAttribute("cartSize", this.sessionUser.getCartCount());
		if (token != null) {
			model.addAttribute("userLoggedIn", true);
			model.addAttribute("email", this.sessionUser.getEmail());
		}
		return "cart";
	}

	@PostMapping(value = "/updatecart")
	public String updatecart(Model model, OAuth2AuthenticationToken token, HttpServletRequest request,
			@RequestParam Map<String, String> params) {
		int cartCount = 1;

		String operator = params.get("operator");
		if (StringUtils.isNotEmpty(operator)) {
			if ("minus".equals(operator)) {
				cartCount = -1;
			}
		}

		this.petStoreService.updateOrder(Long.valueOf(params.get("productId")), cartCount, false);
		return "redirect:cart";
	}

	@PostMapping(value = "/completecart")
	public String updatecart(Model model, OAuth2AuthenticationToken token, HttpServletRequest request) {
		if (token != null) {
			this.petStoreService.updateOrder(0, 0, true);
		}
		return "redirect:cart";
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

		// 30s delay
		Thread.sleep(30000);

		this.sessionUser.getTelemetryClient().trackPageView(pageViewTelemetry);

		return "slowness";
	}

	@GetMapping(value = "/exception")
	public String exception(Model model, OAuth2AuthenticationToken token, HttpServletRequest request)
			throws URISyntaxException, InterruptedException {

		NullPointerException npe = new NullPointerException();

		logger.info("PetStoreApp simulating NullPointerException, routing to home view..." + npe.getStackTrace());

		this.sessionUser.getTelemetryClient().trackException(npe);

		return "exception";
	}

	@GetMapping(value = "/*")
	public String landing(Model model, OAuth2AuthenticationToken token, HttpServletRequest request)
			throws URISyntaxException {
		logger.info(String.format("PetStoreApp %s requested and %s is being routed to home view session %s",
				request.getRequestURI(), this.sessionUser.getName(), this.sessionUser.getSessionId()));
		PageViewTelemetry pageViewTelemetry = new PageViewTelemetry();
		pageViewTelemetry.setUrl(new URI(request.getRequestURL().toString()));
		pageViewTelemetry.setName("landing");
		this.sessionUser.getTelemetryClient().trackPageView(pageViewTelemetry);
		return "home";
	}

	@GetMapping(value = "/bingSearch")
	public String bingSearch(Model model) throws URISyntaxException {
		logger.info(String.format("PetStoreApp /bingsearch requested for %s, routing to bingSearch view...",
				this.sessionUser.getName()));
		String companies[] = { "Chewy", "PetCo", "PetSmart", "Walmart" };
		List<String> companiesList = Arrays.asList(companies);
		List<WebPages> webpages = new ArrayList<>();
		companiesList.forEach(company -> webpages.add(this.searchService.bingSearch(company)));
		model.addAttribute("companies", companiesList);
		model.addAttribute("webpages", webpages);

		return "bingSearch";
	}
	
	@GetMapping(value = "/hybridConnection")
	public String hybridConnection(Model model) throws URISyntaxException {
		logger.info(String.format("PetStoreApp /hybridConnection requested for %s, routing to hybridConnection view...",
				this.sessionUser.getName()));
		
		List<Breed> breeds = null;
		
		if(this.breedRepository != null)
		{
			breeds = this.breedRepository.findAll();
		}
		
		model.addAttribute("breeds", breeds);

		return "hybridConnection";
	}
	
	@GetMapping(value = "/soulmachines")
	public String soulmachines(Model model, HttpServletRequest request, @RequestParam("sid") Optional<String> sid,  @RequestParam("csrf") Optional<String> csrf, @RequestParam("arr") Optional<String> arr) throws URISyntaxException {
		logger.info(String.format("PetStoreApp /soulmachines requested for %s, routing to soulmachines view...",
				this.sessionUser.getName()));		

		// if the user hits this page without a sessions/csrf, redirect and establish one
		if(new HttpSessionCsrfTokenRepository().loadToken(request) == null)
		{
			return "redirect:/home";
		}

		if(new HttpSessionCsrfTokenRepository().loadToken(request) != null)
		{
			this.sessionUser.setCsrfToken(new HttpSessionCsrfTokenRepository().loadToken(request).getToken().toString());		
		}

		String arrAffinity = "";
		if(request.getCookies() != null)
		{
			for(int i = 0; i < request.getCookies().length; i++)
			{
				if(request.getCookies()[i].getName().equals("ARRAffinity"))
				{
					arrAffinity = request.getCookies()[i].getValue();
				}
			}
		}

		model.addAttribute("arrAffinity", arrAffinity);

		String url = request.getRequestURL().toString() + "?" + request.getQueryString();	
		if(!url.contains("sid") || !url.contains("csrf"))
		{
			return "redirect:soulmachines?sid="+this.sessionUser.getJSessionId()+"&csrf="+this.sessionUser.getCsrfToken()+"&arr="+arrAffinity;
		}
		
		return "soulmachines";
	}
	
	@GetMapping(value = "/intelligence")
	public String intelligence(Model model) throws URISyntaxException {
		logger.info(String.format("PetStoreApp /intelligence requested for %s, routing to intelligence view...",
				this.sessionUser.getName()));
	
		return "intelligence";
	}

	@GetMapping(value = "/i2xhack")
	public String i2xhack(Model model) throws URISyntaxException {
		logger.info(String.format("PetStoreApp /i2xhack requested for %s, routing to i2xhack view...",
				this.sessionUser.getName()));
	
		return "i2xhack";
	}

	@GetMapping(value = "/raadcnnai")
	public String raadcnnai(Model model) throws URISyntaxException {
		logger.info(String.format("PetStoreApp /raadcnnai requested for %s, routing to raadcnnai view...",
				this.sessionUser.getName()));
	
		return "raadcnnai";
	}

	@GetMapping(value = "/debug")
	public String debug(Model model, HttpServletRequest request) throws URISyntaxException {
		logger.info(String.format("PetStoreApp /debug requested for %s, routing to raadcnnai view...",
				this.sessionUser.getName()));
		
		model.addAttribute("cookies", request.getCookies());
		Map<String, String> headers = new HashMap<String, String>();
		request.getHeaderNames().asIterator().forEachRemaining(header -> {
			headers.put(header, request.getHeader(header));
		});
		model.addAttribute("headers", headers);
		

		return "debug";
	}
}
