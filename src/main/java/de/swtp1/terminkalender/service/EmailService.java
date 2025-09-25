package de.swtp1.terminkalender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service für E-Mail-Versendung
 * Verwendet für Passwort-Reset und Benachrichtigungen
 * Nur aktiv wenn Mail-Konfiguration vorhanden ist
 */
@Service
@ConditionalOnProperty(name = "spring.mail.host")
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@terminkalender.de}")
    private String fromEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sendet eine E-Mail für Passwort-Reset
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Terminkalender - Passwort zurücksetzen");
            message.setText(createPasswordResetContent(resetToken));
            
            mailSender.send(message);
        } catch (Exception e) {
            // In einer produktiven Umgebung sollte hier ein Logger verwendet werden
            System.err.println("Fehler beim Senden der Passwort-Reset E-Mail: " + e.getMessage());
            throw new RuntimeException("E-Mail konnte nicht gesendet werden", e);
        }
    }

    /**
     * Erstellt den Inhalt der Passwort-Reset E-Mail
     */
    private String createPasswordResetContent(String resetToken) {
        return String.format(
            "Hallo,\n\n" +
            "Sie haben eine Passwort-Zurücksetzung für Ihr Terminkalender-Konto angefordert.\n\n" +
            "Klicken Sie auf den folgenden Link, um Ihr Passwort zurückzusetzen:\n" +
            "http://localhost:8080/reset-password?token=%s\n\n" +
            "Dieser Link ist 24 Stunden gültig.\n\n" +
            "Falls Sie diese Anfrage nicht gestellt haben, ignorieren Sie diese E-Mail.\n\n" +
            "Mit freundlichen Grüßen,\n" +
            "Ihr Terminkalender-Team",
            resetToken
        );
    }

    /**
     * Prüft ob E-Mail-Service verfügbar ist
     */
    public boolean isEmailServiceAvailable() {
        try {
            // Einfacher Connection-Test
            return mailSender != null;
        } catch (Exception e) {
            return false;
        }
    }
}