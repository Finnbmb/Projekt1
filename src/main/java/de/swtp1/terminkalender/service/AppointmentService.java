package de.swtp1.terminkalender.service;

import de.swtp1.terminkalender.dto.AppointmentRequestDto;
import de.swtp1.terminkalender.dto.AppointmentResponseDto;
import de.swtp1.terminkalender.entity.Appointment;
import de.swtp1.terminkalender.entity.User;
import de.swtp1.terminkalender.repository.AppointmentRepository;
import de.swtp1.terminkalender.repository.UserRepository;
import de.swtp1.terminkalender.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service-Klasse für Appointment-Geschäftslogik
 * Implementiert alle CRUD-Operationen und Geschäftsregeln
 */
@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Holt den aktuell angemeldeten Benutzer
     */
    private User getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        System.out.println("DEBUG: getCurrentUser() - username from SecurityUtils: " + username);
        
        if (username == null) {
            System.out.println("DEBUG: No user authenticated, throwing exception");
            throw new RuntimeException("Kein Benutzer angemeldet");
        }
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("DEBUG: User with username '" + username + "' not found in database");
                    return new RuntimeException("Angemeldeter Benutzer nicht gefunden");
                });
        
        System.out.println("DEBUG: Found user: " + user.getUsername() + " with ID: " + user.getId());
        return user;
    }

    /**
     * Erstellt einen neuen Termin
     */
    public AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto) {
        User currentUser = getCurrentUser();
        
        validateAppointmentTime(requestDto);
        checkForOverlappingAppointments(requestDto, currentUser.getId());
        
        Appointment appointment = convertToEntity(requestDto);
        appointment.setUserId(currentUser.getId()); // Setze den aktuellen Benutzer
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToResponseDto(savedAppointment);
    }

    /**
     * Findet einen Termin anhand der ID
     */
    @Transactional(readOnly = true)
    public Optional<AppointmentResponseDto> findAppointmentById(Long id) {
        User currentUser = getCurrentUser();
        return appointmentRepository.findById(id)
                .filter(appointment -> appointment.getUserId().equals(currentUser.getId()))
                .map(this::convertToResponseDto);
    }

    /**
     * Findet alle Termine mit Paginierung
     */
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> findAllAppointments(int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return appointmentRepository.findByUserIdOrderByStartDateTimeAsc(currentUser.getId(), pageable)
                .map(this::convertToResponseDto);
    }

    /**
     * Findet Termine in einem Zeitraum
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAppointmentsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        User currentUser = getCurrentUser();
        List<Appointment> appointments = appointmentRepository.findUserAppointmentsInDateRange(currentUser.getId(), startDate, endDate);
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Findet Termine an einem bestimmten Datum
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAppointmentsByDate(LocalDate date) {
        User currentUser = getCurrentUser();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Appointment> appointments = appointmentRepository.findUserAppointmentsByDate(currentUser.getId(), startOfDay, endOfDay);
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Sucht Termine nach Text
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> searchAppointments(String searchTerm) {
        User currentUser = getCurrentUser();
        List<Appointment> appointments = appointmentRepository.searchUserAppointments(currentUser.getId(), searchTerm);
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Findet Termine in einem Datumsbereich
     */
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> findAppointmentsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        User currentUser = getCurrentUser();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        Page<Appointment> appointments = appointmentRepository.findByUserIdAndStartDateTimeBetween(currentUser.getId(), startDateTime, endDateTime, pageable);
        return appointments.map(this::convertToResponseDto);
    }

    /**
     * Aktualisiert einen Termin
     */
    public AppointmentResponseDto updateAppointment(Long id, AppointmentRequestDto requestDto) {
        User currentUser = getCurrentUser();
        System.out.println("DEBUG: updateAppointment - Benutzer: " + currentUser.getUsername() + " (ID: " + currentUser.getId() + ")");
        System.out.println("DEBUG: updateAppointment - Termin ID: " + id);
        
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Termin mit ID " + id + " nicht gefunden"));

        System.out.println("DEBUG: updateAppointment - Gefundener Termin User-ID: " + existingAppointment.getUserId());
        System.out.println("DEBUG: updateAppointment - Request DTO User-ID: " + requestDto.getUserId());

        // Prüfe, ob der Termin dem aktuellen Benutzer gehört
        if (!existingAppointment.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("Nicht autorisiert, diesen Termin zu bearbeiten");
        }

        validateAppointmentTime(requestDto);
        checkForOverlappingAppointmentsExcludingCurrent(requestDto, id);

        updateAppointmentFromDto(existingAppointment, requestDto);
        System.out.println("DEBUG: updateAppointment - User-ID nach Update: " + existingAppointment.getUserId());
        
        Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
        System.out.println("DEBUG: updateAppointment - Gespeicherte User-ID: " + updatedAppointment.getUserId());
        
        return convertToResponseDto(updatedAppointment);
    }

    /**
     * Löscht einen Termin
     */
    public void deleteAppointment(Long id) {
        User currentUser = getCurrentUser();
        
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Termin mit ID " + id + " nicht gefunden"));
        
        // Prüfe, ob der Termin dem aktuellen Benutzer gehört
        if (!appointment.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("Nicht autorisiert, diesen Termin zu löschen");
        }
        
        appointmentRepository.deleteById(id);
    }

    // Private Hilfsmethoden

    private void validateAppointmentTime(AppointmentRequestDto requestDto) {
        if (requestDto.getStartDateTime().isAfter(requestDto.getEndDateTime())) {
            throw new IllegalArgumentException("Startzeit muss vor der Endzeit liegen");
        }
        if (requestDto.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Termine können nicht in der Vergangenheit liegen");
        }
    }

    private void checkForOverlappingAppointments(AppointmentRequestDto requestDto, Long userId) {
        List<Appointment> overlapping = appointmentRepository.findOverlappingAppointments(
                userId, 
                requestDto.getStartDateTime(), 
                requestDto.getEndDateTime()
        );
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Terminüberschneidung erkannt");
        }
    }

    private void checkForOverlappingAppointmentsExcludingCurrent(AppointmentRequestDto requestDto, Long excludeId) {
        User currentUser = getCurrentUser();
        List<Appointment> overlapping = appointmentRepository.findOverlappingAppointments(
                currentUser.getId(), 
                requestDto.getStartDateTime(), 
                requestDto.getEndDateTime()
        );
        overlapping = overlapping.stream()
                .filter(appointment -> !appointment.getId().equals(excludeId))
                .collect(Collectors.toList());
        
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Terminüberschneidung erkannt");
        }
    }

    private Appointment convertToEntity(AppointmentRequestDto requestDto) {
        System.out.println("DEBUG: convertToEntity - reminderMinutes aus DTO: " + requestDto.getReminderMinutes());
        
        Appointment appointment = new Appointment(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.getStartDateTime(),
                requestDto.getEndDateTime(),
                requestDto.getLocation(),
                requestDto.getUserId()
        );
        
        // Setze erweiterte Felder
        appointment.setPriority(requestDto.getPriority());
        appointment.setReminderMinutes(requestDto.getReminderMinutes());
        appointment.setRecurring(requestDto.isRecurring());
        appointment.setRecurrenceType(requestDto.getRecurrenceType());
        appointment.setCategory(requestDto.getCategory());
        appointment.setColorCode(requestDto.getColorCode());
        
        System.out.println("DEBUG: convertToEntity - reminderMinutes gesetzt auf: " + appointment.getReminderMinutes());
        return appointment;
    }

    private void updateAppointmentFromDto(Appointment appointment, AppointmentRequestDto requestDto) {
        appointment.setTitle(requestDto.getTitle());
        appointment.setDescription(requestDto.getDescription());
        appointment.setStartDateTime(requestDto.getStartDateTime());
        appointment.setEndDateTime(requestDto.getEndDateTime());
        appointment.setLocation(requestDto.getLocation());
        // WICHTIG: User-ID wird beim Update NICHT geändert - sie bleibt die ursprüngliche
        // appointment.setUserId(requestDto.getUserId()); // ENTFERNT - User-ID bleibt unverändert
        
        // Update erweiterte Felder
        appointment.setPriority(requestDto.getPriority());
        appointment.setReminderMinutes(requestDto.getReminderMinutes());
        appointment.setRecurring(requestDto.isRecurring());
        appointment.setRecurrenceType(requestDto.getRecurrenceType());
        appointment.setCategory(requestDto.getCategory());
        appointment.setColorCode(requestDto.getColorCode());
    }

    private AppointmentResponseDto convertToResponseDto(Appointment appointment) {
        AppointmentResponseDto dto = new AppointmentResponseDto(
                appointment.getId(),
                appointment.getTitle(),
                appointment.getDescription(),
                appointment.getStartDateTime(),
                appointment.getEndDateTime(),
                appointment.getLocation(),
                appointment.getUserId(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
        
        // Setze erweiterte Felder
        dto.setPriority(appointment.getPriority());
        dto.setReminderMinutes(appointment.getReminderMinutes());
        dto.setRecurring(appointment.isRecurring());
        dto.setRecurrenceType(appointment.getRecurrenceType());
        dto.setCategory(appointment.getCategory());
        dto.setColorCode(appointment.getColorCode());
        
        return dto;
    }
    
    /**
     * Debug-Methoden ohne Authentifizierung für Testing
     */
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAllAppointmentsForTesting(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(this::convertToResponseDto);
    }
    
    @Transactional(readOnly = true)
    public long countAllAppointments() {
        return appointmentRepository.count();
    }
    
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAllAppointmentsInDateRangeForTesting(LocalDateTime startDate, LocalDateTime endDate) {
        List<Appointment> appointments = appointmentRepository.findAppointmentsInDateRange(startDate, endDate);
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Findet Termine, die eine Erinnerung benötigen
     * (Termine, die in den nächsten X Minuten starten und eine Erinnerung haben)
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getUpcomingReminders() {
        User currentUser = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        System.out.println("DEBUG: getUpcomingReminders() - Current time: " + now);
        
        // Suche Termine, die in den nächsten 60 Minuten starten
        LocalDateTime endTime = now.plusMinutes(60);
        System.out.println("DEBUG: Looking for appointments between " + now + " and " + endTime);
        
        List<Appointment> allAppointments = appointmentRepository.findUserAppointmentsForReminders(
                currentUser.getId(), now, endTime);
        System.out.println("DEBUG: Found " + allAppointments.size() + " appointments in range");
        
        // Debug: Zeige alle gefundenen Termine
        for (Appointment app : allAppointments) {
            System.out.println("DEBUG: Appointment: " + app.getTitle() + 
                             ", Start: " + app.getStartDateTime() + 
                             ", ReminderMinutes: " + app.getReminderMinutes());
        }
        
        // Filtere nur Termine, die eine Erinnerung haben und diese fällig ist
        return allAppointments.stream()
                .filter(appointment -> {
                    if (appointment.getReminderMinutes() == null || appointment.getReminderMinutes() <= 0) {
                        System.out.println("DEBUG: Appointment '" + appointment.getTitle() + "' has no reminder set");
                        return false; // Keine Erinnerung gesetzt
                    }
                    
                    LocalDateTime reminderTime = appointment.getStartDateTime().minusMinutes(appointment.getReminderMinutes());
                    System.out.println("DEBUG: Appointment '" + appointment.getTitle() + 
                                     "' - Reminder time: " + reminderTime + 
                                     ", Current time: " + now + 
                                     ", Is after reminder time: " + now.isAfter(reminderTime) +
                                     ", Is before reminder+1min: " + now.isBefore(reminderTime.plusMinutes(1)));
                    
                    // Erinnerung ist fällig, wenn jetzt höchstens 1 Minute nach der Erinnerungszeit liegt
                    // (Toleranz wegen 30-Sekunden-Prüfintervall)
                    return now.isAfter(reminderTime) && now.isBefore(reminderTime.plusMinutes(1));
                })
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
}
