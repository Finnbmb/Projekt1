package de.swtp1.terminkalender.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.swtp1.terminkalender.entity.Appointment;

import java.time.LocalDateTime;

/**
 * DTO für Appointment-Responses
 * Entspricht der API-Spezifikation
 */
public class AppointmentResponseDto {

    private Long id;
    private String title;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDateTime;

    private String location;
    private Long userId;

    // Neue Felder für erweiterte Funktionen
    private Appointment.Priority priority;
    private Integer reminderMinutes;
    private boolean isRecurring;
    private Appointment.RecurrenceType recurrenceType;
    private String category;
    private String colorCode;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Konstruktoren
    public AppointmentResponseDto() {
    }

    public AppointmentResponseDto(Long id, String title, String description, 
                                 LocalDateTime startDateTime, LocalDateTime endDateTime, 
                                 String location, Long userId, LocalDateTime createdAt, 
                                 LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "AppointmentResponseDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", location='" + location + '\'' +
                ", userId=" + userId +
                '}';
    }
}
