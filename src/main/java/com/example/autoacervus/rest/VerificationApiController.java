package com.example.autoacervus.rest;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.encryption.AES256;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.service.MailService;
import com.example.autoacervus.service.RegistrationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/verification")
public class VerificationApiController {

    @Autowired
    RegistrationService registrationService;

    @Autowired
    MailService mailService;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Autowired
    private UserDAO userDAO;

    private Logger logger = Logger.getLogger(VerificationApiController.class.getName());

    @PostMapping("/verify")
    public String verify(HttpServletRequest request, @RequestParam String username, @RequestParam String password) {
        logger.info("Verifying username " + username);

        User user = this.userDAO.findByEmailDac(username);
        if (user == null) {
            user = new User(username, password);
            String salt = AES256.generateSalt();
            user.setSbuPassword(AES256.encrypt(password, salt));
            user.setPasswordSalt(salt);
        }
        boolean verified = registrationService.verifyUser(user);

        Context thymeleafContext = new Context();

        if (!verified) {
            thymeleafContext.setVariables(Map.of("message", "Credenciais inválidas!"));
            return thymeleafTemplateEngine.process("fragments/terminal-fail.html", thymeleafContext);
        }

        registrationService.saveUser(user);
        mailService.sendHtmlTemplateMail(user.getEmailDac(), "Bem-vindo!", "mail/welcome.html");

        // Login after verification:
        try {
            request.login(username, password);
        } catch (ServletException e) {
            logger.severe("COULDNT LOGIN : ServletException: " + e.getMessage());
            e.printStackTrace();
        }

        thymeleafContext.setVariables(Map.of("borrowedBooks", registrationService.getBorrowedBooks()));
        return thymeleafTemplateEngine.process("fragments/terminal-success.html", thymeleafContext);
    }

}
