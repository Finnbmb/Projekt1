package de.swtp1.terminkalender.dto;

import de.swtp1.terminkalender.entity.Holiday;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO für Kalenderansichten
 * Enthält Termine und Feiertage für einen bestimmten Zeitraum
 */
public class CalendarViewDto {
    
    private int year;
    private int month;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<LocalDate, List<AppointmentResponseDto>> appointmentsByDate;
    private Map<LocalDate, List<Holiday>> holidaysByDate;
    private String federalState;

    // Konstruktoren
    public CalendarViewDto() {
    }

    public CalendarViewDto(int year, int month, LocalDate startDate, LocalDate endDate, 
                          Map<LocalDate, List<AppointmentResponseDto>> appointmentsByDate,
                          Map<LocalDate, List<Holiday>> holidaysByDate, String federalState) {
        this.year = year;
        this.month = month;
        this.startDate = startDate;
        this.endDate = endDate;
        this.appointmentsByDate = appointmentsByDate;
        this.holidaysByDate = holidaysByDate;
        this.federalState = federalState;
    }

    // Getter und Setter
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Map<LocalDate, List<AppointmentResponseDto>> getAppointmentsByDate() {
        return appointmentsByDate;
    }

    public void setAppointmentsByDate(Map<LocalDate, List<AppointmentResponseDto>> appointmentsByDate) {
        this.appointmentsByDate = appointmentsByDate;
    }

    public Map<LocalDate, List<Holiday>> getHolidaysByDate() {
        return holidaysByDate;
    }

    public void setHolidaysByDate(Map<LocalDate, List<Holiday>> holidaysByDate) {
        this.holidaysByDate = holidaysByDate;
    }

    public String getFederalState() {
        return federalState;
    }

    public void setFederalState(String federalState) {
        this.federalState = federalState;
    }

    /**
     * Hilfsmethode: Prüft ob ein Datum Termine hat
     */
    public boolean hasAppointments(LocalDate date) {
        List<AppointmentResponseDto> appointments = appointmentsByDate.get(date);
        return appointments != null && !appointments.isEmpty();
    }

    /**
     * Hilfsmethode: Prüft ob ein Datum ein Feiertag ist
     */
    public boolean isHoliday(LocalDate date) {
        List<Holiday> holidays = holidaysByDate.get(date);
        return holidays != null && !holidays.isEmpty();
    }

    /**
     * Hilfsmethode: Anzahl der Termine an einem Tag
     */
    public int getAppointmentCount(LocalDate date) {
        List<AppointmentResponseDto> appointments = appointmentsByDate.get(date);
        return appointments != null ? appointments.size() : 0;
    }

    /**
     * Hilfsmethode: Feiertage für ein bestimmtes Datum
     */
    public List<Holiday> getHolidaysForDate(LocalDate date) {
        return holidaysByDate.get(date);
    }

    /**
     * Hilfsmethode: Termine für ein bestimmtes Datum
     */
    public List<AppointmentResponseDto> getAppointmentsForDate(LocalDate date) {
        return appointmentsByDate.get(date);
    }
}
