package com.example.autoacervus.daemon;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.BookRenewalResult;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.proxy.AcervusProxyRequests;
import com.example.autoacervus.service.MailService;

public class BookRenewerThread extends Thread {
    private final LinkedBlockingQueue<User> renewalQueue;
    private final UserDAO userDao;

    private final Logger logger = Logger.getLogger(BookRenewerThread.class.getName());

    private final MailService mailService;

    public BookRenewerThread(List<User> users, UserDAO userDao, MailService mailService) {
        super();
        this.renewalQueue = new LinkedBlockingQueue<>(users);
        this.userDao = userDao;
        this.mailService = mailService;
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

                String failedString = failedRenewals.isEmpty()
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

                user.setBorrowedBooks(borrowedBooks);
                user.getUserStats().incrementRenewalCount(renewals.size());
                this.userDao.save(user);

                // Only send email if some renewal happened (or failed to do so) and user wants
                // to receive emails.
                if (renewalResult.getAllBooksRequestedForRenewal().isEmpty() ||
                        !user.getSettings().getReceiveEmails()) {
                    return;
                }

                mailService.sendHtmlTemplateMail(
                        user.getEmailDac(),
                        "Relatóro de renovações",
                        "mail/renewal_summary.html",
                        Map.of(
                                "renewedBooks", renewals,
                                "notRenewedBooks", failedRenewals,
                                "lastRenewalBooks", justExceededRenewals));
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
