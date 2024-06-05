package com.example.autoacervus.service;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.entity.User;
import com.example.autoacervus.proxy.AcervusProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    @Autowired
    UserDAO userDao;

    @Autowired
    AcervusProxy acervusProxy;

    @Override
    public boolean verifyUser(User user) {
        return acervusProxy.login(user);
    }

    @Override
    public void saveUser(User user) {
        userDao.updateUser(user);
    }
}
