package com.example.autoacervus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
