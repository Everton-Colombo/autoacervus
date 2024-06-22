package com.example.autoacervus.service;

import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;

import javax.security.auth.login.LoginException;
import java.util.List;

public interface RegistrationService {
    boolean verifyUser(User user);

    void saveUser(User user);

    boolean userExists(String username);

    List<BorrowedBook> getBorrowedBooks() throws LoginException;
}
