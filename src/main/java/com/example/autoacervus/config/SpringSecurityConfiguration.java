package com.example.autoacervus.config;

import com.example.autoacervus.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                configurer
                        .requestMatchers("/resources/**").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/debugApi/**").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/verify").permitAll()
                        .requestMatchers("/dashboard").hasRole("USER")
            )
            .formLogin(form ->
                form
                        .loginPage("/").loginProcessingUrl("/authenticate").defaultSuccessUrl("/dashboard", true).permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserServiceImpl userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
