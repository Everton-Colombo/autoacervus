package com.example.autoacervus;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.proxy.AcervusProxy;
import com.example.autoacervus.proxy.AcervusProxySelenium;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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
//			// Por enquanto, pode-se usar esse bloco de código como uma espécie de função
//			// main.
//
//			WebDriver driver = new ChromeDriver();
//			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
//			driver.manage().window().setSize(new Dimension(1280, 720));
//
//			AcervusProxy proxy = new AcervusProxySelenium(driver);
//
//			User sampleUser = new User("e257234@dac.unicamp.br", "570366");
//			proxy.login(sampleUser);
//			List<BorrowedBook> borrowedBooks = proxy.getBorrowedBooks();
//			System.out.println(borrowedBooks);
//			sampleUser.setBorrowedBooks(borrowedBooks);
//
//			userDAO.updateUser(sampleUser);
		};
	}

}
