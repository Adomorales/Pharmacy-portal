package com.pharmacy.controller;

import com.pharmacy.model.VaccineAppointment;
import com.pharmacy.model.enums.AppointmentStatus;
import com.pharmacy.dao.VaccineAppointmentDao;
import com.pharmacy.exception.DaoException;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vaccine-appointments")
@CrossOrigin
public class VaccineAppointmentController {

    private final VaccineAppointmentDao dao;

    public VaccineAppointmentController(VaccineAppointmentDao dao) {
        this.dao = dao;
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public VaccineAppointment getAppointmentById(@PathVariable long appointmentId) {
        try {
            return dao.getById(appointmentId);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to get appointment by ID", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public VaccineAppointment createAppointment(@Valid @RequestBody VaccineAppointment appointmentDto) {
        try {
            return dao.create(appointmentDto);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to create appointment", e);
        }
    }

    @PutMapping("/{appointmentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public VaccineAppointment updateAppointment( @PathVariable long appointmentId, @Valid @RequestBody VaccineAppointment appointmentDto) {
        try {
            return dao.update(appointmentDto);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to update appointment", e);
        }
    }

    @PutMapping("/{appointmentId}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public VaccineAppointment updateAppointmentStatus( @PathVariable long appointmentId, @RequestParam AppointmentStatus status) {
        try {
            return dao.updateStatus(appointmentId, status);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to update appointment status", e);
        }
    }

    @DeleteMapping("/{appointmentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public boolean deleteAppointment(@PathVariable long appointmentId) {
        try {
            return dao.delete(appointmentId);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to delete appointment", e);
        }
    }

    @PostMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public VaccineAppointment cancelAppointment( @PathVariable long appointmentId, @RequestParam(required = false) String reason) {
        dao.updateStatus(appointmentId, AppointmentStatus.CANCELLED);
        return dao.getById(appointmentId);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<VaccineAppointment> getAppointmentsByPatientId(@PathVariable long patientId) {
        return dao.listByPatient(patientId);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<VaccineAppointment> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        try {
            return dao.listByStatus(status);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to get appointments by status", e);
        }
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<VaccineAppointment> getUpcomingAppointments() {
        try {
            return dao.listByDateRange(LocalDateTime.now(), LocalDateTime.now().plusMonths(1));
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to get upcoming appointments", e);
        }
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<VaccineAppointment> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            return dao.listByDateRange(startDate, endDate);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to get appointments by date range", e);
        }
    }
}