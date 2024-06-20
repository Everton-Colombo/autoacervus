package com.example.autoacervus.daemon;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.proxy.AcervusProxyRequests;

public class BookRenewerThread extends Thread {
    private LinkedBlockingQueue<User> renewalQueue;
    private UserDAO userDao;

    private Logger logger = Logger.getLogger(BookRenewerThread.class.getName());

    public BookRenewerThread(List<User> users, UserDAO userDao) {
        super();
        this.renewalQueue = new LinkedBlockingQueue<User>(users);
        this.userDao = userDao;
    }

    @Override
    public void run() {
        while (!renewalQueue.isEmpty()) {
            User user = renewalQueue.poll();
            AcervusProxyRequests proxy = new AcervusProxyRequests();
            try {
                this.logger.info("Logging in as " + user.getEmailDac());

                proxy.login(user);
                List<BorrowedBook> renewedBooks = proxy.renewBooksDueToday().getSuccessfullyRenewedBooks();

                String borrowedBookString = renewedBooks.isEmpty()
                        ? "No books to renew."
                        : "Renewed books:";
                for (BorrowedBook book : renewedBooks) {
                    borrowedBookString += "\n- " + book;
                }
                this.logger.info(borrowedBookString);

                // Update list of borrowed books; grabs any new entries and updates renewal
                // dates.
                List<BorrowedBook> borrowedBooks = proxy.getBorrowedBooks();
                String registeredBookString = borrowedBooks.isEmpty()
                        ? "No books to register."
                        : "Registered books:";
                for (BorrowedBook book : borrowedBooks) {
                    registeredBookString += "\n" + book;
                }
                this.logger.info(registeredBookString);

                user.updateBorrowedBooks(borrowedBooks); // TODO: check whether to use updateBorrowedBooks() or setBorrowedBooks()
                this.userDao.save(user);
            } catch (Exception e) {
                this.logger.warning(
                        "Failed to renew books due today for user " + user.getEmailDac() +
                                ". Exception:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.logger.warning("Book renewal thread interrupted.");
    }
}
