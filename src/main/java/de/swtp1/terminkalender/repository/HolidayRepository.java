package de.swtp1.terminkalender.repository;

import de.swtp1.terminkalender.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository für Holiday Entity
 * Verwaltet Feiertage und bundeslandspezifische Abfragen
 */
@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    /**
     * Findet alle Feiertage für ein bestimmtes Datum
     */
    List<Holiday> findByDate(LocalDate date);

    /**
     * Findet alle Feiertage in einem Datumsbereich
     */
    @Query("SELECT h FROM Holiday h WHERE h.date BETWEEN :startDate AND :endDate")
    List<Holiday> findByDateBetween(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);

    /**
     * Findet bundesweite Feiertage für ein bestimmtes Datum
     */
    @Query("SELECT h FROM Holiday h WHERE h.date = :date AND h.federalState IS NULL")
    List<Holiday> findNationalHolidaysByDate(@Param("date") LocalDate date);

    /**
     * Findet Feiertage für ein bestimmtes Bundesland und Datum
     */
    @Query("SELECT h FROM Holiday h WHERE h.date = :date AND (h.federalState IS NULL OR h.federalState = :federalState)")
    List<Holiday> findHolidaysByDateAndFederalState(@Param("date") LocalDate date, 
                                                    @Param("federalState") String federalState);

    /**
     * Findet alle Feiertage für ein bestimmtes Bundesland in einem Jahr
     */
    @Query("SELECT h FROM Holiday h WHERE YEAR(h.date) = :year AND (h.federalState IS NULL OR h.federalState = :federalState)")
    List<Holiday> findHolidaysByYearAndFederalState(@Param("year") int year, 
                                                    @Param("federalState") String federalState);

    /**
     * Findet alle bundesweiten Feiertage in einem Jahr
     */
    @Query("SELECT h FROM Holiday h WHERE YEAR(h.date) = :year AND h.federalState IS NULL")
    List<Holiday> findNationalHolidaysByYear(@Param("year") int year);

    /**
     * Findet alle wiederkehrenden Feiertage
     */
    List<Holiday> findByIsRecurringTrue();

    /**
     * Findet Feiertage nach Typ
     */
    List<Holiday> findByType(Holiday.HolidayType type);
}
