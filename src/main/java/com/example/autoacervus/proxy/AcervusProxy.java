package com.example.autoacervus.proxy;

import com.example.autoacervus.model.BookRenewalResult;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import org.springframework.stereotype.Component;
import javax.security.auth.login.LoginException;
import java.util.List;

@Component
public interface AcervusProxy {
    boolean login(User user) throws LoginException;

    List<BorrowedBook> getBorrowedBooks();

    BookRenewalResult renewBooks(List<BorrowedBook> book);

    BookRenewalResult renewBooksDueToday() throws LoginException;
}
