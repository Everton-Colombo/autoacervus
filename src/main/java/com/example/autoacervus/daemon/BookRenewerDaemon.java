package com.example.autoacervus.daemon;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.proxy.AcervusProxy;
import com.example.autoacervus.proxy.AcervusProxySelenium;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.security.auth.login.LoginException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class BookRenewerDaemon extends Thread {
    private LinkedBlockingQueue<User> renewalQueue;
    private WebDriver driver;
    private AcervusProxy proxy;
    private UserDAO userDao;

    public BookRenewerDaemon() {
        super();
        this.driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        driver.manage().window().setSize(new Dimension(1280, 720));

        this.proxy = new AcervusProxySelenium(driver);
//        this.userDao = new UserDaoHibernateJpa();

        renewalQueue = new LinkedBlockingQueue<>(userDao.getUsersWithNoBorrowedBooks());
    }

    @Override
    public void run() {
        while (!renewalQueue.isEmpty()) {
            User nextUser = renewalQueue.poll();

            try {
                proxy.login(nextUser);

                List<BorrowedBook> renewedBooks = proxy.renewBooksDueToday();
                System.out.println("Renewed books:");
                for (BorrowedBook book : renewedBooks) {
                    System.out.println(book);
                }
                System.out.println("---");

                // Update list of borrowed books; grabs any new entries and updates renewal dates.
                List<BorrowedBook> borrowedBooks = proxy.getBorrowedBooks();
                System.out.println("Registering book: " + borrowedBooks);
                nextUser.updateBorrowedBooks(borrowedBooks);
                userDao.saveUser(nextUser);

            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        System.out.println("INTERRUPT");
    }


}
