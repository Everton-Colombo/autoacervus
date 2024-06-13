package com.example.autoacervus.daemon;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.entity.User;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookRenewerDaemon {
    @Value("${autoacervus.renewalMaxUsersPerThread}")
    private static int maxThreads;

    @Value("${autoacervus.renewalCronExpression}")
    private static String cronExpression;

    private static UserDAO userDao;

    private static Logger logger = Logger.getLogger(BookRenewerDaemon.class.getName());

    @Scheduled(cron = "#{@bookRenewerDaemon.cronExpression}")
    public void execute() {
        LinkedList<Thread> bookRenewerThreads = new LinkedList<Thread>();
        // Probably faster to use List<User> but this is more consistent with the other
        // lists' types.
        LinkedList<User> users = (LinkedList<User>) userDao.getUsersWithBooksDueToday();

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
            BookRenewerThread thread = new BookRenewerThread(threadUsers);
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
