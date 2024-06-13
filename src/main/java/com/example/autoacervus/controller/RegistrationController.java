package com.example.autoacervus.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {
    @PostMapping("/register")
    public String register(HttpServletRequest request, @RequestParam String username, @RequestParam String password,
            Model model) {
        model.addAttribute("username", username);
        model.addAttribute("password", password);

        return "verify";
    }
}
