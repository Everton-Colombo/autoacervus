package com.example.autoacervus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AutoacervusApplication {
	public static void main(String[] args) {
		// Spring bootstrap
		SpringApplication.run(AutoacervusApplication.class, args);
	}
}
