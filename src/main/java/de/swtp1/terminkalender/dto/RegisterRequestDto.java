package de.swtp1.terminkalender.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO für Registrierungs-Anfragen
 */
public class RegisterRequestDto {

    @NotBlank(message = "Benutzername ist erforderlich")
    @Size(min = 3, max = 50, message = "Benutzername muss zwischen 3 und 50 Zeichen lang sein")
    private String username;

    @NotBlank(message = "E-Mail ist erforderlich")
    @Email(message = "Ungültige E-Mail-Adresse")
    private String email;

    @NotBlank(message = "Name ist erforderlich")
    @Size(min = 2, max = 100, message = "Name muss zwischen 2 und 100 Zeichen lang sein")
    private String name;

    @NotBlank(message = "Passwort ist erforderlich")
    @Size(min = 6, max = 100, message = "Passwort muss zwischen 6 und 100 Zeichen lang sein")
    private String password;

    @NotBlank(message = "Passwort-Bestätigung ist erforderlich")
    private String confirmPassword;

    private String federalState;

    // Konstruktoren
    public RegisterRequestDto() {
    }

    public RegisterRequestDto(String username, String email, String name, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    // Getter und Setter
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFederalState() {
        return federalState;
    }

    public void setFederalState(String federalState) {
        this.federalState = federalState;
    }

    /**
     * Validiert ob die Passwörter übereinstimmen
     */
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }

    @Override
    public String toString() {
        return "RegisterRequestDto{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='[PROTECTED]'" +
                ", confirmPassword='[PROTECTED]'" +
                '}';
    }
}
