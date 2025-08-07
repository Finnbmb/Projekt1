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

    // JWT Secret - in Produktion aus Environment Variable laden!
    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final int jwtExpirationMs = 86400000; // 24 Stunden

    /**
     * Benutzer-Login
     */
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        // Benutzer suchen
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Ungültige E-Mail oder Passwort");
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
}
