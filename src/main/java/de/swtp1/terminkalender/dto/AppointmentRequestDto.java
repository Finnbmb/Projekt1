package de.swtp1.terminkalender.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.swtp1.terminkalender.entity.Appointment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO für Appointment-Requests
 * Entspricht der API-Spezifikation
 */
public class AppointmentRequestDto {

    @NotBlank(message = "Titel ist erforderlich")
    private String title;

    private String description;

    @NotNull(message = "Startdatum/-zeit ist erforderlich")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDateTime;

    @NotNull(message = "Enddatum/-zeit ist erforderlich")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDateTime;

    private String location;

    private Long userId;

    // Neue Felder für erweiterte Funktionen
    private Appointment.Priority priority = Appointment.Priority.MEDIUM;
    
    private Integer reminderMinutes = 15;
    
    private boolean isRecurring = false;
    
    private Appointment.RecurrenceType recurrenceType;
    
    private String category;
    
    private String colorCode = "#007bff";

    // Konstruktoren
    public AppointmentRequestDto() {
    }

    public AppointmentRequestDto(String title, String description, LocalDateTime startDateTime, 
                                LocalDateTime endDateTime, String location, Long userId) {
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.userId = userId;
    }

    // Getter und Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Getter und Setter für neue Felder
    public Appointment.Priority getPriority() {
        return priority;
    }

    public void setPriority(Appointment.Priority priority) {
        this.priority = priority;
    }

    public Integer getReminderMinutes() {
        return reminderMinutes;
    }

    public void setReminderMinutes(Integer reminderMinutes) {
        this.reminderMinutes = reminderMinutes;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public Appointment.RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(Appointment.RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    @Override
    public String toString() {
        return "AppointmentRequestDto{" +
                "title='" + title + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", location='" + location + '\'' +
                ", priority=" + priority +
                ", reminderMinutes=" + reminderMinutes +
                ", category='" + category + '\'' +
                '}';
    }
}
