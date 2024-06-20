package com.example.autoacervus.daemon;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.BookRenewalResult;
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
        this.logger.info("[Thread-" + this.getId() + "] A renewal thread has been fired up!");
        while (!renewalQueue.isEmpty()) {
            User user = renewalQueue.poll();
            AcervusProxyRequests proxy = new AcervusProxyRequests();
            try {
                this.logger.info("[Thread-" + this.getId() + "] Logging in as " + user.getEmailDac());

                proxy.login(user);
                BookRenewalResult renewalResult = proxy.renewBooksDueToday();

                List<BorrowedBook> renewals = renewalResult.getSuccessfullyRenewedBooks();
                List<BorrowedBook> failedRenewals = renewalResult.getNotRenewedBooks();
                List<BorrowedBook> justExceededRenewals = renewalResult.getRenewalLimitJustExceededBooks();

                String renewedString = renewals.isEmpty()
                        ? "No books to renew."
                        : "Renewed books:";
                for (BorrowedBook book : renewals) {
                    renewedString += "\n- " + book;
                }
                this.logger.info("[Thread-" + this.getId() + "] " + renewedString);

                String failedString = renewals.isEmpty()
                        ? "No renewals failed."
                        : "Failed renewals:";
                for (BorrowedBook book : failedRenewals) {
                    failedString += "\n- " + book;
                }
                this.logger.severe("[Thread-" + this.getId() + "] " + failedString);

                String exceededString = justExceededRenewals.isEmpty()
                        ? "No books have just reached renewal limit."
                        : "Books that have just reached renewal limit:";
                for (BorrowedBook book : justExceededRenewals) {
                    exceededString += "\n- " + book;
                }
                this.logger.warning("[Thread-" + this.getId() + "] " + exceededString);

                // Update list of borrowed books; grabs any new entries and updates renewal
                // dates.
                List<BorrowedBook> borrowedBooks = proxy.getBorrowedBooks();
                String registeredBookString = borrowedBooks.isEmpty()
                        ? "No books to register."
                        : "Registered books:";
                for (BorrowedBook book : borrowedBooks) {
                    registeredBookString += "\n" + book;
                }
                this.logger.info("[Thread-" + this.getId() + "] " + registeredBookString);

                user.updateBorrowedBooks(borrowedBooks); // setBorrowedBooks fails because you can't leave a stray list
                                                         // of borrowed books floating around (JPA)
                this.userDao.save(user);
            } catch (Exception e) {
                this.logger.warning(
                        "[Thread-" + this.getId() + "] Failed to renew books due today for user " + user.getEmailDac()
                                + ". Exception:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.logger.warning("[Thread-" + this.getId() + "] Book renewal thread interrupted.");
    }
}
