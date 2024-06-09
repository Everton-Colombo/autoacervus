package com.example.autoacervus.controller;

import com.example.autoacervus.model.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeAndInfoController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("newUser", new User());
        return "index";
    }
}
