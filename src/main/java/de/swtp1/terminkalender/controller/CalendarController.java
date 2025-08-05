package de.swtp1.terminkalender.controller;

import de.swtp1.terminkalender.dto.AppointmentResponseDto;
import de.swtp1.terminkalender.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller für Kalenderansichten
 * Phase 3: Erweiterte Funktionalität
 */
@RestController
@RequestMapping("/api/v1/calendar")
@CrossOrigin(origins = "*")
public class CalendarController {

    private final CalendarService calendarService;

    @Autowired
    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    /**
     * Test-Endpoint um zu prüfen ob Controller funktioniert
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Calendar Controller funktioniert!");
    }

    /**
     * GET /api/v1/calendar/today
     * Heutige Termine
     */
    @GetMapping("/today")
    public ResponseEntity<List<AppointmentResponseDto>> getTodaysAppointments() {
        try {
            System.out.println("DEBUG: getTodaysAppointments() called");
            List<AppointmentResponseDto> todaysAppointments = 
                    calendarService.getTodaysAppointments();
            System.out.println("DEBUG: Found " + todaysAppointments.size() + " appointments for today");
            return ResponseEntity.ok(todaysAppointments);
        } catch (Exception e) {
            System.err.println("ERROR in getTodaysAppointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/calendar/upcoming
     * Kommende Termine (nächste 7 Tage)
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentResponseDto>> getUpcomingAppointments() {
        try {
            System.out.println("DEBUG: getUpcomingAppointments() called");
            List<AppointmentResponseDto> upcomingAppointments = 
                    calendarService.getUpcomingAppointments();
            System.out.println("DEBUG: Found " + upcomingAppointments.size() + " upcoming appointments");
            return ResponseEntity.ok(upcomingAppointments);
        } catch (Exception e) {
            System.err.println("ERROR in getUpcomingAppointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/calendar/this-week
     * Diese Woche
     */
    @GetMapping("/this-week")
    public ResponseEntity<Map<LocalDate, List<AppointmentResponseDto>>> getThisWeeksAppointments() {
        try {
            System.out.println("DEBUG: getThisWeeksAppointments() called");
            Map<LocalDate, List<AppointmentResponseDto>> thisWeek = 
                    calendarService.getThisWeeksAppointments();
            System.out.println("DEBUG: This week appointments: " + thisWeek.size() + " days");
            return ResponseEntity.ok(thisWeek);
        } catch (Exception e) {
            System.err.println("ERROR in getThisWeeksAppointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/calendar/this-month
     * Dieser Monat
     */
    @GetMapping("/this-month")
    public ResponseEntity<Map<LocalDate, List<AppointmentResponseDto>>> getThisMonthsAppointments() {
        try {
            System.out.println("DEBUG: getThisMonthsAppointments() called");
            Map<LocalDate, List<AppointmentResponseDto>> thisMonth = 
                    calendarService.getThisMonthsAppointments();
            System.out.println("DEBUG: This month appointments: " + thisMonth.size() + " days");
            return ResponseEntity.ok(thisMonth);
        } catch (Exception e) {
            System.err.println("ERROR in getThisMonthsAppointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/calendar/month/{year}/{month}
     * Monatsansicht
     */
    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<Map<LocalDate, List<AppointmentResponseDto>>> getMonthView(
            @PathVariable int year,
            @PathVariable int month) {
        
        try {
            Map<LocalDate, List<AppointmentResponseDto>> monthView = 
                    calendarService.getMonthView(year, month);
            return ResponseEntity.ok(monthView);
        } catch (Exception e) {
            System.err.println("ERROR in getMonthView: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/calendar/week?date=YYYY-MM-DD
     * Wochenansicht
     */
    @GetMapping("/week")
    public ResponseEntity<Map<LocalDate, List<AppointmentResponseDto>>> getWeekView(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        try {
            Map<LocalDate, List<AppointmentResponseDto>> weekView = 
                    calendarService.getWeekView(date);
            return ResponseEntity.ok(weekView);
        } catch (Exception e) {
            System.err.println("ERROR in getWeekView: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/calendar/day?date=YYYY-MM-DD
     * Tagesansicht
     */
    @GetMapping("/day")
    public ResponseEntity<List<AppointmentResponseDto>> getDayView(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        try {
            List<AppointmentResponseDto> dayView = calendarService.getDayView(date);
            return ResponseEntity.ok(dayView);
        } catch (Exception e) {
            System.err.println("ERROR in getDayView: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}