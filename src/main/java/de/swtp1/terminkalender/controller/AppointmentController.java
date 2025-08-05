package de.swtp1.terminkalender.controller;

import de.swtp1.terminkalender.dto.AppointmentRequestDto;
import de.swtp1.terminkalender.dto.AppointmentResponseDto;
import de.swtp1.terminkalender.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller für Appointment API
 * Implementiert alle Endpoints entsprechend der API-Spezifikation
 */
@RestController
@RequestMapping("/api/v1/appointments")
@CrossOrigin(origins = "*") // Für Frontend-Entwicklung
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * GET /api/v1/appointments
     * Alle Termine abrufen mit optionalen Filtern
     */
    @GetMapping
    public ResponseEntity<Object> getAllAppointments(
            @RequestParam(value = "date", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            
            @RequestParam(value = "startDate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @RequestParam(value = "endDate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            
            @RequestParam(value = "search", required = false) String searchTerm,
            
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        try {
            // Spezifische Datum-Suche
            if (date != null) {
                List<AppointmentResponseDto> appointments = appointmentService.findAppointmentsByDate(date);
                return ResponseEntity.ok(appointments);
            }
            
            // Zeitraum-Suche
            if (startDate != null && endDate != null) {
                List<AppointmentResponseDto> appointments = appointmentService.findAppointmentsInDateRange(startDate, endDate);
                return ResponseEntity.ok(appointments);
            }
            
            // Text-Suche
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                List<AppointmentResponseDto> appointments = appointmentService.searchAppointments(searchTerm);
                return ResponseEntity.ok(appointments);
            }
            
            // Alle Termine mit Paginierung
            Page<AppointmentResponseDto> appointments = appointmentService.findAllAppointments(page, size);
            return ResponseEntity.ok(appointments);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler beim Abrufen der Termine: " + e.getMessage());
        }
    }

    /**
     * GET /api/v1/appointments/{id}
     * Einzelnen Termin abrufen
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getAppointmentById(@PathVariable Long id) {
        try {
            Optional<AppointmentResponseDto> appointment = appointmentService.findAppointmentById(id);
            
            if (appointment.isPresent()) {
                return ResponseEntity.ok(appointment.get());
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler beim Abrufen des Termins: " + e.getMessage());
        }
    }

    /**
     * POST /api/v1/appointments
     * Neuen Termin erstellen
     */
    @PostMapping
    public ResponseEntity<Object> createAppointment(@Valid @RequestBody AppointmentRequestDto requestDto) {
        try {
            AppointmentResponseDto createdAppointment = appointmentService.createAppointment(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ungültige Daten: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler beim Erstellen des Termins: " + e.getMessage());
        }
    }

    /**
     * PUT /api/v1/appointments/{id}
     * Termin aktualisieren
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAppointment(@PathVariable Long id, 
                                                   @Valid @RequestBody AppointmentRequestDto requestDto) {
        try {
            AppointmentResponseDto updatedAppointment = appointmentService.updateAppointment(id, requestDto);
            return ResponseEntity.ok(updatedAppointment);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Ungültige Daten: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler beim Aktualisieren des Termins: " + e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/appointments/{id}
     * Termin löschen
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
            
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler beim Löschen des Termins: " + e.getMessage());
        }
    }
}
