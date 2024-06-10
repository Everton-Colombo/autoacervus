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

@Service
public class UserServiceImpl implements AutoacervusUserService, UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    // From UserDetailsService interface. Used by spring security to do user authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Spring security expects a UserDetails object, with which it handles authentication. By default, Spring is
        // configured to expect a very specific database schema from which to poll user credentials. However, this
        // project's database schema does not comply with such expectations, and thus this function provides a way to
        // map our current database and entity layout to a UserDetails object.

        User user = userDAO.findByEmailDac(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        // For now, there will only be a single user role.
        Collection<? extends GrantedAuthority> authorities
                = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(user.getEmailDac(), user.getSbuPassword(),
                authorities);
    }
}
