package com.student.app.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * WebSecurityConfig:
 * ------------------
 * Central configuration class for:
 * 1. User authentication (in-memory users).
 * 2. HTTP security rules (authorization, CSRF, login).
 * 3. Password encoding strategy.
 * 4. Multipart file upload limits.
 *
 * Annotations:
 * - @Configuration → Marks this class as a source of bean definitions.
 * - @EnableWebSecurity → Enables Spring Security’s web security support.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final Logger logger = LogManager.getLogger(WebSecurityConfig.class);

    /**
     * Defines in-memory users for authentication.
     * - "aishwarya" with role USER
     * - "admin" with role ADMIN
     * 
     * InMemoryUserDetailsManager stores these users in memory.
     * Useful for demos and testing; production apps should use a database or external identity provider.
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        logger.info("Initializing in-memory users for authentication");

        UserDetails user = User.withUsername("user")
                .password("user")
                .roles("USER")
                .build();
        logger.info("Created in-memory user: aishwarya with role USER");

        UserDetails admin = User.withUsername("admin")
                .password("admin")
                .roles("ADMIN")
                .build();
        logger.info("Created in-memory user: admin with role ADMIN");

        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * Configures HTTP security rules.
     * - CSRF disabled (for simplicity; not recommended in production).
     * - Authorization rules:
     *   → "/students/upload" and "/login" are open to all.
     *   → "/students/**" requires USER or ADMIN role.
     *   → Any other request requires authentication.
     * - httpBasic() enables basic authentication (username/password in HTTP headers).
     * - http.build() finalizes the SecurityFilterChain used at startup.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { 
        logger.info("Configuring HTTP security rules");
        http.csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers("/students/upload", "/login").permitAll()
                .requestMatchers("/students/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            .and()
            .httpBasic();
        logger.debug("HTTP security rules configured successfully");
        return http.build();
    }

    /**
     * Defines password encoding strategy.
     * - NoOpPasswordEncoder stores passwords in plain text.
     * - Only for demo purposes; in production use BCryptPasswordEncoder or another secure encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.warn("Using NoOpPasswordEncoder (not secure, demo purposes only)");
        return NoOpPasswordEncoder.getInstance();
    }
    
    /**
     * Configures multipart file upload limits.
     * - Max file size: 10 MB
     * - Max request size: 10 MB
     * MultipartConfigFactory builds the configuration, then createMultipartConfig() finalizes it.
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        logger.info("Configuring multipart file upload limits: Max 10 MB");
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));
        logger.debug("MultipartConfigElement created with 10 MB limits");
        return factory.createMultipartConfig();
    }
}
