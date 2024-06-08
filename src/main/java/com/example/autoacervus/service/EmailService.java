package com.example.autoacervus.service;

public interface EmailService {
    void sendSimpleMail(String to, String subject, String content);
    void sendHtmlMail(String to, String subject, String content);
    void sendAttachmentsMail(String to, String subject, String content, String filePath);
    void sendHtmlThymeleafMail(String to, String subject, String content, String filePath);
}
