package com.example.autoacervus.daemon;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.service.MailService;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookRenewerDaemon {
    @Value("${autoacervus.renewalMaxUsersPerThread}")
    private int maxThreads;

    @Autowired
    private UserDAO userDao;

    private static final Logger logger = Logger.getLogger(BookRenewerDaemon.class.getName());

    @Autowired
    private MailService mailService;

    @Scheduled(cron = "${autoacervus.renewalCronExpression}")
    public void execute() {
        LinkedList<Thread> bookRenewerThreads = new LinkedList<Thread>();

        List<User> users = new LinkedList<>();
        users.addAll(userDao.getUsersWithBooksDueToday());
        users.addAll(userDao.getUsersWithNoBorrowedBooks());

        if (users.isEmpty()) {
            logger.info("No users to operate upon.");
            return;
        }

        int maxUsersPerThread = users.size() / maxThreads + 1;

        stopLoops: for (int i = 0; i < maxThreads; i++) {
            LinkedList<User> threadUsers = new LinkedList<User>();
            for (int j = 0; j < maxUsersPerThread; j++) {
                int idx = i * maxUsersPerThread + j;
                if (idx >= users.size()) {
                    break stopLoops;
                }
                threadUsers.add(users.get(idx));
            }
            BookRenewerThread thread = new BookRenewerThread(threadUsers, this.userDao, this.mailService);
            thread.start();
            bookRenewerThreads.add(thread);
        }

        // Join all threads (wait for all of them to finish). Not a good idea to join
        // right after starting them because it would be the same as running them
        // sequentially.
        for (Thread thread : bookRenewerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.warning("Thread interrupted. Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
