package com.pharmacy.controller;

import com.pharmacy.dao.PatientDao;
import com.pharmacy.exception.DaoException;
import com.pharmacy.model.Patient;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin
public class PatientController {

    private final PatientDao patientDao;

    public PatientController(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Patient> listAll() {
        try {
            List<Patient> patients = patientDao.listAll();
            return patients;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to list patients", e);
        }
    }

    @GetMapping("/{patientId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Patient getPatientById(@PathVariable long patientId) {
        try {
            return patientDao.getById(patientId);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to get patient by ID", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Patient createPatient(@Valid @RequestBody Patient patient) {
        try {
            return patientDao.create(patient);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to create patient", e);
        }
    }

    @PutMapping("/{patientId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Patient updatePatient( @PathVariable long patientId, @Valid @RequestBody Patient patient) {
        try {
            return patientDao.update(patientId, patient);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to update patient", e);
        }
    }

    @DeleteMapping("/{patientId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public boolean deletePatient(@PathVariable long patientId) {
        try {
            patientDao.deletePatient(patientId);
            return true;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to delete patient", e);
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Patient> searchPatients(@RequestParam String query) {
        try {
            List<Patient> patients = patientDao.searchByName(query);
            return patients;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to search patients", e);
        }
    }

    @PostMapping("/lookup")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Patient lookupPatientByContact(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phone) {
        try {
            Patient patient = patientDao.findByPhoneDigits(phone);
            return patient;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to lookup patient by contact", e);
        }
    }
}