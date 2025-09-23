package de.swtp1.terminkalender.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility-Klasse für Sicherheits-bezogene Operationen
 */
public class SecurityUtils {

    /**
     * Holt den Benutzernamen des aktuell angemeldeten Benutzers
     * @return Benutzername oder null wenn nicht angemeldet
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        System.out.println("DEBUG: SecurityUtils.getCurrentUsername() called");
        System.out.println("DEBUG: Authentication object: " + authentication);
        
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("DEBUG: Authentication is not null and authenticated");
            System.out.println("DEBUG: Principal: " + authentication.getPrincipal());
            System.out.println("DEBUG: Name: " + authentication.getName());
            
            // Bei anonymen Benutzern ist der Principal "anonymousUser"
            if (!"anonymousUser".equals(authentication.getPrincipal())) {
                String username = authentication.getName();
                System.out.println("DEBUG: Returning username: " + username);
                return username;
            } else {
                System.out.println("DEBUG: Principal is anonymousUser, returning null");
            }
        } else {
            System.out.println("DEBUG: Authentication is null or not authenticated");
        }
        
        return null;
    }

    /**
     * Prüft ob ein Benutzer angemeldet ist
     * @return true wenn angemeldet, false sonst
     */
    public static boolean isAuthenticated() {
        return getCurrentUsername() != null;
    }
}