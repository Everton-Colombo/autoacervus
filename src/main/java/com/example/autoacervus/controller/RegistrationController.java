package com.example.autoacervus.controller;

import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String register(@ModelAttribute User user, Model model) {
        boolean verified = registrationService.verifyUser(user);
        model.addAttribute("createdUser", user);
        model.addAttribute("status", verified ? "Sucesso!" : "Credenciais inválidas");

        if (verified) {
            registrationService.saveUser(user);
        }

        return "verify";
    }
}
