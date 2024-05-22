package com.example.autoacervus;

import com.example.autoacervus.daemon.BookRenewerDaemon;
import org.apache.commons.exec.CommandLine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AutoacervusApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoacervusApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			// Por enquanto, pode-se usar esse bloco de código como uma espécie de função main.

			System.out.println("Hello World!");
			new BookRenewerDaemon().run();
		};
	}

}
