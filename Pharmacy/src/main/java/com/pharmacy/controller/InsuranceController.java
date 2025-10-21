package com.pharmacy.controller;

import com.pharmacy.dao.InsuranceDao;
import com.pharmacy.exception.DaoException;
import com.pharmacy.model.InsuranceInfo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/insurance")
@CrossOrigin
public class InsuranceController {

    private final InsuranceDao dao;

    public InsuranceController(InsuranceDao dao) {
        this.dao = dao;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<InsuranceInfo> listAll() {
        try{
            return dao.listAll();
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to list insurance", e);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public InsuranceInfo getById(@PathVariable("id") long id) {
        try{
            return dao.getById(id);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to get insurance by ID", e);
        }
    }

    @GetMapping("/by-patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<InsuranceInfo> listByPatient(@PathVariable long patientId) {
        try{
            return dao.listByPatient(patientId);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to list insurance by patient", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public InsuranceInfo create(@RequestBody @Valid InsuranceInfo dto) {
        try{
            return dao.create(dto);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to create insurance", e);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public InsuranceInfo update(@PathVariable("id") long id, @RequestBody @Valid InsuranceInfo dto) {
        try{
            dto.setInsuranceId(id);
            return dao.update(dto);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to update insurance", e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public boolean delete(@PathVariable("id") long id) {
        try{
            return dao.delete(id);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to delete insurance", e);
        }
    }
}
