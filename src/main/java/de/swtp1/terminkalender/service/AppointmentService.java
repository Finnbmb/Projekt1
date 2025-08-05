package de.swtp1.terminkalender.service;

import de.swtp1.terminkalender.dto.AppointmentRequestDto;
import de.swtp1.terminkalender.dto.AppointmentResponseDto;
import de.swtp1.terminkalender.entity.Appointment;
import de.swtp1.terminkalender.repository.AppointmentRepository;
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

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Erstellt einen neuen Termin
     */
    public AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto) {
        validateAppointmentTime(requestDto);
        checkForOverlappingAppointments(requestDto);
        
        Appointment appointment = convertToEntity(requestDto);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        return convertToResponseDto(savedAppointment);
    }

    /**
     * Findet einen Termin anhand der ID
     */
    @Transactional(readOnly = true)
    public Optional<AppointmentResponseDto> findAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::convertToResponseDto);
    }

    /**
     * Findet alle Termine mit Paginierung
     */
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> findAllAppointments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return appointmentRepository.findAllByOrderByStartDateTimeAsc(pageable)
                .map(this::convertToResponseDto);
    }

    /**
     * Findet Termine in einem Zeitraum
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAppointmentsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Appointment> appointments = appointmentRepository.findAppointmentsInDateRange(startDate, endDate);
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Findet Termine an einem bestimmten Datum
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAppointmentsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Appointment> appointments = appointmentRepository.findAppointmentsByDate(startOfDay, endOfDay);
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Sucht Termine nach Text
     */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> searchAppointments(String searchTerm) {
        List<Appointment> appointments = appointmentRepository.searchAppointments(searchTerm);
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Aktualisiert einen Termin
     */
    public AppointmentResponseDto updateAppointment(Long id, AppointmentRequestDto requestDto) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Termin mit ID " + id + " nicht gefunden"));

        validateAppointmentTime(requestDto);
        checkForOverlappingAppointmentsExcludingCurrent(requestDto, id);

        updateAppointmentFromDto(existingAppointment, requestDto);
        Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
        
        return convertToResponseDto(updatedAppointment);
    }

    /**
     * Löscht einen Termin
     */
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Termin mit ID " + id + " nicht gefunden");
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

    private void checkForOverlappingAppointments(AppointmentRequestDto requestDto) {
        if (requestDto.getUserId() != null) {
            List<Appointment> overlapping = appointmentRepository.findOverlappingAppointments(
                    requestDto.getUserId(), 
                    requestDto.getStartDateTime(), 
                    requestDto.getEndDateTime()
            );
            if (!overlapping.isEmpty()) {
                throw new IllegalArgumentException("Terminüberschneidung erkannt");
            }
        }
    }

    private void checkForOverlappingAppointmentsExcludingCurrent(AppointmentRequestDto requestDto, Long excludeId) {
        if (requestDto.getUserId() != null) {
            List<Appointment> overlapping = appointmentRepository.findOverlappingAppointments(
                    requestDto.getUserId(), 
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
    }

    private Appointment convertToEntity(AppointmentRequestDto requestDto) {
        return new Appointment(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.getStartDateTime(),
                requestDto.getEndDateTime(),
                requestDto.getLocation(),
                requestDto.getUserId()
        );
    }

    private void updateAppointmentFromDto(Appointment appointment, AppointmentRequestDto requestDto) {
        appointment.setTitle(requestDto.getTitle());
        appointment.setDescription(requestDto.getDescription());
        appointment.setStartDateTime(requestDto.getStartDateTime());
        appointment.setEndDateTime(requestDto.getEndDateTime());
        appointment.setLocation(requestDto.getLocation());
        appointment.setUserId(requestDto.getUserId());
    }

    private AppointmentResponseDto convertToResponseDto(Appointment appointment) {
        return new AppointmentResponseDto(
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
    }
}
