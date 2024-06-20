package com.example.autoacervus.rest;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.BookRenewalResult;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.proxy.AcervusProxy;
import com.example.autoacervus.service.MailService;
import com.example.autoacervus.service.MailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;
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

    @Autowired
    private MailService mailServiceImpl;

    @PostMapping("/sendMail")
    public String sendMail() {
//        emailService.sendSimpleMail("e.rcolombo2@gmail.com", "Teste", "aoba");
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("message", "Hello World");
        emailService.sendHtmlTemplateMail("e.rcolombo2@gmail.com", "Teste", "mail/test-template.html", templateModel);

        return "done";
    }

    @GetMapping("/testdb")
    public String testDb() {
        for (User user : userDAO.findAll()) {
            System.out.println(user);
        }
//        System.out.println(userDAO.findAll());
        return "done";
    }

    @GetMapping("updateUser")
    public String updateUser() {
        User user = userDAO.findByEmailDac("e257234@dac.unicamp.br");
        try {
            acervusProxy.login(user);
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }

        user.setBorrowedBooks(acervusProxy.getBorrowedBooks());

        userDAO.save(user);

        return "done";
    }

    @GetMapping("/testRenewal")
    public String testRenewal() {
        User user = userDAO.findByEmailDac("e257234@dac.unicamp.br");
        try {
            acervusProxy.login(user);
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }

        List<BorrowedBook> borrowedBooks = acervusProxy.getBorrowedBooks();
        System.out.println(borrowedBooks);
        BookRenewalResult result = acervusProxy.renewBooks(borrowedBooks);
        System.out.println(result);
        user.setBorrowedBooks(borrowedBooks);
        userDAO.save(user);

        return "done";
    }

    @GetMapping("sum")
    public String sum() {
        User user = userDAO.findByEmailDac("e257234@dac.unicamp.br");
        try {
            acervusProxy.login(user);
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }

        List<BorrowedBook> borrowedBooks = acervusProxy.getBorrowedBooks();
        BookRenewalResult result = acervusProxy.renewBooks(borrowedBooks);
        System.out.println(result);

        emailService.sendHtmlTemplateMail(user.getEmailDac(), "Relatório de operações",
                "mail/renewal_summary.html", Map.of("renewedBooks", result.getSuccessfullyRenewedBooks(),
                        "notRenewedBooks", result.getNotRenewedBooks(), "lastRenewalBooks", result.getRenewalLimitJustExceededBooks()));

        return "done";
    }
}
