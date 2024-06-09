package com.example.autoacervus.service;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.proxy.AcervusProxy;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private Logger logger = Logger.getLogger(RegistrationServiceImpl.class.getName());

    @Autowired
    UserDAO userDao;

    @Autowired
    AcervusProxy acervusProxy;

    @Override
    public boolean verifyUser(User user) {
        try {
            return acervusProxy.login(user);
        } catch (Exception e) {
            this.logger.severe("Could not verify user. Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void saveUser(User user) {
        this.logger.info("Saving user " + user);
        userDao.updateUser(user);
    }
}
