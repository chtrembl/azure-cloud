package com.chtrembl.petstoreapp.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.chtrembl.petstoreapp.model.ContainerEnvironment;

@Component
@Order(1)
public class LoggingFilter implements Filter {

	@Autowired
	private ContainerEnvironment containerEnvironment;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		MDC.put("appVersion", this.containerEnvironment.getAppVersion());
		MDC.put("appDate", this.containerEnvironment.getAppDate());
		MDC.put("containerHostName", this.containerEnvironment.getContainerHostName());

		chain.doFilter(request, response);
	}

}
