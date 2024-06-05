package com.example.autoacervus.proxy;

import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;
import java.util.List;


public interface AcervusProxy {
    boolean login(User user);
    List<BorrowedBook> getBorrowedBooks() throws LoginException;
    boolean renewBook(BorrowedBook book) throws LoginException;
    List<BorrowedBook> renewBooksDueToday() throws LoginException;
}
