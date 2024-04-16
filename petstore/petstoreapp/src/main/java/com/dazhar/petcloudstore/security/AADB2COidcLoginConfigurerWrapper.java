package com.dazhar.petcloudstore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.microsoft.azure.spring.autoconfigure.b2c.AADB2COidcLoginConfigurer;

/**
 * Ebables Azure B2C if and only when the required Azure properties are present
 *
 */
@Component
@ConditionalOnProperty({ "azure.activedirectory.b2c.tenant", "azure.activedirectory.b2c.client-id",
		"azure.activedirectory.b2c.client-secret", "azure.activedirectory.b2c.logout-success-url" })
public class AADB2COidcLoginConfigurerWrapper {

	@Autowired(required = false)
	private AADB2COidcLoginConfigurer configurer = null;

	public AADB2COidcLoginConfigurer getConfigurer() {
		return configurer;
	}

	public void setConfigurer(AADB2COidcLoginConfigurer configurer) {
		this.configurer = configurer;
	}
}
