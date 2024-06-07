package com.example.autoacervus.service;

import com.example.autoacervus.model.entity.User;

public interface RegistrationService {
    boolean verifyUser(User user);
    void saveUser(User user);
}
