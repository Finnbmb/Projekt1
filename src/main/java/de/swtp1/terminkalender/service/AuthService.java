package de.swtp1.terminkalender.service;

import de.swtp1.terminkalender.dto.AuthResponseDto;
import de.swtp1.terminkalender.dto.LoginRequestDto;
import de.swtp1.terminkalender.dto.RegisterRequestDto;
import de.swtp1.terminkalender.entity.User;
import de.swtp1.terminkalender.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * Service für Authentifizierung und Benutzerverwaltung
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private EmailService emailService;

    // JWT Secret - in Produktion aus Environment Variable laden!
    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final int jwtExpirationMs = 86400000; // 24 Stunden

    /**
     * Benutzer-Login
     */
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        // Benutzer suchen - erst nach Email, dann nach Username
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            // Falls Email nicht gefunden, versuche Username
            userOpt = userRepository.findByUsername(loginRequest.getEmail());
        }
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Ungültige E-Mail/Username oder Passwort");
        }

        User user = userOpt.get();

        // Account-Status prüfen
        if (!user.isActive()) {
            throw new RuntimeException("Konto ist deaktiviert");
        }

        if (user.isAccountLocked()) {
            throw new RuntimeException("Konto ist gesperrt");
        }

        // Passwort prüfen
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            // Login-Versuche erhöhen
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            if (user.getLoginAttempts() >= 5) {
                user.setAccountLocked(true);
            }
            userRepository.save(user);
            throw new RuntimeException("Ungültige E-Mail oder Passwort");
        }

        // Erfolgreicher Login
        user.setLastLogin(LocalDateTime.now());
        user.setLoginAttempts(0);
        userRepository.save(user);

        // JWT Token generieren
        String token = generateToken(user);

        // Response DTO erstellen
        AuthResponseDto.UserDto userDto = new AuthResponseDto.UserDto(user);
        return new AuthResponseDto(token, userDto, "Login erfolgreich");
    }

    /**
     * Benutzer-Registrierung
     */
    public AuthResponseDto register(RegisterRequestDto registerRequest) {
        // Prüfe ob E-Mail bereits existiert
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("E-Mail-Adresse bereits registriert");
        }

        // Prüfe ob Benutzername bereits existiert
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Benutzername bereits vergeben");
        }

        // Neuen Benutzer erstellen
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getName()
        );
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFederalState(registerRequest.getFederalState());
        user.setRole(User.UserRole.USER);
        user.setActive(true);

        user = userRepository.save(user);

        // JWT Token generieren
        String token = generateToken(user);

        // Response DTO erstellen
        AuthResponseDto.UserDto userDto = new AuthResponseDto.UserDto(user);
        return new AuthResponseDto(token, userDto, "Registrierung erfolgreich");
    }

    /**
     * JWT Token generieren
     */
    private String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecret)
                .compact();
    }

    /**
     * Token validieren
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Benutzer aus Token extrahieren
     */
    public AuthResponseDto.UserDto getCurrentUser(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isPresent()) {
                return new AuthResponseDto.UserDto(userOpt.get());
            } else {
                throw new RuntimeException("Benutzer nicht gefunden");
            }
        } catch (Exception e) {
            throw new RuntimeException("Ungültiger Token");
        }
    }

    /**
     * E-Mail aus Token extrahieren
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    
    /**
     * Benutzername aus Token extrahieren (für JWT Filter)
     */
    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("username", String.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Token gültig prüfen (für JWT Filter)
     */
    public boolean isTokenValid(String token) {
        return validateToken(token);
    }

    /**
     * Token validieren und Claims zurückgeben
     */
    private Claims validateTokenAndGetClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Spezieller Token-Generator für Password Reset mit angepasster Gültigkeit
     */
    private String generateResetToken(Long userId, String email, int expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("type", "password_reset")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecret)
                .compact();
    }

    /**
     * E-Mail für Passwort-Reset senden
     */
    @Transactional
    public void sendPasswordResetEmail(String email) {
        System.out.println("DEBUG: sendPasswordResetEmail called for: " + email);
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            System.out.println("DEBUG: User not found for email: " + email);
            // Aus Sicherheitsgründen keine Fehlermeldung, wenn E-Mail nicht existiert
            return;
        }

        User user = userOpt.get();
        System.out.println("DEBUG: User found: " + user.getUsername() + " (ID: " + user.getId() + ")");
        
        // Reset-Token generieren (60 Minuten gültig)
        String resetToken = generateResetToken(user.getId(), user.getEmail(), 3600000); // 1 Stunde
        System.out.println("DEBUG: Generated reset token (length): " + resetToken.length());
        
        // Reset-Token und Ablaufzeit in der Datenbank speichern
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        User savedUser = userRepository.save(user);
        System.out.println("DEBUG: Token saved to database. HasToken: " + (savedUser.getPasswordResetToken() != null));

        // E-Mail senden in separater Transaktion (darf DB-Speicherung nicht beeinflussen)
        try {
            if (emailService != null) {
                System.out.println("DEBUG: EmailService available, sending email...");
                emailService.sendPasswordResetEmail(email, resetToken);
                System.out.println("DEBUG: Email sent successfully");
            } else {
                System.out.println("DEBUG: EmailService not available (development mode)");
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Email sending failed, but token was saved: " + e.getMessage());
            // Token bleibt gespeichert, auch wenn E-Mail fehlschlägt
        }
    }

    /**
     * Passwort mit Reset-Token zurücksetzen
     */
    public void resetPassword(String resetToken, String newPassword) {
        // Token validieren
        Claims claims = validateTokenAndGetClaims(resetToken);
        if (claims == null) {
            throw new RuntimeException("Ungültiger Reset-Token");
        }

        Long userId = claims.get("userId", Long.class);
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Benutzer nicht gefunden");
        }

        User user = userOpt.get();

        // Prüfen ob der Token mit dem in der DB gespeicherten übereinstimmt
        if (!resetToken.equals(user.getPasswordResetToken())) {
            throw new RuntimeException("Ungültiger Reset-Token");
        }

        // Prüfen ob Token noch gültig ist
        if (user.getPasswordResetTokenExpiry() == null || 
            user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset-Token ist abgelaufen");
        }

        // Passwort aktualisieren
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        user.setLastPasswordChange(LocalDateTime.now());
        
        userRepository.save(user);
    }

    /**
     * Benutzer löschen
     */
    public void deleteUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Benutzer nicht gefunden");
        }

        User user = userOpt.get();
        
        // TODO: Hier könnten weitere Prüfungen hinzugefügt werden:
        // - Prüfen ob es der letzte Admin ist
        // - Termine des Benutzers behandeln (löschen oder übertragen)
        
        userRepository.delete(user);
    }

    /**
     * Alle Benutzer auflisten
     */
    public java.util.List<AuthResponseDto.UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new AuthResponseDto.UserDto(user))
                .collect(java.util.stream.Collectors.toList());
    }
}
