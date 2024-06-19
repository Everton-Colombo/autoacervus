package com.example.autoacervus.controller;

import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.service.RegistrationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@Controller
public class RegistrationController {
    @Autowired
    RegistrationService registrationService;

    private final Logger logger = Logger.getLogger(RegistrationController.class.getName());

    @PostMapping("/register")
    public String register(HttpServletRequest request, @RequestParam String username, @RequestParam String password, Model model) {

        if (registrationService.userExists(username)) {
            logger.info("[register()]: User already exists. Attempting to authenticate...");

            // Attempt to manually login:
            try {
                request.login(username, password);
            } catch (ServletException e) {
                logger.severe("[register()]: COULDNT LOGIN : ServletException: " + e.getMessage());
                if (e.getMessage().equals("Bad credentials")) {
                    return "redirect:/?error";
                } else {
                    e.printStackTrace();
                }
            }

            return "redirect:/dashboard";
        }

        model.addAttribute("username", username);
        model.addAttribute("password", password);

        return "verify";
    }
}
