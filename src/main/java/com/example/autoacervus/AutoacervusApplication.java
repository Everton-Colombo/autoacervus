package com.example.autoacervus;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.proxy.AcervusProxy;
import com.example.autoacervus.proxy.AcervusProxyRequests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;
import java.util.logging.Logger;

@SpringBootApplication
@EnableAsync
public class AutoacervusApplication {

	private Logger logger = Logger.getLogger(AutoacervusApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(AutoacervusApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

		};
	}

}
