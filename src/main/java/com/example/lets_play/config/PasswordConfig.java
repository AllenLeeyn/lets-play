package com.example.lets_play.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Provides the password encoder used for hashing user passwords before storage.
 * BCrypt is used so plain-text passwords are never stored. No setup required; the bean is injected
 * wherever encoding or matching of passwords is needed (e.g. signup, signin, user create/update).
 */
@Configuration
public class PasswordConfig {

    /** BCrypt encoder for one-way hashing and verification of passwords. */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
