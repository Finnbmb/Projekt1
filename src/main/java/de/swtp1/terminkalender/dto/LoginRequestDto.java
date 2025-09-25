package de.swtp1.terminkalender.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO für Login-Anfragen
 */
public class LoginRequestDto {

    @NotBlank(message = "E-Mail oder Username ist erforderlich")
    private String email; // Kann sowohl E-Mail als auch Username sein

    @NotBlank(message = "Passwort ist erforderlich")
    @Size(min = 6, message = "Passwort muss mindestens 6 Zeichen lang sein")
    private String password;

    // Konstruktoren
    public LoginRequestDto() {
    }

    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter und Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequestDto{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}
