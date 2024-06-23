package com.example.autoacervus.rest;

import com.example.autoacervus.model.BookRenewalResult;
import com.example.autoacervus.model.entity.BorrowedBook;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.service.MailService;
import com.example.autoacervus.service.RegistrationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/verification")
public class VerificationApiController {

    private final RegistrationService registrationService;
    private final MailService mailService;
    private final SpringTemplateEngine thymeleafTemplateEngine;

    private final Logger logger = Logger.getLogger(VerificationApiController.class.getName());

    @Autowired
    public VerificationApiController(RegistrationService registrationService, MailService mailService, SpringTemplateEngine thymeleafTemplateEngine) {
        this.registrationService = registrationService;
        this.mailService = mailService;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    @PostMapping("/verify")
    public String verify(HttpServletRequest request, @RequestParam String username, @RequestParam String password) throws LoginException {
        logger.info("[verify()] Verifying user " + username);

        User user = new User(username, password);
        boolean verified = registrationService.verifyUser(user);

        Context thymeleafContext = new Context();

        if (!verified) {
            thymeleafContext.setVariables(Map.of("message", "Credenciais inválidas!"));
            return thymeleafTemplateEngine.process("fragments/terminal-fail.html", thymeleafContext);
        }

        mailService.sendHtmlTemplateMail(user.getEmailDac(), "Bem-vindo!", "mail/welcome.html");

        // Renew any books that are due today, as today's operations have most likely already ended:
        BookRenewalResult result = registrationService.performFirstRenewal(user);
        if (!result.getAllBooksRequestedForRenewal().isEmpty()) {
            user.getUserStats().incrementRenewalCount(result.getSuccessfullyRenewedBooks().size());
            mailService.sendHtmlTemplateMail(user.getEmailDac(), "Relatório de renovações (debug)",
                    "mail/renewal_summary.html", Map.of("renewedBooks", result.getSuccessfullyRenewedBooks(),
                            "notRenewedBooks", result.getNotRenewedBooks(), "lastRenewalBooks",
                            result.getRenewalLimitJustExceededBooks()));
        }
        // Save user details to database
        List<BorrowedBook> userBooks = registrationService.getBorrowedBooks();
        user.setBorrowedBooks(userBooks);
        registrationService.saveUser(user);

        // Login after verification:
        try {
            request.login(username, password);
        } catch (ServletException e) {
            logger.severe("[verify()] COULDN'T LOGIN : ServletException: " + e.getMessage());
            e.printStackTrace();
        }

        thymeleafContext.setVariables(Map.of("borrowedBooks", userBooks));
        return thymeleafTemplateEngine.process("fragments/terminal-success.html", thymeleafContext);
    }

}
