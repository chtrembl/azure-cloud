package com.chtrembl.petstoreapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.azure.spring.cloud.autoconfigure.aadb2c.AadB2cOidcLoginConfigurer;

/**
 * Ebables Azure B2C if and only when the required Azure properties are present
 *
 */
@Component
@ConditionalOnProperty({ "azure.activedirectory.b2c.tenant", "azure.activedirectory.b2c.client-id",
		"azure.activedirectory.b2c.client-secret", "azure.activedirectory.b2c.logout-success-url" })
public class AADB2COidcLoginConfigurerWrapper {

	@Autowired(required = false)
	private AadB2cOidcLoginConfigurer configurer = null;

	public AadB2cOidcLoginConfigurer getConfigurer() {
		return configurer;
	}

	public void setConfigurer(AadB2cOidcLoginConfigurer configurer) {
		this.configurer = configurer;
	}
}
