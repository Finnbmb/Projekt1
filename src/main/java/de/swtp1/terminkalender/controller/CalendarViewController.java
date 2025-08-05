package de.swtp1.terminkalender.controller;

import de.swtp1.terminkalender.dto.CalendarViewDto;
import de.swtp1.terminkalender.service.CalendarViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller für erweiterte Kalenderansichten
 * Stellt verschiedene Kalenderviews bereit
 */
@RestController
@RequestMapping("/api/v1/calendar-view")
@CrossOrigin(origins = "*")
public class CalendarViewController {

    @Autowired
    private CalendarViewService calendarViewService;

    /**
     * Holt die Monatsansicht mit Terminen und Feiertagen
     */
    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<CalendarViewDto> getMonthView(
            @PathVariable int year,
            @PathVariable int month,
            @RequestParam(required = false) String federalState) {
        
        CalendarViewDto monthView = calendarViewService.getMonthView(year, month, federalState);
        return ResponseEntity.ok(monthView);
    }

    /**
     * Holt die Wochenansicht
     */
    @GetMapping("/week")
    public ResponseEntity<CalendarViewDto> getWeekView(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
            @RequestParam(required = false) String federalState) {
        
        CalendarViewDto weekView = calendarViewService.getWeekView(weekStart, federalState);
        return ResponseEntity.ok(weekView);
    }

    /**
     * Holt die Jahresübersicht mit Terminanzahlen pro Monat
     */
    @GetMapping("/year/{year}/overview")
    public ResponseEntity<Map<Integer, Integer>> getYearOverview(
            @PathVariable int year,
            @RequestParam(required = false) Long userId) {
        
        Map<Integer, Integer> yearOverview = calendarViewService.getYearOverview(year, userId);
        return ResponseEntity.ok(yearOverview);
    }

    /**
     * Findet freie Tage in einem Zeitraum
     */
    @GetMapping("/free-days")
    public ResponseEntity<List<LocalDate>> getFreeDays(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String federalState) {
        
        List<LocalDate> freeDays = calendarViewService.findFreeDays(startDate, endDate, federalState);
        return ResponseEntity.ok(freeDays);
    }

    /**
     * Holt Kalenderstatistiken für einen Zeitraum
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String federalState) {
        
        Map<String, Object> statistics = calendarViewService.getCalendarStatistics(startDate, endDate, federalState);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Holt die aktuelle Monatsansicht
     */
    @GetMapping("/current-month")
    public ResponseEntity<CalendarViewDto> getCurrentMonthView(
            @RequestParam(required = false) String federalState) {
        
        LocalDate now = LocalDate.now();
        CalendarViewDto monthView = calendarViewService.getMonthView(now.getYear(), now.getMonthValue(), federalState);
        return ResponseEntity.ok(monthView);
    }

    /**
     * Holt die aktuelle Wochenansicht
     */
    @GetMapping("/current-week")
    public ResponseEntity<CalendarViewDto> getCurrentWeekView(
            @RequestParam(required = false) String federalState) {
        
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1); // Montag als Wochenstart
        
        CalendarViewDto weekView = calendarViewService.getWeekView(startOfWeek, federalState);
        return ResponseEntity.ok(weekView);
    }
}
