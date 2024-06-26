package com.example.autoacervus.service;

import com.example.autoacervus.model.entity.User;

import java.security.Principal;

public interface DashboardService {
    User getLoggedInUser(Principal principal);

    void saveUser(User user);

    void deleteUser(User user);
}
