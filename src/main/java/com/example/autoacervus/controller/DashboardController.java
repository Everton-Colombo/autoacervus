package com.example.autoacervus.controller;

import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.model.entity.UserSettings;
import com.example.autoacervus.service.DashboardService;
import com.example.autoacervus.service.MailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private MailService mailService;

    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Logger logger = Logger.getLogger(DashboardController.class.getName());

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User loggedInUser = dashboardService.getLoggedInUser(principal);

        model.addAttribute("loggedUsername", principal.getName());
        model.addAttribute("renewedBookCount", loggedInUser.getUserStats().getRenewalCount());
        model.addAttribute("signupDate", loggedInUser.getUserStats().getSignupDate().format(dtFormatter));

        model.addAttribute("userSettings", loggedInUser.getSettings());

        model.addAttribute("newUserSettings", new UserSettings());

        return "dashboard";
    }

    @DeleteMapping("/user")
    public String deleteUser(HttpServletRequest request, Principal principal) {
        logger.info("[deleteUser()] Deleting user: " + principal.getName());

        try {
            request.logout();
        } catch (ServletException e) {
            logger.severe("[deleteUser()] ServletException while logging out user: " + e.getMessage());
        }

        User toDelete = dashboardService.getLoggedInUser(principal);
        dashboardService.deleteUser(toDelete);

        mailService.sendHtmlTemplateMail(principal.getName(), "Conta removida", "mail/goodbye.html");

        return "redirect:/";
    }

    @PutMapping("/userSettings")
    public String updateSettings(Principal principal, @ModelAttribute UserSettings userSettings) {
        logger.info("[updateSettings()] Updating user settings: " + userSettings);

        User loggedInUser = dashboardService.getLoggedInUser(principal);
        userSettings.setUser(loggedInUser);
        loggedInUser.setSettings(userSettings);
        dashboardService.saveUser(loggedInUser);

        return "redirect:/dashboard";
    }
}
