package com.example.autoacervus.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class MailServiceImpl implements MailService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    @Async
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

    // TODO: make EmailService an abstract class?
    private void sendHtmlMail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }

    /**
     * {@inheritDoc}
     *
     * This implementation uses Thymeleaf to render the HTML template.
     *
     * @param to receiver's email
     * @param subject email's subject
     * @param template path to thymeleaf html template
     * @param templateModel map containing values that will be used to render the html.
     */
    @Override
    @Async
    public void sendHtmlTemplateMail(String to, String subject, String template, Map<String, Object> templateModel) {
        logger.info("[sendHtmlTemplateMail()] subject: \"%s\"; template: \"%s\"; to: %s]".formatted(subject, template, to));

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process(template, thymeleafContext);

        try {
            sendHtmlMail(to, subject, htmlBody);
        } catch (MessagingException e) {
            logger.severe("[sendHtmlTemplateMail()] Failed to send html mail. Error msg: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlTemplateMail(String to, String subject, String template) {
        this.sendHtmlTemplateMail(to, subject, template, new HashMap<>());
    }
}
