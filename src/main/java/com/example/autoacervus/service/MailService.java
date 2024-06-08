package com.example.autoacervus.service;

import java.util.Map;

public interface MailService {
    void sendSimpleMail(String to, String subject, String content);

    /**
     * Sends HTML template email, whose attributes will be substituted with the values provided by the
     * {@code templateModel} map.
     *
     * @param to receiver's email
     * @param subject email's subject
     * @param template path to thymeleaf html template
     * @param templateModel map containing values that will be used to render the html.
     */
    void sendHtmlTemplateMail(String to, String subject, String template, Map<String, Object> templateModel);
}
