package com.pharmacy.controller;

import com.pharmacy.model.Prescription;
import com.pharmacy.model.enums.RxStatus;
import com.pharmacy.dao.PrescriptionDao;
import com.pharmacy.exception.DaoException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin
public class PrescriptionController {

    private final PrescriptionDao dao;

    public PrescriptionController(PrescriptionDao dao) {
        this.dao = dao;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Prescription getById(@PathVariable("id") long id) {
        try {
            return dao.getById(id);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to get prescription by ID", e);
        }
    }

    @GetMapping("/by-patient/{patientId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Prescription> listByPatient(@PathVariable long patientId) {
        try {
            return dao.listByPatient(patientId);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to list prescriptions by patient", e);
        }
    }

    @GetMapping("/by-prescriber/{prescriberId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Prescription> listByPrescriber(@PathVariable long prescriberId) {
        try {
            return dao.listByPrescriber(prescriberId);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to list prescriptions by prescriber", e);
        }
    }

    @GetMapping("/by-patient/{patientId}/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Prescription> listByPatientAndStatus(@PathVariable long patientId, @PathVariable RxStatus status) {
        try {
            return dao.listByPatientAndStatus(patientId, status);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to list prescriptions by patient and status", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Prescription create(@RequestBody @Valid PrescriptionDto dto) {
        try {
            return dao.create(PrescriptionMapper.fromDto(dto));
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to create prescription", e);
        }
    }

    @PatchMapping("/{id}/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Prescription updateStatus(@PathVariable("id") long id, @PathVariable RxStatus status) {
        try {
            return dao.updateStatus(id, status);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to update prescription status", e);
        }
    }

    @PatchMapping("/{id}/prescriber/{prescriberId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Prescription updatePrescriber(@PathVariable("id") long id, @PathVariable long prescriberId) {
        try {
            return dao.updatePrescriber(id, prescriberId);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to update prescription prescriber", e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public boolean delete(@PathVariable("id") long id) {
        try {
            return dao.deleteById(id);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to delete prescription", e);
        }
    }
}
