package com.example.autoacervus.rest;

import com.example.autoacervus.service.EmailService;
import com.example.autoacervus.service.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debugApi")
public class DebugApiController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendMail")
    public String sendMail() {
        emailService.sendSimpleMail("e.rcolombo2@gmail.com", "Teste", "aoba");

        return "done";
    }
}
