package com.abtm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security Configuration for Spring Security
 * 
 * Currently configured to allow all requests for development.
 * In production, implement JWT-based authentication.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf().disable()
            
            // Configure CORS
            .cors().and()
            
            // Configure session management - stateless for REST API
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            
            // Configure authorization
            .authorizeHttpRequests(authz -> authz
                // Public endpoints (no authentication required)
                .requestMatchers(
                        // Quick scenario analysis
                        // User registration
                        // User login
                        // View modules
                        // View module details
                ).permitAll()
                
                // All other endpoints require authentication
                // TODO: Implement JWT authentication
                .anyRequest().permitAll()  // Change to .authenticated() after implementing JWT
            );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow requests from frontend (adjust for production)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",     // React development server
            "http://localhost:4200",     // Angular development server
            "http://localhost:8081"      // Alternative frontend port
        ));
        
        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Expose authorization header
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        // Max age for preflight requests (24 hours)
        configuration.setMaxAge(86400L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
