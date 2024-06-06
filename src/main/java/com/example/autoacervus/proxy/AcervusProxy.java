package com.example.autoacervus.proxy;

import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;

import javax.security.auth.login.LoginException;
import java.util.List;

public interface AcervusProxy {
    boolean login(User user) throws LoginException;

    List<BorrowedBook> getBorrowedBooks();

    boolean renewBook(BorrowedBook book) throws LoginException;

    List<BorrowedBook> renewBooksDueToday() throws LoginException;
}
