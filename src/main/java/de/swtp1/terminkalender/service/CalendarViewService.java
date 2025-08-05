package de.swtp1.terminkalender.service;

import de.swtp1.terminkalender.dto.AppointmentResponseDto;
import de.swtp1.terminkalender.dto.CalendarViewDto;
import de.swtp1.terminkalender.entity.Holiday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service für erweiterte Kalenderansichten
 * Stellt verschiedene Kalenderviews mit Feiertagen bereit
 */
@Service
@Transactional(readOnly = true)
public class CalendarViewService {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private HolidayService holidayService;

    /**
     * Erstellt eine Monatsansicht mit Terminen und Feiertagen
     */
    public CalendarViewDto getMonthView(int year, int month, String federalState) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Hole Termine für den Monat
        Map<LocalDate, List<AppointmentResponseDto>> appointmentsByDate = new HashMap<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<AppointmentResponseDto> dayAppointments = appointmentService.findAppointmentsByDate(date);
            appointmentsByDate.put(date, dayAppointments);
        }

        // Hole Feiertage für den Monat
        List<Holiday> holidays = holidayService.getHolidaysInRange(startDate, endDate, federalState);
        Map<LocalDate, List<Holiday>> holidaysByDate = holidays.stream()
                .collect(Collectors.groupingBy(Holiday::getDate));

        return new CalendarViewDto(
                year,
                month,
                startDate,
                endDate,
                appointmentsByDate,
                holidaysByDate,
                federalState
        );
    }

    /**
     * Erstellt eine Wochenansicht
     */
    public CalendarViewDto getWeekView(LocalDate weekStart, String federalState) {
        LocalDate weekEnd = weekStart.plusDays(6);

        Map<LocalDate, List<AppointmentResponseDto>> appointmentsByDate = new HashMap<>();
        
        for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
            List<AppointmentResponseDto> dayAppointments = appointmentService.findAppointmentsByDate(date);
            appointmentsByDate.put(date, dayAppointments);
        }

        List<Holiday> holidays = holidayService.getHolidaysInRange(weekStart, weekEnd, federalState);
        Map<LocalDate, List<Holiday>> holidaysByDate = holidays.stream()
                .collect(Collectors.groupingBy(Holiday::getDate));

        return new CalendarViewDto(
                weekStart.getYear(),
                weekStart.getMonthValue(),
                weekStart,
                weekEnd,
                appointmentsByDate,
                holidaysByDate,
                federalState
        );
    }

    /**
     * Erstellt eine Jahresübersicht
     */
    public Map<Integer, Integer> getYearOverview(int year, Long userId) {
        Map<Integer, Integer> monthlyAppointmentCounts = new HashMap<>();
        
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            
            int appointmentCount = 0;
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                appointmentCount += appointmentService.findAppointmentsByDate(date).size();
            }
            
            monthlyAppointmentCounts.put(month, appointmentCount);
        }
        
        return monthlyAppointmentCounts;
    }

    /**
     * Findet freie Zeitslots in einem Datumsbereich
     */
    public List<LocalDate> findFreeDays(LocalDate startDate, LocalDate endDate, String federalState) {
        List<LocalDate> freeDays = new ArrayList<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Überspringe Wochenenden (optional)
            if (date.getDayOfWeek().getValue() >= 6) {
                continue;
            }
            
            // Überspringe Feiertage
            if (holidayService.isHoliday(date, federalState)) {
                continue;
            }
            
            // Überspringe Tage mit Terminen
            List<AppointmentResponseDto> appointments = appointmentService.findAppointmentsByDate(date);
            if (appointments.isEmpty()) {
                freeDays.add(date);
            }
        }
        
        return freeDays;
    }

    /**
     * Berechnet Statistiken für einen Zeitraum
     */
    public Map<String, Object> getCalendarStatistics(LocalDate startDate, LocalDate endDate, String federalState) {
        Map<String, Object> stats = new HashMap<>();
        
        int totalDays = 0;
        int workDays = 0;
        int appointmentDays = 0;
        int holidayDays = 0;
        int freeDays = 0;
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            totalDays++;
            
            // Ist es ein Wochenende?
            boolean isWeekend = date.getDayOfWeek().getValue() >= 6;
            
            // Ist es ein Feiertag?
            boolean isHoliday = holidayService.isHoliday(date, federalState);
            
            // Hat der Tag Termine?
            List<AppointmentResponseDto> appointments = appointmentService.findAppointmentsByDate(date);
            boolean hasAppointments = !appointments.isEmpty();
            
            if (!isWeekend && !isHoliday) {
                workDays++;
            }
            
            if (isHoliday) {
                holidayDays++;
            }
            
            if (hasAppointments) {
                appointmentDays++;
            }
            
            if (!hasAppointments && !isHoliday && !isWeekend) {
                freeDays++;
            }
        }
        
        stats.put("totalDays", totalDays);
        stats.put("workDays", workDays);
        stats.put("appointmentDays", appointmentDays);
        stats.put("holidayDays", holidayDays);
        stats.put("freeDays", freeDays);
        stats.put("utilizationRate", workDays > 0 ? (double) appointmentDays / workDays * 100 : 0.0);
        
        return stats;
    }
}
