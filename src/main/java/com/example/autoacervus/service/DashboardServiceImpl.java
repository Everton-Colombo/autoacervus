package com.example.autoacervus.service;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class DashboardServiceImpl implements DashboardService{

    @Autowired
    private UserDAO userDAO;

    @Override
    public User getLoggedInUser(Principal principal) {
        return userDAO.findByEmailDac(principal.getName());
    }

    @Override
    public void saveUser(User user) {
        userDAO.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userDAO.delete(user);
    }
}
