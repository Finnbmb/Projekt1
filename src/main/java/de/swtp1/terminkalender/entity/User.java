package de.swtp1.terminkalender.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * JPA Entity für Benutzer
 * Vereinfachte Implementierung für Phase 2
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Benutzername ist erforderlich")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "E-Mail ist erforderlich")
    @Email(message = "Ungültige E-Mail-Adresse")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Name ist erforderlich")
    @Column(nullable = false)
    private String name;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "account_locked")
    private boolean accountLocked = false;

    @Column(name = "login_attempts")
    private int loginAttempts = 0;

    // Erweiterte Benutzerfelder
    @Column(name = "federal_state")
    private String federalState; // Für bundeslandspezifische Feiertage

    @Column(name = "timezone")
    private String timezone = "Europe/Berlin";

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role = UserRole.USER;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "default_reminder_minutes")
    private Integer defaultReminderMinutes = 15;

    @Column(name = "email_notifications")
    private boolean emailNotifications = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Konstruktoren
    public User() {
    }

    public User(String username, String email, String name) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.role = UserRole.USER;
        this.isActive = true;
        this.defaultReminderMinutes = 15;
        this.emailNotifications = true;
        this.timezone = "Europe/Berlin";
        this.accountLocked = false;
        this.loginAttempts = 0;
    }

    public User(String username, String email, String name, String passwordHash) {
        this(username, email, name);
        this.passwordHash = passwordHash;
    }

    // Enum für Benutzerrollen
    public enum UserRole {
        USER("Benutzer"),
        ADMIN("Administrator"),
        MODERATOR("Moderator");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // JPA Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Neue Getter und Setter
    public String getFederalState() {
        return federalState;
    }

    public void setFederalState(String federalState) {
        this.federalState = federalState;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getDefaultReminderMinutes() {
        return defaultReminderMinutes;
    }

    public void setDefaultReminderMinutes(Integer defaultReminderMinutes) {
        this.defaultReminderMinutes = defaultReminderMinutes;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
