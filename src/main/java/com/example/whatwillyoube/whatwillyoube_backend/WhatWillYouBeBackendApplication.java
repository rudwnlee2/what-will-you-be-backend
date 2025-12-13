package com.example.whatwillyoube.whatwillyoube_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class WhatWillYouBeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhatWillYouBeBackendApplication.class, args);
	}

}
