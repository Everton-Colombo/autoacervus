package com.example.autoacervus.rest;

import com.example.autoacervus.service.EmailService;
import com.example.autoacervus.service.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debugApi")
public class DebugApiController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendMail")
    public String sendMail() {
//        emailService.sendSimpleMail("e.rcolombo2@gmail.com", "Teste", "aoba");
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("message", "Hello World");
        emailService.sendHtmlTemplateMail("e.rcolombo2@gmail.com", "Teste", "mail/test-template.html", templateModel);

        return "done";
    }
}
