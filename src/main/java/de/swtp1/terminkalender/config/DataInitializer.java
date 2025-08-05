package de.swtp1.terminkalender.config;

import de.swtp1.terminkalender.entity.Appointment;
import de.swtp1.terminkalender.entity.User;
import de.swtp1.terminkalender.repository.AppointmentRepository;
import de.swtp1.terminkalender.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Datenbank-Initialisierung mit Testdaten
 * Für Entwicklung und Testing
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, 
                                    AppointmentRepository appointmentRepository) {
        return args -> {
            // Test-Benutzer erstellen
            User testUser = new User("testuser", "test@example.com", "Test Benutzer");
            testUser = userRepository.save(testUser);
            
            User adminUser = new User("admin", "admin@example.com", "Admin Benutzer");
            adminUser = userRepository.save(adminUser);

            // Test-Termine erstellen
            LocalDateTime now = LocalDateTime.now();
            
            // Heutiger Termin
            Appointment meeting1 = new Appointment(
                "Team Meeting",
                "Wöchentliches Team-Sync Meeting",
                now.plusHours(2),
                now.plusHours(3),
                "Konferenzraum A",
                testUser.getId()
            );
            appointmentRepository.save(meeting1);

            // Morgen Termin
            Appointment meeting2 = new Appointment(
                "Kundenpräsentation",
                "Präsentation der neuen Features für Kunde XYZ",
                now.plusDays(1).withHour(14).withMinute(0),
                now.plusDays(1).withHour(15).withMinute(30),
                "Online - Teams",
                testUser.getId()
            );
            appointmentRepository.save(meeting2);

            // Nächste Woche Termin
            Appointment meeting3 = new Appointment(
                "Code Review",
                "Review der Sprint-Ergebnisse",
                now.plusWeeks(1).withHour(10).withMinute(0),
                now.plusWeeks(1).withHour(11).withMinute(0),
                "Entwicklerbereich",
                adminUser.getId()
            );
            appointmentRepository.save(meeting3);

            // Workshop
            Appointment workshop = new Appointment(
                "Spring Boot Workshop",
                "Hands-on Workshop zu Spring Boot Best Practices",
                now.plusDays(3).withHour(9).withMinute(0),
                now.plusDays(3).withHour(17).withMinute(0),
                "Schulungsraum 1",
                testUser.getId()
            );
            appointmentRepository.save(workshop);

            System.out.println("✅ Testdaten erfolgreich geladen:");
            System.out.println("   - " + userRepository.count() + " Benutzer erstellt");
            System.out.println("   - " + appointmentRepository.count() + " Termine erstellt");
        };
    }
}
