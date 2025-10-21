package com.pharmacy.controller;

import com.pharmacy.dao.MedicationDao;
import com.pharmacy.exception.DaoException;
import com.pharmacy.model.Medication;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@CrossOrigin
public class MedicationController {

    private final MedicationDao dao;

    public MedicationController(MedicationDao dao) {
        this.dao = dao;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Medication getById(@PathVariable("id") long id) {
        try{
            return dao.getById(id);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to get medication by ID", e);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<Medication> listAll() {
        try{
            return dao.listAll();
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to list medications", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Medication create(@RequestBody @Valid Medication dto) {
        try{
            return dao.create(dto);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to create medication", e);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Medication update(@PathVariable("id") long id, @RequestBody @Valid Medication dto) {
        try{
            dto.setMedicationId(id);
            return dao.update(dto);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to update medication", e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public boolean delete(@PathVariable("id") long id) {
        try{
            return dao.deleteById(id);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to delete medication", e);
        }
    }
}
