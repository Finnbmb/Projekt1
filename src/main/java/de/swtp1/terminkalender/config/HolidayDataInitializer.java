package de.swtp1.terminkalender.config;

import de.swtp1.terminkalender.entity.Holiday;
import de.swtp1.terminkalender.repository.HolidayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * Initialisiert deutsche Feiertage in der Datenbank
 * Läuft beim Start der Anwendung und befüllt die Holiday-Tabelle
 */
@Component
public class HolidayDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(HolidayDataInitializer.class);

    @Autowired
    private HolidayRepository holidayRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initialisiere deutsche Feiertage...");
        
        // Prüfen ob bereits Feiertage existieren
        if (holidayRepository.count() > 0) {
            logger.info("Feiertage bereits in der Datenbank vorhanden. Überspringe Initialisierung.");
            return;
        }

        // Feiertage für die nächsten 5 Jahre erstellen
        int currentYear = Year.now().getValue();
        for (int year = currentYear - 1; year <= currentYear + 5; year++) {
            createHolidaysForYear(year);
        }

        logger.info("Deutsche Feiertage erfolgreich initialisiert.");
    }

    private void createHolidaysForYear(int year) {
        List<Holiday> holidays = new ArrayList<>();

        // **Bundesweite Feiertage**
        
        // Neujahr
        holidays.add(new Holiday("Neujahr", LocalDate.of(year, 1, 1), 
                Holiday.HolidayType.PUBLIC, null));
        
        // Ostersonntag und davon abhängige Tage berechnen
        LocalDate easter = calculateEaster(year);
        
        // Karfreitag (2 Tage vor Ostersonntag)
        holidays.add(new Holiday("Karfreitag", easter.minusDays(2), 
                Holiday.HolidayType.PUBLIC, null));
        
        // Ostermontag (1 Tag nach Ostersonntag)
        holidays.add(new Holiday("Ostermontag", easter.plusDays(1), 
                Holiday.HolidayType.PUBLIC, null));
        
        // Tag der Arbeit
        holidays.add(new Holiday("Tag der Arbeit", LocalDate.of(year, 5, 1), 
                Holiday.HolidayType.PUBLIC, null));
        
        // Christi Himmelfahrt (39 Tage nach Ostersonntag)
        holidays.add(new Holiday("Christi Himmelfahrt", easter.plusDays(39), 
                Holiday.HolidayType.PUBLIC, null));
        
        // Pfingstmontag (50 Tage nach Ostersonntag)
        holidays.add(new Holiday("Pfingstmontag", easter.plusDays(50), 
                Holiday.HolidayType.PUBLIC, null));
        
        // Tag der Deutschen Einheit
        holidays.add(new Holiday("Tag der Deutschen Einheit", LocalDate.of(year, 10, 3), 
                Holiday.HolidayType.PUBLIC, null));
        
        // Weihnachtsfeiertage
        holidays.add(new Holiday("1. Weihnachtsfeiertag", LocalDate.of(year, 12, 25), 
                Holiday.HolidayType.PUBLIC, null));
        holidays.add(new Holiday("2. Weihnachtsfeiertag", LocalDate.of(year, 12, 26), 
                Holiday.HolidayType.PUBLIC, null));

        // **Bundeslandspezifische Feiertage**
        
        // Heilige Drei Könige (BW, BY, ST)
        holidays.add(new Holiday("Heilige Drei Könige", LocalDate.of(year, 1, 6), 
                Holiday.HolidayType.PUBLIC, "BW"));
        holidays.add(new Holiday("Heilige Drei Könige", LocalDate.of(year, 1, 6), 
                Holiday.HolidayType.PUBLIC, "BY"));
        holidays.add(new Holiday("Heilige Drei Könige", LocalDate.of(year, 1, 6), 
                Holiday.HolidayType.PUBLIC, "ST"));
        
        // Fronleichnam (BW, BY, HE, NW, RP, SL)
        LocalDate fronleichnam = easter.plusDays(60);
        String[] fronleichnamStates = {"BW", "BY", "HE", "NW", "RP", "SL"};
        for (String state : fronleichnamStates) {
            holidays.add(new Holiday("Fronleichnam", fronleichnam, 
                    Holiday.HolidayType.PUBLIC, state));
        }
        
        // Mariä Himmelfahrt (BY, SL)
        holidays.add(new Holiday("Mariä Himmelfahrt", LocalDate.of(year, 8, 15), 
                Holiday.HolidayType.PUBLIC, "BY"));
        holidays.add(new Holiday("Mariä Himmelfahrt", LocalDate.of(year, 8, 15), 
                Holiday.HolidayType.PUBLIC, "SL"));
        
        // Reformationstag (BB, MV, SN, ST, TH)
        String[] reformationStates = {"BB", "MV", "SN", "ST", "TH"};
        for (String state : reformationStates) {
            holidays.add(new Holiday("Reformationstag", LocalDate.of(year, 10, 31), 
                    Holiday.HolidayType.PUBLIC, state));
        }
        
        // Allerheiligen (BW, BY, NW, RP, SL)
        String[] allerheiligenStates = {"BW", "BY", "NW", "RP", "SL"};
        for (String state : allerheiligenStates) {
            holidays.add(new Holiday("Allerheiligen", LocalDate.of(year, 11, 1), 
                    Holiday.HolidayType.PUBLIC, state));
        }
        
        // Buß- und Bettag (SN)
        LocalDate bussUndBettag = getBussUndBettag(year);
        holidays.add(new Holiday("Buß- und Bettag", bussUndBettag, 
                Holiday.HolidayType.PUBLIC, "SN"));

        // **Spezielle Tage**
        
        // Internationaler Frauentag - Berlin
        holidays.add(new Holiday("Internationaler Frauentag", LocalDate.of(year, 3, 8), 
                Holiday.HolidayType.PUBLIC, "BE"));
        
        // Weltkindertag - Thüringen
        holidays.add(new Holiday("Weltkindertag", LocalDate.of(year, 9, 20), 
                Holiday.HolidayType.PUBLIC, "TH"));

        // Alle Feiertage für dieses Jahr speichern
        try {
            holidayRepository.saveAll(holidays);
            logger.info("Feiertage für Jahr {} erfolgreich erstellt ({} Einträge)", year, holidays.size());
        } catch (Exception e) {
            logger.error("Fehler beim Speichern der Feiertage für Jahr {}: {}", year, e.getMessage());
        }
    }

    /**
     * Berechnet das Osterdatum nach dem Gregorianischen Kalender
     * Verwendet den Algorithmus von Carl Friedrich Gauss
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
     * Berechnet den Buß- und Bettag (letzter Mittwoch vor dem 1. Advent)
     */
    private LocalDate getBussUndBettag(int year) {
        // 1. Advent ist der 4. Sonntag vor dem 1. Weihnachtsfeiertag
        LocalDate christmas = LocalDate.of(year, 12, 25);
        LocalDate firstAdvent = christmas.minusDays((christmas.getDayOfWeek().getValue() % 7) + 21);
        
        // Buß- und Bettag ist der Mittwoch vor dem 1. Advent
        int daysToWednesday = (firstAdvent.getDayOfWeek().getValue() + 4) % 7;
        return firstAdvent.minusDays(daysToWednesday);
    }
}