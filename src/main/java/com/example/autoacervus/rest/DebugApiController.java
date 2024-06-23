package com.example.autoacervus.rest;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.BookRenewalResult;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.proxy.AcervusProxy;
import com.example.autoacervus.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debugApi")
public class DebugApiController {

    @Autowired
    private MailService emailService;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AcervusProxy acervusProxy;

    @GetMapping("demo")
    public String demo(Principal principal) throws LoginException {
        User user = userDAO.findByEmailDac(principal.getName());
        acervusProxy.login(user);

        List<BorrowedBook> borrowedBooks = acervusProxy.getBorrowedBooks();
        BookRenewalResult result = acervusProxy.renewBooks(borrowedBooks);
        user.setBorrowedBooks(borrowedBooks);
        user.getUserStats().incrementRenewalCount(result.getSuccessfullyRenewedBooks().size());
        userDAO.save(user);

        emailService.sendHtmlTemplateMail(user.getEmailDac(), "Relatório de renovações (debug)",
                "mail/renewal_summary.html", Map.of("renewedBooks", result.getSuccessfullyRenewedBooks(),
                        "notRenewedBooks", result.getNotRenewedBooks(), "lastRenewalBooks",
                        result.getRenewalLimitJustExceededBooks()));

        return "done";
    }
}
