package de.swtp1.terminkalender.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Separate Security-Konfiguration für H2-Console
 * Höchste Priorität - wird vor der Haupt-SecurityConfig angewendet
 */
@Configuration
@EnableWebSecurity
@Order(1) // Höchste Priorität
public class H2SecurityConfig {

    /**
     * Separate Security-Chain nur für H2-Console
     * Komplett ohne Sicherheit für lokale Entwicklung
     */
    @Bean
    @Order(1)
    public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/h2-console/**")
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .build();
    }
}