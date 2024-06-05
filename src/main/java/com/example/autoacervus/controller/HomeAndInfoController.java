package com.example.autoacervus.controller;

import com.example.autoacervus.model.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeAndInfoController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
