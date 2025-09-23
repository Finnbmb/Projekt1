package de.swtp1.terminkalender.service;

import de.swtp1.terminkalender.dto.AppointmentResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service für Kalenderansichten
 * Phase 3: Erweiterte Funktionalität
 */
@Service
@Transactional(readOnly = true)
public class CalendarService {

    private final AppointmentService appointmentService;

    @Autowired
    public CalendarService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * Monatsansicht: Alle Termine eines Monats
     */
    public Map<LocalDate, List<AppointmentResponseDto>> getMonthView(int year, int month) {
        try {
            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());
            
            LocalDateTime startDateTime = startOfMonth.atStartOfDay();
            LocalDateTime endDateTime = endOfMonth.atTime(23, 59, 59);
            
            List<AppointmentResponseDto> appointments = appointmentService
                    .findAppointmentsInDateRange(startDateTime, endDateTime);
            
            return appointments.stream()
                    .collect(Collectors.groupingBy(
                        appointment -> appointment.getStartDateTime().toLocalDate()
                    ));
        } catch (Exception e) {
            System.err.println("Fehler in getMonthView: " + e.getMessage());
            e.printStackTrace();
            return Map.of(); // Leere Map zurückgeben statt Exception
        }
    }

    /**
     * Wochenansicht: Alle Termine einer Woche
     */
    public Map<LocalDate, List<AppointmentResponseDto>> getWeekView(LocalDate weekStart) {
        try {
            LocalDate startOfWeek = weekStart.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            
            LocalDateTime startDateTime = startOfWeek.atStartOfDay();
            LocalDateTime endDateTime = endOfWeek.atTime(23, 59, 59);
            
            List<AppointmentResponseDto> appointments = appointmentService
                    .findAppointmentsInDateRange(startDateTime, endDateTime);
            
            return appointments.stream()
                    .collect(Collectors.groupingBy(
                        appointment -> appointment.getStartDateTime().toLocalDate()
                    ));
        } catch (Exception e) {
            System.err.println("Fehler in getWeekView: " + e.getMessage());
            e.printStackTrace();
            return Map.of(); // Leere Map zurückgeben statt Exception
        }
    }

    /**
     * Tagesansicht: Alle Termine eines Tages
     */
    public List<AppointmentResponseDto> getDayView(LocalDate date) {
        try {
            return appointmentService.findAppointmentsByDate(date);
        } catch (Exception e) {
            System.err.println("Fehler in getDayView: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Leere Liste zurückgeben statt Exception
        }
    }

    /**
     * Kommende Termine (nächste 7 Tage)
     */
    public List<AppointmentResponseDto> getUpcomingAppointments() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextWeek = now.plusDays(7);
            
            return appointmentService.findAppointmentsInDateRange(now, nextWeek);
        } catch (Exception e) {
            System.err.println("Fehler in getUpcomingAppointments: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Leere Liste zurückgeben statt Exception
        }
    }

    /**
     * Heutige Termine
     */
    public List<AppointmentResponseDto> getTodaysAppointments() {
        try {
            return appointmentService.findAppointmentsByDate(LocalDate.now());
        } catch (Exception e) {
            System.err.println("Fehler in getTodaysAppointments: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Leere Liste zurückgeben statt Exception
        }
    }

    /**
     * Termine diese Woche
     */
    public Map<LocalDate, List<AppointmentResponseDto>> getThisWeeksAppointments() {
        try {
            return getWeekView(LocalDate.now());
        } catch (Exception e) {
            System.err.println("Fehler in getThisWeeksAppointments: " + e.getMessage());
            e.printStackTrace();
            return Map.of(); // Leere Map zurückgeben statt Exception
        }
    }

    /**
     * Termine diesen Monat
     */
    public Map<LocalDate, List<AppointmentResponseDto>> getThisMonthsAppointments() {
        try {
            LocalDate now = LocalDate.now();
            return getMonthView(now.getYear(), now.getMonthValue());
        } catch (Exception e) {
            System.err.println("Fehler in getThisMonthsAppointments: " + e.getMessage());
            e.printStackTrace();
            return Map.of(); // Leere Map zurückgeben statt Exception
        }
    }
    
    /**
     * Testing-Methoden ohne Authentifizierung
     */
    public List<AppointmentResponseDto> getTodaysAppointmentsForTesting() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(23, 59, 59);
            
            return appointmentService.findAllAppointmentsInDateRangeForTesting(startOfDay, endOfDay);
        } catch (Exception e) {
            System.err.println("Fehler in getTodaysAppointmentsForTesting: " + e.getMessage());
            return List.of();
        }
    }
    
    public List<AppointmentResponseDto> getUpcomingAppointmentsForTesting() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextWeek = now.plusDays(7);
            
            return appointmentService.findAllAppointmentsInDateRangeForTesting(now, nextWeek);
        } catch (Exception e) {
            System.err.println("Fehler in getUpcomingAppointmentsForTesting: " + e.getMessage());
            return List.of();
        }
    }
    
    public Map<LocalDate, List<AppointmentResponseDto>> getThisWeeksAppointmentsForTesting() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            
            LocalDateTime startDateTime = startOfWeek.atStartOfDay();
            LocalDateTime endDateTime = endOfWeek.atTime(23, 59, 59);
            
            List<AppointmentResponseDto> appointments = appointmentService
                    .findAllAppointmentsInDateRangeForTesting(startDateTime, endDateTime);
            
            return appointments.stream()
                    .collect(Collectors.groupingBy(
                        appointment -> appointment.getStartDateTime().toLocalDate()
                    ));
        } catch (Exception e) {
            System.err.println("Fehler in getThisWeeksAppointmentsForTesting: " + e.getMessage());
            return Map.of();
        }
    }
    
    public Map<LocalDate, List<AppointmentResponseDto>> getThisMonthsAppointmentsForTesting() {
        try {
            LocalDate now = LocalDate.now();
            return getMonthViewForTesting(now.getYear(), now.getMonthValue());
        } catch (Exception e) {
            System.err.println("Fehler in getThisMonthsAppointmentsForTesting: " + e.getMessage());
            return Map.of();
        }
    }
    
    public Map<LocalDate, List<AppointmentResponseDto>> getMonthViewForTesting(int year, int month) {
        try {
            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());
            
            LocalDateTime startDateTime = startOfMonth.atStartOfDay();
            LocalDateTime endDateTime = endOfMonth.atTime(23, 59, 59);
            
            List<AppointmentResponseDto> appointments = appointmentService
                    .findAllAppointmentsInDateRangeForTesting(startDateTime, endDateTime);
            
            return appointments.stream()
                    .collect(Collectors.groupingBy(
                        appointment -> appointment.getStartDateTime().toLocalDate()
                    ));
        } catch (Exception e) {
            System.err.println("Fehler in getMonthViewForTesting: " + e.getMessage());
            return Map.of();
        }
    }
}