package de.swtp1.terminkalender.controller;

import de.swtp1.terminkalender.entity.Holiday;
import de.swtp1.terminkalender.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller für Feiertage
 * Stellt Endpoints für Feiertagsabfragen bereit
 */
@RestController
@RequestMapping("/api/v1/holidays")
@CrossOrigin(origins = "*")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    /**
     * Prüft, ob ein Datum ein Feiertag ist
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> isHoliday(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String federalState) {
        
        boolean isHoliday = holidayService.isHoliday(date, federalState);
        List<Holiday> holidays = holidayService.getHolidaysForDate(date, federalState);
        
        Map<String, Object> response = Map.of(
                "date", date,
                "isHoliday", isHoliday,
                "holidays", holidays,
                "federalState", federalState != null ? federalState : "bundesweit"
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Holt alle Feiertage für ein Jahr und Bundesland
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<List<Holiday>> getHolidaysForYear(
            @PathVariable int year,
            @RequestParam(required = false) String federalState) {
        
        List<Holiday> holidays = holidayService.getHolidaysForYear(year, federalState);
        return ResponseEntity.ok(holidays);
    }

    /**
     * Holt alle Feiertage in einem Datumsbereich
     */
    @GetMapping("/range")
    public ResponseEntity<List<Holiday>> getHolidaysInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String federalState) {
        
        List<Holiday> holidays = holidayService.getHolidaysInRange(startDate, endDate, federalState);
        return ResponseEntity.ok(holidays);
    }

    /**
     * Initialisiert Feiertage für ein Jahr
     */
    @PostMapping("/initialize/{year}")
    public ResponseEntity<Map<String, String>> initializeHolidays(@PathVariable int year) {
        holidayService.initializeGermanHolidays(year);
        
        Map<String, String> response = Map.of(
                "message", "Feiertage für " + year + " wurden erfolgreich initialisiert",
                "year", String.valueOf(year)
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Initialisiert Feiertage für die nächsten 5 Jahre (aktuelles Jahr + 4 weitere)
     */
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeAllHolidays() {
        int currentYear = LocalDate.now().getYear();
        int yearsInitialized = 0;
        
        for (int year = currentYear; year <= currentYear + 4; year++) {
            holidayService.initializeGermanHolidays(year);
            yearsInitialized++;
        }
        
        return ResponseEntity.ok(
            "Deutsche Feiertage für " + yearsInitialized + 
            " Jahre erfolgreich initialisiert (" + currentYear + " - " + (currentYear + 4) + ")"
        );
    }

    /**
     * Aktualisiert die Feiertage für das aktuelle Jahr
     */
    @PostMapping("/update-current-year")
    public ResponseEntity<Map<String, String>> updateCurrentYearHolidays() {
        holidayService.updateCurrentYearHolidays();
        
        Map<String, String> response = Map.of(
                "message", "Feiertage für das aktuelle Jahr wurden erfolgreich aktualisiert"
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Holt alle verfügbaren Bundesländer
     */
    @GetMapping("/federal-states")
    public ResponseEntity<List<Map<String, String>>> getFederalStates() {
        List<Map<String, String>> federalStates = List.of(
                Map.of("code", "BW", "name", "Baden-Württemberg"),
                Map.of("code", "BY", "name", "Bayern"),
                Map.of("code", "BE", "name", "Berlin"),
                Map.of("code", "BB", "name", "Brandenburg"),
                Map.of("code", "HB", "name", "Bremen"),
                Map.of("code", "HH", "name", "Hamburg"),
                Map.of("code", "HE", "name", "Hessen"),
                Map.of("code", "MV", "name", "Mecklenburg-Vorpommern"),
                Map.of("code", "NI", "name", "Niedersachsen"),
                Map.of("code", "NW", "name", "Nordrhein-Westfalen"),
                Map.of("code", "RP", "name", "Rheinland-Pfalz"),
                Map.of("code", "SL", "name", "Saarland"),
                Map.of("code", "SN", "name", "Sachsen"),
                Map.of("code", "ST", "name", "Sachsen-Anhalt"),
                Map.of("code", "SH", "name", "Schleswig-Holstein"),
                Map.of("code", "TH", "name", "Thüringen")
        );
        
        return ResponseEntity.ok(federalStates);
    }
}
