package com.portfolio.manager.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecurityConfigTest {

    @Test
    void shouldCreateInMemoryAdminUser() {
        SecurityConfig securityConfig = new SecurityConfig();
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        UserDetailsService userDetailsService = securityConfig.userDetailsService(passwordEncoder);

        UserDetails user = userDetailsService.loadUserByUsername("admin");

        assertNotNull(user);
        assertTrue(passwordEncoder.matches("admin123", user.getPassword()));
    }
}
