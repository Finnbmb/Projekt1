package de.swtp1.terminkalender.config;

import de.swtp1.terminkalender.entity.User;
import de.swtp1.terminkalender.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Produktions-Dateninitialisierung
 * Migriert Daten von H2 zu Azure MySQL falls nötig
 */
@Configuration
@Profile("prod")
public class ProductionDataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductionDataInitializer.class);

    @Bean
    public CommandLineRunner initProductionData(UserRepository userRepository, 
                                               PasswordEncoder passwordEncoder) {
        return args -> {
            
            // Prüfe ob bereits Daten vorhanden sind
            long userCount = userRepository.count();
            if (userCount > 0) {
                logger.info("✅ Produktionsdatenbank bereits initialisiert - {} Benutzer vorhanden", userCount);
                return;
            }
            
            logger.info("🔄 Erstmalige Produktionsdatenbank-Initialisierung...");
            
            // Erstelle Basis-Admin-User für Produktion falls keine Daten da sind
            User adminUser = new User("admin", "admin@example.com", "System Administrator");
            adminUser.setPasswordHash(passwordEncoder.encode("admin123"));
            adminUser.setFederalState("DE");
            adminUser.setRole(User.UserRole.ADMIN);
            adminUser.setActive(true);  // 🔧 ADMIN AKTIV SETZEN!
            adminUser.setDefaultReminderMinutes(30);
            adminUser.setEmailNotifications(true);
            userRepository.save(adminUser);
            
            logger.info("✅ Produktionsdaten erfolgreich initialisiert");
            logger.info("   - Admin-Benutzer erstellt: admin@example.com");
            logger.info("   - Standard-Passwort: admin123 (Bitte nach erstem Login ändern!)");
        };
    }
}