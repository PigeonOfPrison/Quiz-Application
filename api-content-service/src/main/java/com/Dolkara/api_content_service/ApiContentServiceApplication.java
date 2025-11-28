package com.Dolkara.api_content_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ApiContentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiContentServiceApplication.class, args);
	}

}
