package com.bazaarx.bazaarxbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Base64;
import java.util.List;

@SpringBootApplication
@RestController
public class BazaarxBackendApplication {


	public static void main(String[] args) {
		SpringApplication.run(BazaarxBackendApplication.class, args);
	}





}
