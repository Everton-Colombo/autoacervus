package com.example.autoacervus.service;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.exception.LoginException;
import com.example.autoacervus.model.BookRenewalResult;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.proxy.AcervusProxy;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserDAO userDao;
    private final AcervusProxy acervusProxy;

    private final Logger logger = Logger.getLogger(RegistrationServiceImpl.class.getName());

    @Autowired
    public RegistrationServiceImpl(UserDAO userDao, AcervusProxy acervusProxy) {
        this.userDao = userDao;
        this.acervusProxy = acervusProxy;
    }

    @Override
    public boolean verifyUser(User user) {
        try {
            return acervusProxy.login(user);
        } catch (Exception e) {
            this.logger.severe("[verifyUser()] Could not verify user " + user.getEmailDac() + ". Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void saveUser(User user) {
        this.logger.info("[saveUser()] Saving user with email " + user.getEmailDac());
        userDao.save(user);
    }

    @Override
    public BookRenewalResult performFirstRenewal(User user) {
        BookRenewalResult result = new BookRenewalResult();
        try {
            result = acervusProxy.renewBooksDueToday();
        } catch (LoginException e) {
            logger.severe("[performFirstRenewal()] Login exception. Error: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<BorrowedBook> getBorrowedBooks() throws LoginException {
        return this.acervusProxy.getBorrowedBooks();
    }

    @Override
    public boolean userExists(String username) {
        return userDao.existsById(username);
    }
}
