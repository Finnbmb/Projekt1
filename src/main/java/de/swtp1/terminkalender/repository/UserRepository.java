package de.swtp1.terminkalender.repository;

import de.swtp1.terminkalender.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository für User Entitäten
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Findet einen Benutzer anhand des Benutzernamens
     */
    Optional<User> findByUsername(String username);

    /**
     * Findet einen Benutzer anhand der E-Mail-Adresse
     */
    Optional<User> findByEmail(String email);

    /**
     * Prüft ob ein Benutzername bereits existiert
     */
    boolean existsByUsername(String username);

    /**
     * Prüft ob eine E-Mail bereits existiert
     */
    boolean existsByEmail(String email);
}
