package com.example.autoacervus.daemon;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.proxy.AcervusProxy;
import com.example.autoacervus.proxy.AcervusProxyRequests;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class BookRenewerDaemon extends Thread {
    private LinkedBlockingQueue<User> renewalQueue;
    private AcervusProxy proxy;
    private UserDAO userDao;
    private Logger logger = Logger.getLogger(BookRenewerDaemon.class.getName());

    public BookRenewerDaemon() {
        super();
        this.proxy = new AcervusProxyRequests();
        // this.userDao = new UserDaoHibernateJpa();

        renewalQueue = new LinkedBlockingQueue<>(userDao.getUsersWithNoBorrowedBooks());
    }

    @Override
    public void run() {
        while (!renewalQueue.isEmpty()) {
            User nextUser = renewalQueue.poll();

            try {
                proxy.login(nextUser);

                List<BorrowedBook> renewedBooks = proxy.renewBooksDueToday();
                String borrowedBookString = renewedBooks.isEmpty() ? "No books to renew." : "Renewed books:";
                for (BorrowedBook book : renewedBooks) {
                    borrowedBookString += "\n" + book;
                }
                this.logger.info(borrowedBookString);

                // Update list of borrowed books; grabs any new entries and updates renewal
                // dates.
                List<BorrowedBook> borrowedBooks = proxy.getBorrowedBooks();
                String registeredBookString = borrowedBooks.isEmpty() ? "No books to register." : "Registered books:";
                for (BorrowedBook book : borrowedBooks) {
                    registeredBookString += "\n" + book;
                }
                this.logger.info(registeredBookString);
                nextUser.updateBorrowedBooks(borrowedBooks);
                userDao.save(nextUser);

            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.logger.severe("Book renewal daemon interrupted.");
    }

}
