package com.example.autoacervus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class EmailServiceImpl implements EmailService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        logger.info("[sendSimpleMail()] subject: \"%s\"; to: %s]".formatted(subject, to));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.fromMail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            emailSender.send(message);
        } catch (MailException e) {
            logger.severe("[sendSimpleMail()] Failed to send simple mail. Error msg: " + e.getMessage());
        }
    }

    @Override
    public void sendHtmlMail(String to, String subject, String content) {

    }

    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {

    }

    @Override
    public void sendHtmlThymeleafMail(String to, String subject, String content, String filePath) {

    }
}
