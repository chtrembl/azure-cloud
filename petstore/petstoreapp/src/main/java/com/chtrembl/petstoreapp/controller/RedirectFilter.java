package com.chtrembl.petstoreapp.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.chtrembl.petstoreapp.model.PetStoreRequest;

@Component
@Order(2)
public class RedirectFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(RedirectFilter.class);

	@Autowired
	private PetStoreRequest petStoreRequest;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		petStoreRequest.setHost(((HttpServletRequest) request).getHeader("Host"));
		chain.doFilter(request, response);
	}

}
