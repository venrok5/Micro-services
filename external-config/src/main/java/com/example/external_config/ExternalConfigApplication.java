package com.example.external_config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ExternalConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExternalConfigApplication.class, args);
	}

}
