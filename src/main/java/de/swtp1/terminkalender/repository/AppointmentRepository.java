package de.swtp1.terminkalender.repository;

import de.swtp1.terminkalender.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository für Appointment Entitäten
 * Implementiert CRUD-Operationen und erweiterte Suchmethoden
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Findet alle Termine eines Benutzers
     */
    List<Appointment> findByUserIdOrderByStartDateTimeAsc(Long userId);

    /**
     * Findet Termine in einem bestimmten Zeitraum
     */
    @Query("SELECT a FROM Appointment a WHERE a.startDateTime >= :startDate AND a.endDateTime <= :endDate ORDER BY a.startDateTime ASC")
    List<Appointment> findAppointmentsInDateRange(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);

    /**
     * Findet Termine eines Benutzers in einem bestimmten Zeitraum
     */
    @Query("SELECT a FROM Appointment a WHERE a.userId = :userId AND a.startDateTime >= :startDate AND a.endDateTime <= :endDate ORDER BY a.startDateTime ASC")
    List<Appointment> findUserAppointmentsInDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);

    /**
     * Findet Termine eines Benutzers für Erinnerungen (nur nach Startzeit filtern)
     */
    @Query("SELECT a FROM Appointment a WHERE a.userId = :userId AND a.startDateTime >= :startDate AND a.startDateTime <= :endDate ORDER BY a.startDateTime ASC")
    List<Appointment> findUserAppointmentsForReminders(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);

    /**
     * Findet Termine an einem bestimmten Datum
     */
    @Query("SELECT a FROM Appointment a WHERE a.startDateTime >= :startOfDay AND a.startDateTime < :endOfDay ORDER BY a.startDateTime ASC")
    List<Appointment> findAppointmentsByDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * Findet Termine eines Benutzers an einem bestimmten Datum
     */
    @Query("SELECT a FROM Appointment a WHERE a.userId = :userId AND a.startDateTime >= :startOfDay AND a.startDateTime < :endOfDay ORDER BY a.startDateTime ASC")
    List<Appointment> findUserAppointmentsByDate(@Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * Sucht Termine nach Titel oder Beschreibung
     */
    @Query("SELECT a FROM Appointment a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY a.startDateTime ASC")
    List<Appointment> searchAppointments(@Param("searchTerm") String searchTerm);

    /**
     * Sucht Termine eines Benutzers nach Titel oder Beschreibung
     */
    @Query("SELECT a FROM Appointment a WHERE a.userId = :userId AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) ORDER BY a.startDateTime ASC")
    List<Appointment> searchUserAppointments(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);

    /**
     * Findet alle Termine mit Paginierung
     */
    Page<Appointment> findAllByOrderByStartDateTimeAsc(Pageable pageable);

    /**
     * Findet alle Termine eines Benutzers mit Paginierung
     */
    Page<Appointment> findByUserIdOrderByStartDateTimeAsc(Long userId, Pageable pageable);

    /**
     * Prüft auf Terminüberschneidungen für einen Benutzer
     */
    @Query("SELECT a FROM Appointment a WHERE a.userId = :userId AND " +
           "((a.startDateTime < :endDateTime AND a.endDateTime > :startDateTime))")
    List<Appointment> findOverlappingAppointments(
            @Param("userId") Long userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    /**
     * Findet Termine in einem Datumsbereich mit Paginierung
     */
    Page<Appointment> findByStartDateTimeBetween(
            LocalDateTime startDateTime, 
            LocalDateTime endDateTime, 
            Pageable pageable);

    /**
     * Findet Termine eines Benutzers in einem Datumsbereich mit Paginierung
     */
    Page<Appointment> findByUserIdAndStartDateTimeBetween(
            Long userId,
            LocalDateTime startDateTime, 
            LocalDateTime endDateTime, 
            Pageable pageable);
}
