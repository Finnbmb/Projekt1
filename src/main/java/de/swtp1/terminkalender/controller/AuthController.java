package de.swtp1.terminkalender.controller;

import de.swtp1.terminkalender.dto.AuthResponseDto;
import de.swtp1.terminkalender.dto.LoginRequestDto;
import de.swtp1.terminkalender.dto.RegisterRequestDto;
import de.swtp1.terminkalender.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller für Authentifizierung
 * Handles Login, Registration und Token-Validierung
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Benutzer-Login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            AuthResponseDto response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Login fehlgeschlagen: " + e.getMessage()));
        }
    }

    /**
     * Benutzer-Registrierung
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        try {
            // Validiere Passwort-Bestätigung
            if (!registerRequest.isPasswordConfirmed()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Passwörter stimmen nicht überein"));
            }

            AuthResponseDto response = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Registrierung fehlgeschlagen: " + e.getMessage()));
        }
    }

    /**
     * Token validieren
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            // Entferne "Bearer " prefix
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            boolean isValid = authService.validateToken(token);
            if (isValid) {
                return ResponseEntity.ok(new MessageResponse("Token ist gültig"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Token ist ungültig"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token-Validierung fehlgeschlagen"));
        }
    }

    /**
     * Logout (Client-seitig, da JWT stateless ist)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(new MessageResponse("Erfolgreich abgemeldet"));
    }

    /**
     * Aktueller Benutzer
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            AuthResponseDto.UserDto user = authService.getCurrentUser(token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Benutzer nicht gefunden"));
        }
    }

    /**
     * Passwort vergessen - E-Mail mit Reset-Link senden
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.sendPasswordResetEmail(request.getEmail());
            return ResponseEntity.ok(new MessageResponse("Falls ein Konto mit dieser E-Mail-Adresse existiert, wurde eine E-Mail mit weiteren Anweisungen gesendet."));
        } catch (Exception e) {
            // Aus Sicherheitsgründen immer die gleiche Nachricht zurückgeben
            return ResponseEntity.ok(new MessageResponse("Falls ein Konto mit dieser E-Mail-Adresse existiert, wurde eine E-Mail mit weiteren Anweisungen gesendet."));
        }
    }

    /**
     * Passwort zurücksetzen mit Token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new MessageResponse("Passwort wurde erfolgreich zurückgesetzt."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Ungültiger oder abgelaufener Reset-Token."));
        }
    }

    // Helper Classes
    public static class ErrorResponse {
        private String message;
        private long timestamp = System.currentTimeMillis();

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class MessageResponse {
        private String message;
        private long timestamp = System.currentTimeMillis();

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class ForgotPasswordRequest {
        private String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
