package de.swtp1.terminkalender.controller;

import de.swtp1.terminkalender.dto.AppointmentResponseDto;
import de.swtp1.terminkalender.entity.Holiday;
import de.swtp1.terminkalender.service.AppointmentService;
import de.swtp1.terminkalender.service.HolidayService;
import de.swtp1.terminkalender.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Debug Controller für öffentliche API-Tests ohne Authentifizierung
 * Nur für Development und Testing verwenden!
 */
@RestController
@RequestMapping(value = "/debug-api", produces = "application/json;charset=UTF-8")
@CrossOrigin(origins = "*")
public class DebugController {

    private final AppointmentService appointmentService;
    private final HolidayService holidayService;
    private final CalendarService calendarService;

    @Autowired
    public DebugController(AppointmentService appointmentService, 
                          HolidayService holidayService,
                          CalendarService calendarService) {
        this.appointmentService = appointmentService;
        this.holidayService = holidayService;
        this.calendarService = calendarService;
    }

    /**
     * Test Endpoint
     */
    @GetMapping(value = "/test", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Debug API funktioniert!");
    }

    /**
     * Test Calendar Controller
     */
    @GetMapping(value = "/calendar/test", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> testCalendar() {
        return ResponseEntity.ok("Calendar Controller funktioniert via Debug API!");
    }

    /**
     * Appointments ohne Authentifizierung (für Debug)
     */
    @GetMapping(value = "/appointments", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> getAppointments() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Lade alle Appointments ohne User-Filter für Debug-Zwecke
            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<AppointmentResponseDto> appointments = appointmentService.getAllAppointmentsForTesting(pageRequest);
            
            response.put("content", appointments.getContent());
            response.put("totalElements", appointments.getTotalElements());
            response.put("totalPages", appointments.getTotalPages());
            response.put("size", appointments.getSize());
            response.put("status", "success");
            
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(response);
        } catch (Exception e) {
            System.err.println("DEBUG: Error in getAppointments: " + e.getMessage());
            e.printStackTrace();
            
            response.put("error", "Fehler beim Laden der Termine: " + e.getMessage());
            response.put("totalElements", 0);
            response.put("status", "error");
            
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(response);
        }
    }

    /**
     * Today's appointments ohne Auth
     */
    @GetMapping(value = "/calendar/today", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<AppointmentResponseDto>> getTodaysAppointments() {
        try {
            List<AppointmentResponseDto> todaysAppointments = calendarService.getTodaysAppointmentsForTesting();
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(todaysAppointments);
        } catch (Exception e) {
            System.err.println("DEBUG: Error in getTodaysAppointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(List.of()); // Leere Liste statt Fehler
        }
    }

    /**
     * Upcoming appointments ohne Auth
     */
    @GetMapping(value = "/calendar/upcoming", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<AppointmentResponseDto>> getUpcomingAppointments() {
        try {
            List<AppointmentResponseDto> upcomingAppointments = calendarService.getUpcomingAppointmentsForTesting();
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(upcomingAppointments);
        } catch (Exception e) {
            System.err.println("DEBUG: Error in getUpcomingAppointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(List.of());
        }
    }

    /**
     * This week appointments ohne Auth
     */
    @GetMapping("/calendar/this-week")
    public ResponseEntity<Map<LocalDate, List<AppointmentResponseDto>>> getThisWeeksAppointments() {
        try {
            Map<LocalDate, List<AppointmentResponseDto>> thisWeek = calendarService.getThisWeeksAppointmentsForTesting();
            return ResponseEntity.ok(thisWeek);
        } catch (Exception e) {
            System.err.println("DEBUG: Error in getThisWeeksAppointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Map.of());
        }
    }

    /**
     * This month appointments ohne Auth
     */
    @GetMapping("/calendar/this-month")
    public ResponseEntity<Map<LocalDate, List<AppointmentResponseDto>>> getThisMonthsAppointments() {
        try {
            Map<LocalDate, List<AppointmentResponseDto>> thisMonth = calendarService.getThisMonthsAppointmentsForTesting();
            return ResponseEntity.ok(thisMonth);
        } catch (Exception e) {
            System.err.println("DEBUG: Error in getThisMonthsAppointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Map.of());
        }
    }

    /**
     * Holidays ohne Auth
     */
    @GetMapping("/holidays/year/{year}")
    public ResponseEntity<List<Holiday>> getHolidaysForYear(@PathVariable int year) {
        try {
            List<Holiday> holidays = holidayService.getHolidaysForYear(year);
            return ResponseEntity.ok(holidays);
        } catch (Exception e) {
            System.err.println("DEBUG: Error in getHolidaysForYear: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Calendar View Current Month ohne Auth
     */
    @GetMapping("/calendar-view/current-month")
    public ResponseEntity<Map<String, Object>> getCurrentMonthView() {
        try {
            LocalDate now = LocalDate.now();
            Map<String, Object> response = new HashMap<>();
            response.put("month", now.getMonthValue());
            response.put("year", now.getYear());
            response.put("days", now.lengthOfMonth());
            
            // Simuliere Kalenderansicht
            Map<LocalDate, List<AppointmentResponseDto>> monthData = calendarService.getMonthViewForTesting(now.getYear(), now.getMonthValue());
            response.put("appointments", monthData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("DEBUG: Error in getCurrentMonthView: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * Statistics ohne Auth
     */
    @GetMapping("/calendar-view/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("startDate", startDate);
            stats.put("endDate", endDate);
            stats.put("totalDays", 31);
            stats.put("workDays", 22);
            stats.put("appointmentDays", 8);
            stats.put("message", "Simulierte Statistiken für Debug-Zwecke");
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("DEBUG: Error in getStatistics: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * System Status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "OK");
        status.put("timestamp", System.currentTimeMillis());
        status.put("message", "Debug API läuft");
        
        try {
            // Teste Datenbankverbindung
            long appointmentCount = appointmentService.countAllAppointments();
            status.put("appointmentCount", appointmentCount);
            status.put("database", "Connected");
        } catch (Exception e) {
            status.put("database", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(status);
    }

    // Password Reset Debug Functions
    @Autowired
    private de.swtp1.terminkalender.repository.UserRepository userRepository;

    /**
     * Gibt den letzten generierten Password Reset Token für einen User zurück
     * ACHTUNG: Nur für Development! Nie in Production verwenden!
     */
    @GetMapping("/password-reset-token/{email}")
    public ResponseEntity<?> getLastPasswordResetToken(@PathVariable String email) {
        try {
            var userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "User nicht gefunden");
                return ResponseEntity.badRequest().body(response);
            }

            var user = userOptional.get();
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("token", user.getPasswordResetToken());
            response.put("tokenExpiry", user.getPasswordResetTokenExpiry());
            response.put("hasToken", user.getPasswordResetToken() != null);
            response.put("message", "Debug: Password Reset Token abgerufen");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Fehler beim Abrufen des Tokens: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Zeigt alle verfügbaren Users für Debug-Zwecke
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            var users = userRepository.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("count", users.size());
            response.put("users", users.stream().map(user -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("email", user.getEmail());
                userInfo.put("username", user.getUsername());
                userInfo.put("hasResetToken", user.getPasswordResetToken() != null);
                if (user.getPasswordResetToken() != null) {
                    userInfo.put("resetTokenExpiry", user.getPasswordResetTokenExpiry());
                }
                return userInfo;
            }).toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Fehler beim Abrufen der Users: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}