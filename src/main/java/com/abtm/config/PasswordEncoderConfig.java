package com.abtm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration for Password Encoding
 * Uses BCrypt hashing algorithm for secure password storage
 */
@Configuration
public class PasswordEncoderConfig {
    
    /**
     * BCrypt password encoder bean
     * Strength: 10 (default) - good balance between security and performance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
