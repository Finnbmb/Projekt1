package de.swtp1.terminkalender.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security Konfiguration
 * Schützt API-Endpunkte mit JWT-Authentifizierung
 */
@Configuration
@EnableWebSecurity
@Order(2) // Niedrigere Priorität als H2SecurityConfig
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Actuator Endpoints für Monitoring
                .requestMatchers("/actuator/**").permitAll()
                // Database Controller für direkten DB-Zugriff
                .requestMatchers("/database/**").permitAll()
                // Debug API für Testing ohne Auth
                .requestMatchers("/debug-api/**").permitAll()
                // Auth Endpunkte
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Holiday API für öffentlichen Zugriff
                .requestMatchers("/api/v1/holidays/**").permitAll()
                // Statische Ressourcen
                .requestMatchers("/login.html", "/index.html").permitAll()
                .requestMatchers("/debug-interface.html", "/test-interface.html").permitAll()
                .requestMatchers("/static/**", "/css/**", "/js/**", "/").permitAll()
                .requestMatchers("/*.html").permitAll()  // Alle HTML-Dateien
                
                // Alle anderen Endpunkte erfordern Authentifizierung
                .anyRequest().authenticated()
            )
            // JWT Filter vor Username/Password Filter hinzufügen
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
