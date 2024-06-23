package com.example.autoacervus.config;

import com.example.autoacervus.encryption.AES256PasswordEncoder;
import com.example.autoacervus.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/resources/**").permitAll()
                .requestMatchers("/static/**").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/debugApi/**").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/verify").permitAll()
                .requestMatchers("/dashboard").hasRole("USER")
                .requestMatchers("/user").hasRole("USER")
                .requestMatchers("/userSettings").hasRole("USER")
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/verify").permitAll())
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/authenticate")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll())
                .logout(logout -> logout.permitAll())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(AES256PasswordEncoder.getInstance());

        return authProvider;
    }
}
