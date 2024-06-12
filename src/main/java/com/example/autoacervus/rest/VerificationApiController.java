package com.example.autoacervus.rest;

import com.example.autoacervus.model.entity.User;
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
    private SpringTemplateEngine thymeleafTemplateEngine;

    private Logger logger = Logger.getLogger(VerificationApiController.class.getName());

    @PostMapping("/verify")
    public String verify(HttpServletRequest request, @RequestParam String username, @RequestParam String password) {
        logger.info("Very username " + username + ": " + password);

        User user = new User(username, password);
        boolean verified = registrationService.verifyUser(user);

        String renderedFragment = "";
        Context thymeleafContext = new Context();

        if (verified) {
            registrationService.saveUser(user);

            // Login after verification:
            try {
                request.login(username, password);
            } catch (ServletException e) {
                logger.severe("COULDNT LOGIN : ServletException: " + e.getMessage());
                e.printStackTrace();
            }

            thymeleafContext.setVariables(Map.of("borrowedBooks", registrationService.getBorrowedBooks()));
            renderedFragment = thymeleafTemplateEngine.process("fragments/terminal-success.html", thymeleafContext);
        }
        else {
            thymeleafContext.setVariables(Map.of("message", "Credenciais inválidas!"));
            renderedFragment = thymeleafTemplateEngine.process("fragments/terminal-fail.html", thymeleafContext);
        }

        return renderedFragment;
    }

}
