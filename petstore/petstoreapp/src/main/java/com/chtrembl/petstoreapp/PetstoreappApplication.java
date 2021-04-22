package com.chtrembl.petstoreapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetstoreappApplication {
	private static Logger logger = LoggerFactory.getLogger(PetstoreappApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PetstoreappApplication.class, args);
		logger.info("PetStoreApp started up... " + System.getProperty("catalina.base"));
	}
}
