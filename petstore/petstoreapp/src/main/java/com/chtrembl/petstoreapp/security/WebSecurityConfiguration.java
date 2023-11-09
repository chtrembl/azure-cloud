package com.chtrembl.petstoreapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.chtrembl.petstoreapp.model.ContainerEnvironment;

/**
 * Wire up Spring Security
 */
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private static Logger logger = LoggerFactory.getLogger(WebSecurityConfiguration.class);

	@Autowired(required = false)
	private AADB2COidcLoginConfigurerWrapper aadB2COidcLoginConfigurerWrapper = null;

	@Autowired
	private ContainerEnvironment containeEnvironment;

	@Override
	public void configure(WebSecurity web) throws Exception {
		if (this.aadB2COidcLoginConfigurerWrapper != null
				&& this.aadB2COidcLoginConfigurerWrapper.getConfigurer() != null) {
			web.ignoring().antMatchers("/static/**");
			web.ignoring().antMatchers("/content/**");
			web.ignoring().antMatchers("/.well-known/**");
		}
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		if (this.aadB2COidcLoginConfigurerWrapper != null
				&& this.aadB2COidcLoginConfigurerWrapper.getConfigurer() != null) {

			http.csrf().ignoringAntMatchers("/signalr/**").and().authorizeRequests().antMatchers("/")
					.permitAll()
					.antMatchers("/*breed*").permitAll()
					.antMatchers("/*product*").permitAll()
					.antMatchers("/*cart*").permitAll()
					.antMatchers("/api/contactus").permitAll()
					.antMatchers("/api/updatecart").permitAll()
					.antMatchers("/api/completecart").permitAll()
					.antMatchers("/api/cartcount").permitAll()
					.antMatchers("/api/viewcart").permitAll()
					.antMatchers("/slowness").permitAll()
					.antMatchers("/exception").permitAll()
					.antMatchers("/introspectionSimulation*").permitAll()
					.antMatchers("/bingSearch*").permitAll()
					.antMatchers("/signalr/negotiate").permitAll()
					.antMatchers("/signalr/test").permitAll()
					.antMatchers("/login*").permitAll()
					.antMatchers("/soulmachines*").permitAll()
					.antMatchers("/intelligence*").permitAll()
					.antMatchers("/i2xhack*").permitAll()
					.antMatchers("/pets*").permitAll()
					.antMatchers("/raadcnnai*").permitAll()
					.antMatchers("/hybridConnection").permitAll().anyRequest()
					.authenticated().and().apply(this.aadB2COidcLoginConfigurerWrapper.getConfigurer()).and()
					.oauth2Login().loginPage("/login");

			this.containeEnvironment.setSecurityEnabled(true);
		} else {
			logger.warn(
					"azure.activedirectory.b2c.tenant, azure.activedirectory.b2c.client-id, azure.activedirectory.b2c.client-secret and azure.activedirectory.b2c.logout-success-url must be set for Azure B2C Authentication to be enabled, considering configuring Azure B2C App Registration if you would like to authenticate users.");
		}
	}
}
