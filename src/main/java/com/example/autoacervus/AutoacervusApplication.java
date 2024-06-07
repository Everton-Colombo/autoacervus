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

import java.util.List;
import java.util.logging.Logger;

@SpringBootApplication
public class AutoacervusApplication {

	@Autowired
	private UserDAO userDAO;

	private Logger logger = Logger.getLogger(AutoacervusApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(AutoacervusApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			// Por enquanto, pode-se usar esse bloco de código como uma espécie de função
			// main.
			AcervusProxy proxy = new AcervusProxyRequests();

			User sampleUser = new User("e257234@dac.unicamp.br", "570366");
			final boolean hasLoggedIn = proxy.login(sampleUser);
			if (!hasLoggedIn) {
				this.logger.severe("Failed to log in.");
				return;
			}
			List<BorrowedBook> borrowedBooks = proxy.getBorrowedBooks();
			this.logger.info(borrowedBooks.toString());
			sampleUser.setBorrowedBooks(borrowedBooks);
			proxy.renewBooks(borrowedBooks);
			userDAO.updateUser(sampleUser);
		};
	}

}
