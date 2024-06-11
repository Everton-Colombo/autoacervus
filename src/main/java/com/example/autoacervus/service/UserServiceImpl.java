package com.example.autoacervus.service;

import com.example.autoacervus.dao.UserDAO;
import com.example.autoacervus.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements AutoacervusUserService, UserDetailsService {

    private Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Autowired
    private UserDAO userDAO;

    // From UserDetailsService interface. Used by spring security to do user authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // When handling authentication, Spring Security manipulates a UserDetails object. By default, Spring is
        // configured to expect a very specific database schema from which to poll user credentials. However, this
        // project's database schema does not comply with such expectations, and thus this function provides a way to
        // map our current database and entity layout to a UserDetails object.

        logger.info("[loadByUsername()]: retrieve user by username \"" + username + "\"");
        User user = userDAO.findByEmailDac(username);
        if (user == null) {
            logger.warning("Username \"" + username + "\" not found");
            throw new UsernameNotFoundException(username);
        }

        // For now, there will only be a single user role.
        Collection<? extends GrantedAuthority> authorities
                = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        logger.info("[loadByUsername()]: Returning user credentials");
        return new org.springframework.security.core.userdetails.User(user.getEmailDac(),
                user.getSbuPassword(), authorities);
    }
}
