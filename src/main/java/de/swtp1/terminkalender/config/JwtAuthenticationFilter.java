package de.swtp1.terminkalender.config;

import de.swtp1.terminkalender.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * Überprüft JWT-Tokens in HTTP-Requests und setzt den Security-Context
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    @Lazy
    private AuthService authService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/database/") ||  // Database Controller ausschließen
               path.startsWith("/debug-api/") ||  // Debug API ausschließen
               path.startsWith("/api/v1/auth/") ||
               path.endsWith("/login.html") ||
               path.endsWith("/index.html") ||
               path.endsWith("/debug-interface.html") ||
               path.endsWith("/test-interface.html") ||
               path.startsWith("/static/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.equals("/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Wenn kein Authorization Header vorhanden ist
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        
        try {
            username = authService.extractUsername(jwt);

            // Wenn Token gültig ist und noch kein SecurityContext gesetzt ist
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                if (authService.isTokenValid(jwt)) {
                    // Token ist gültig - erstelle Authentication
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            username, 
                            null, 
                            null // Hier könnten Authorities gesetzt werden
                        );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token ist ungültig - ignorieren und weiterleiten
            logger.debug("JWT Token validation failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}