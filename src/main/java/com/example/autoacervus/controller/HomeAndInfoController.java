package com.example.autoacervus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeAndInfoController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
