package com.chtrembl.petstoreapp;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.chtrembl.petstoreapp.security.AADB2COidcLoginConfigurerWrapper;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableCaching
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class AppConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	}

	@Bean
	public AADB2COidcLoginConfigurerWrapper aadB2COidcLoginConfigurerWrapper() {
		return new AADB2COidcLoginConfigurerWrapper();
	}

	@Bean
	public Caffeine caffeineConfig() {
		return Caffeine.newBuilder().expireAfterAccess(300, TimeUnit.SECONDS);
	}

	@Bean
	public CacheManager currentUsersCacheManager(Caffeine caffeine) {
		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
		caffeineCacheManager.setCaffeine(caffeine);

		return caffeineCacheManager;
	}
}