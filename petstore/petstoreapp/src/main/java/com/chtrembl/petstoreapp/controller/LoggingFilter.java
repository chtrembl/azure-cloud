package com.chtrembl.petstoreapp.controller;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.chtrembl.petstoreapp.model.ContainerEnvironment;
import com.chtrembl.petstoreapp.model.WebRequest;

/**
 * First Filter in the chain to set some MDC data for logging purposes, since
 * this is statis data, nothing request scope (yet), this could be moved to the
 * logging context singleton.
 */
@Component
@Order(1)
public class LoggingFilter implements Filter {

	@Autowired
	private ContainerEnvironment containerEnvironment;

	@Autowired
	private WebRequest webRequest;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		MDC.put("appVersion", this.containerEnvironment.getAppVersion());
		MDC.put("appDate", this.containerEnvironment.getAppDate());
		MDC.put("containerHostName", this.containerEnvironment.getContainerHostName());

		if (containerEnvironment.getAdditionalHeadersToLog().size() > 0) {
			Enumeration<String> headerNames = ((HttpServletRequest) request).getHeaderNames();
			if (headerNames != null) {
				try {
					while (headerNames.hasMoreElements()) {
						String key = headerNames.nextElement();
						String value = ((HttpServletRequest) request).getHeader(key);
						System.out.println("Header: " + key + "=" + value);
					}

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}

		String additionalHeadersToLog = "";
		if(containerEnvironment.getAdditionalHeadersToLog().size()>0)
		{
			StringBuilder sb = new StringBuilder();
			// building out %X{additionalHeadersToLog}
			for (String headerKey : containerEnvironment.getAdditionalHeadersToLog())
			{
				String headerValue = ((HttpServletRequest) request).getHeader(headerKey);

				sb.append(headerKey.trim() + "=" + headerValue + ' ');

				if (containerEnvironment.getAdditionalHeadersToSend().size() > 0) {
					if (containerEnvironment.getAdditionalHeadersToSend().contains(headerKey.trim())) {
						this.webRequest.addHeader(headerKey, headerValue);
					}
				}
			}
			additionalHeadersToLog = sb.toString();
		}

		MDC.put("additionalHeadersToLog", additionalHeadersToLog);
		chain.doFilter(request, response);
	}

}
