package com.example.autoacervus.controller;

import com.example.autoacervus.model.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeAndInfoController {

    @GetMapping("/")
    public String index(Model model) {
        // Done like this instead of adding a User object to the model so that the form for registering can be the same
        // form for logging in
        model.addAttribute("username", "");
        model.addAttribute("password", "");

        return "index";
    }
}
