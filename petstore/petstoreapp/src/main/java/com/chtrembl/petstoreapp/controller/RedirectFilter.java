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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Second Filter in the chain to redirect traffic if needed. For the scenario
 * where a front facing web server sits in front of the deployed PaaS App
 * Service. Perhaps you want to enforce users hit the domain instead of the App
 * Service FQDN, just inject the host property and this filter will kick in.
 */
@Component
@Order(2)
public class RedirectFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(RedirectFilter.class);

	@Value("${host:}")
	private String host;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (StringUtils.isNotEmpty(this.host)) {
			String incomingHost = (((HttpServletRequest) request).getHeader("Host"));
			if (!this.host.equals(incomingHost)) {
				logger.info("redirecting user to " + this.host);
				((HttpServletResponse) response).sendRedirect("https://" + this.host);
				return;
			}
		}
		chain.doFilter(request, response);
	}

}
