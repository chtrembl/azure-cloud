package com.chtrembl.petstoreapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chtrembl.petstoreapp.model.User;

@RestController
public class RestAPIController {

	@Autowired
	private User sessionUser;

	@GetMapping("/api/contactus")
	public String contactus() {

		this.sessionUser.getTelemetryClient()
				.trackEvent(String.format("user %s requesting Contact Us", this.sessionUser.getName()));

		return "Please contact Rhody PetStore at 401-555-5555";
	}

	@GetMapping("/api/sessionid")
	public String sessionid() {

		return this.sessionUser.getSessionId();
	}

}
