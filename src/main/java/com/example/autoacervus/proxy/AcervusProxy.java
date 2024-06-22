package com.example.autoacervus.proxy;

import com.example.autoacervus.model.BookRenewalResult;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import org.springframework.stereotype.Component;
import javax.security.auth.login.LoginException;
import java.util.List;

@Component
public abstract class AcervusProxy {
    protected User user;

    public abstract boolean login(User user);
    public abstract void logout();

    public abstract List<BorrowedBook> getBorrowedBooks() throws LoginException;

    public abstract BookRenewalResult renewBooks(List<BorrowedBook> books) throws LoginException;

    public abstract BookRenewalResult renewBooksDueToday() throws LoginException;
}
