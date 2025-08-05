package de.swtp1.terminkalender.service;

import de.swtp1.terminkalender.entity.Holiday;
import de.swtp1.terminkalender.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Service für Feiertagsverwaltung
 * Verwaltet deutsche Feiertage mit bundeslandspezifischen Regeln
 */
@Service
@Transactional
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    /**
     * Prüft, ob ein bestimmtes Datum ein Feiertag ist
     */
    @Transactional(readOnly = true)
    public boolean isHoliday(LocalDate date, String federalState) {
        List<Holiday> holidays = holidayRepository.findHolidaysByDateAndFederalState(date, federalState);
        return !holidays.isEmpty();
    }

    /**
     * Findet alle Feiertage für ein bestimmtes Datum und Bundesland
     */
    @Transactional(readOnly = true)
    public List<Holiday> getHolidaysForDate(LocalDate date, String federalState) {
        return holidayRepository.findHolidaysByDateAndFederalState(date, federalState);
    }

    /**
     * Findet alle Feiertage in einem Datumsbereich
     */
    @Transactional(readOnly = true)
    public List<Holiday> getHolidaysInRange(LocalDate startDate, LocalDate endDate, String federalState) {
        List<Holiday> holidays = holidayRepository.findByDateBetween(startDate, endDate);
        
        // Filtere nach Bundesland
        if (federalState != null) {
            holidays = holidays.stream()
                    .filter(h -> h.getFederalState() == null || h.getFederalState().equals(federalState))
                    .toList();
        }
        
        return holidays;
    }

    /**
     * Findet alle Feiertage für ein Jahr und Bundesland
     */
    @Transactional(readOnly = true)
    public List<Holiday> getHolidaysForYear(int year, String federalState) {
        return holidayRepository.findHolidaysByYearAndFederalState(year, federalState);
    }

    /**
     * Initialisiert deutsche Feiertage für ein Jahr
     */
    public void initializeGermanHolidays(int year) {
        // Bundesweite Feiertage
        createHoliday("Neujahr", LocalDate.of(year, 1, 1), Holiday.HolidayType.PUBLIC, null);
        createHoliday("Tag der Arbeit", LocalDate.of(year, 5, 1), Holiday.HolidayType.PUBLIC, null);
        createHoliday("Tag der Deutschen Einheit", LocalDate.of(year, 10, 3), Holiday.HolidayType.PUBLIC, null);
        createHoliday("1. Weihnachtsfeiertag", LocalDate.of(year, 12, 25), Holiday.HolidayType.PUBLIC, null);
        createHoliday("2. Weihnachtsfeiertag", LocalDate.of(year, 12, 26), Holiday.HolidayType.PUBLIC, null);

        // Berechne Osterdatum
        LocalDate easter = calculateEaster(year);
        createHoliday("Karfreitag", easter.minusDays(2), Holiday.HolidayType.RELIGIOUS, null);
        createHoliday("Ostermontag", easter.plusDays(1), Holiday.HolidayType.RELIGIOUS, null);
        createHoliday("Christi Himmelfahrt", easter.plusDays(39), Holiday.HolidayType.RELIGIOUS, null);
        createHoliday("Pfingstmontag", easter.plusDays(50), Holiday.HolidayType.RELIGIOUS, null);

        // Bundeslandspezifische Feiertage
        initializeFederalStateHolidays(year, easter);
    }

    /**
     * Initialisiert bundeslandspezifische Feiertage
     */
    private void initializeFederalStateHolidays(int year, LocalDate easter) {
        // Heilige Drei Könige (BW, BY, ST)
        createHoliday("Heilige Drei Könige", LocalDate.of(year, 1, 6), Holiday.HolidayType.RELIGIOUS, "BW");
        createHoliday("Heilige Drei Könige", LocalDate.of(year, 1, 6), Holiday.HolidayType.RELIGIOUS, "BY");
        createHoliday("Heilige Drei Könige", LocalDate.of(year, 1, 6), Holiday.HolidayType.RELIGIOUS, "ST");

        // Fronleichnam (BW, BY, HE, NW, RP, SL)
        LocalDate fronleichnam = easter.plusDays(60);
        String[] fronleichnamStates = {"BW", "BY", "HE", "NW", "RP", "SL"};
        for (String state : fronleichnamStates) {
            createHoliday("Fronleichnam", fronleichnam, Holiday.HolidayType.RELIGIOUS, state);
        }

        // Mariä Himmelfahrt (BY, SL)
        createHoliday("Mariä Himmelfahrt", LocalDate.of(year, 8, 15), Holiday.HolidayType.RELIGIOUS, "BY");
        createHoliday("Mariä Himmelfahrt", LocalDate.of(year, 8, 15), Holiday.HolidayType.RELIGIOUS, "SL");

        // Reformationstag (BB, MV, SN, ST, TH)
        String[] reformationstagStates = {"BB", "MV", "SN", "ST", "TH"};
        for (String state : reformationstagStates) {
            createHoliday("Reformationstag", LocalDate.of(year, 10, 31), Holiday.HolidayType.RELIGIOUS, state);
        }

        // Allerheiligen (BW, BY, NW, RP, SL)
        String[] allerheilligenStates = {"BW", "BY", "NW", "RP", "SL"};
        for (String state : allerheilligenStates) {
            createHoliday("Allerheiligen", LocalDate.of(year, 11, 1), Holiday.HolidayType.RELIGIOUS, state);
        }

        // Buß- und Bettag (SN)
        LocalDate bussUndBettag = getLastWednesdayInNovember(year);
        createHoliday("Buß- und Bettag", bussUndBettag, Holiday.HolidayType.RELIGIOUS, "SN");
    }

    /**
     * Erstellt einen Feiertag, falls er noch nicht existiert
     */
    private void createHoliday(String name, LocalDate date, Holiday.HolidayType type, String federalState) {
        List<Holiday> existing = holidayRepository.findHolidaysByDateAndFederalState(date, federalState);
        boolean exists = existing.stream().anyMatch(h -> h.getName().equals(name));
        
        if (!exists) {
            Holiday holiday = new Holiday(name, date, type, federalState);
            holidayRepository.save(holiday);
        }
    }

    /**
     * Berechnet das Osterdatum nach dem Gregorianischen Kalender
     */
    private LocalDate calculateEaster(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        
        return LocalDate.of(year, month, day);
    }

    /**
     * Findet den letzten Mittwoch im November (Buß- und Bettag)
     */
    private LocalDate getLastWednesdayInNovember(int year) {
        LocalDate lastDayOfNovember = LocalDate.of(year, 11, 30);
        
        // Finde den letzten Mittwoch
        while (lastDayOfNovember.getDayOfWeek().getValue() != 3) { // 3 = Mittwoch
            lastDayOfNovember = lastDayOfNovember.minusDays(1);
        }
        
        return lastDayOfNovember;
    }

    /**
     * Löscht alle Feiertage für ein Jahr
     */
    public void deleteHolidaysForYear(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        List<Holiday> holidays = holidayRepository.findByDateBetween(startOfYear, endOfYear);
        holidayRepository.deleteAll(holidays);
    }

    /**
     * Aktualisiert die Feiertage für das aktuelle Jahr
     */
    public void updateCurrentYearHolidays() {
        int currentYear = Year.now().getValue();
        deleteHolidaysForYear(currentYear);
        initializeGermanHolidays(currentYear);
    }
}
