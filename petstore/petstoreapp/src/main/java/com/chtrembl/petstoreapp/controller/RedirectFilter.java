package com.chtrembl.petstoreapp.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.chtrembl.petstoreapp.model.PetStoreRequest;

@Component
@Order(2)
public class RedirectFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(RedirectFilter.class);

	@Value("${host:}")
	private String host;

	@Autowired
	private PetStoreRequest petStoreRequest;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (StringUtils.isNotEmpty(this.host)) {
			String incomingHost = (((HttpServletRequest) request).getHeader("Host"));
			this.petStoreRequest.setHost(incomingHost);
			if (!this.host.equals(incomingHost)) {
				logger.info("redirecting user to " + this.host);
				((HttpServletResponse) response).sendRedirect("https://" + this.host);
			}
		}
		chain.doFilter(request, response);
	}

}
