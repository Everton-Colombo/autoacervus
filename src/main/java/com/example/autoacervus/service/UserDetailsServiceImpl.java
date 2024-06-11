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

/**
 * The UserDetailsService interface is used by Spring Security to retrieve user authentication credentials. Some
 * default implementations are provided by spring security. However, since our project structure doesn't match any of
 * the structures those implementations are pre-configured to expect, this custom implementation must be provided.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private Logger logger = Logger.getLogger(UserDetailsServiceImpl.class.getName());

    @Autowired
    private UserDAO userDAO;

    // When handling authentication, Spring Security manipulates a UserDetails object. By default, Spring is
    // configured to expect a very specific database schema from which to poll user credentials. However, this
    // project's database schema does not comply with such expectations, and thus this function provides a way to
    // map our current database and entity layout to a UserDetails object.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("[loadByUsername()]: retrieving user details by username \"" + username + "\"");

        User user = userDAO.findByEmailDac(username);
        if (user == null) {
            logger.warning("[loadByUsername()]: Username \"" + username + "\" not found!");
            throw new UsernameNotFoundException(username);  // Handled internally by spring security
        }

        // For now, there will only be a single user role, which all users have.
        Collection<? extends GrantedAuthority> authorities
                = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        logger.info("[loadByUsername()]: Returning user credentials");
        return new org.springframework.security.core.userdetails.User(user.getEmailDac(),
                user.getSbuPassword(), authorities);
    }
}
