package com.example.autoacervus.service;

import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;

import java.util.List;

public interface RegistrationService {
    boolean verifyUser(User user);
    void saveUser(User user);
    List<BorrowedBook> getBorrowedBooks();  // TODO move this to another service. (responsibility overlap)
}
