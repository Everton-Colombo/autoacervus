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

import java.time.Duration;
import java.util.List;

@SpringBootApplication
public class AutoacervusApplication {

	@Autowired
	private UserDAO userDAO;

	public static void main(String[] args) {
		SpringApplication.run(AutoacervusApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			// Por enquanto, pode-se usar esse bloco de código como uma espécie de função
			// main.
			AcervusProxy proxy = new AcervusProxyRequests();

			User sampleUser = new User("l252615@dac.unicamp.br", "123110");
			final boolean hasLoggedIn = proxy.login(sampleUser);
			if (!hasLoggedIn) {
				System.out.println("Failed to log in.");
				return;
			}
			// List<BorrowedBook> borrowedBooks = proxy.getBorrowedBooks();
			// System.out.println(borrowedBooks);
			// sampleUser.setBorrowedBooks(borrowedBooks);

			// userDAO.updateUser(sampleUser);
		};
	}

}
