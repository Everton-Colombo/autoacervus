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

@Controller
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("newUser", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(HttpServletRequest request, @RequestParam String username, @RequestParam String password, Model model) {
        User user = new User(username, password);
        boolean verified = registrationService.verifyUser(user);
        model.addAttribute("createdUser", user);
        model.addAttribute("status", verified ? "Sucesso!" : "Credenciais inválidas");

        if (verified) {
            registrationService.saveUser(user);

            try {
                request.login(username, password);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }

        return "verify";
    }
}
