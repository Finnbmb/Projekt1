package de.swtp1.terminkalender.config;

import de.swtp1.terminkalender.entity.Appointment;
import de.swtp1.terminkalender.entity.User;
import de.swtp1.terminkalender.repository.AppointmentRepository;
import de.swtp1.terminkalender.repository.UserRepository;
import de.swtp1.terminkalender.service.HolidayService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.Year;

/**
 * Datenbank-Initialisierung mit Testdaten
 * Für Entwicklung und Testing
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, 
                                    AppointmentRepository appointmentRepository,
                                    HolidayService holidayService) {
        return args -> {
            // Initialisiere Feiertage für aktuelles Jahr
            int currentYear = Year.now().getValue();
            holidayService.initializeGermanHolidays(currentYear);
            
            // Test-Benutzer erstellen mit erweiterten Feldern
            User testUser = new User("testuser", "test@example.com", "Test Benutzer");
            testUser.setFederalState("NW"); // Nordrhein-Westfalen
            testUser.setDefaultReminderMinutes(15);
            testUser.setEmailNotifications(true);
            testUser = userRepository.save(testUser);
            
            User adminUser = new User("admin", "admin@example.com", "Admin Benutzer");
            adminUser.setFederalState("BY"); // Bayern
            adminUser.setRole(User.UserRole.ADMIN);
            adminUser.setDefaultReminderMinutes(30);
            adminUser = userRepository.save(adminUser);

            // Test-Termine mit erweiterten Feldern erstellen
            LocalDateTime now = LocalDateTime.now();
            
            // Heutiger Termin - Hohe Priorität
            Appointment meeting1 = new Appointment(
                "Team Meeting",
                "Wöchentliches Team-Sync Meeting",
                now.plusHours(2),
                now.plusHours(3),
                "Konferenzraum A",
                testUser.getId()
            );
            meeting1.setPriority(Appointment.Priority.HIGH);
            meeting1.setCategory("Meeting");
            meeting1.setColorCode("#ff6b6b");
            meeting1.setReminderMinutes(15);
            appointmentRepository.save(meeting1);

            // Morgen Termin - Dringend
            Appointment meeting2 = new Appointment(
                "Kundenpräsentation",
                "Präsentation der neuen Features für Kunde XYZ",
                now.plusDays(1).withHour(14).withMinute(0),
                now.plusDays(1).withHour(15).withMinute(30),
                "Online - Teams",
                testUser.getId()
            );
            meeting2.setPriority(Appointment.Priority.URGENT);
            meeting2.setCategory("Kunde");
            meeting2.setColorCode("#4ecdc4");
            meeting2.setReminderMinutes(30);
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
            meeting3.setPriority(Appointment.Priority.MEDIUM);
            meeting3.setCategory("Entwicklung");
            meeting3.setColorCode("#45b7d1");
            meeting3.setReminderMinutes(10);
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
            workshop.setPriority(Appointment.Priority.LOW);
            workshop.setCategory("Weiterbildung");
            workshop.setColorCode("#96ceb4");
            workshop.setReminderMinutes(60);
            appointmentRepository.save(workshop);

            System.out.println("✅ Testdaten erfolgreich geladen:");
            System.out.println("   - " + userRepository.count() + " Benutzer erstellt");
            System.out.println("   - " + appointmentRepository.count() + " Termine erstellt");
        };
    }
}
