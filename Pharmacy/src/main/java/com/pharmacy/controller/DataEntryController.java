package com.pharmacy.controller;

import com.pharmacy.dao.DataEntryDao;
import com.pharmacy.exception.DaoException;
import com.pharmacy.model.DataEntry;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/data-entry")
@CrossOrigin
public class DataEntryController {

    private final DataEntryDao dao;

    public DataEntryController(DataEntryDao dao) {
        this.dao = dao;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataEntry getById(@PathVariable("id") long id) {
        try {
            return dao.getById(id);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to get data entry by ID", e);
        }
    }

    @GetMapping("/queue")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<DataEntry> listQueue() {
        try {
            return dao.listQueuePage();
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to list data entries in queue", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataEntry create(@RequestBody @Valid DataEntry dto) {
        try {
            return dao.create(dto);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to create data entry", e);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataEntry update(@PathVariable("id") long id, @RequestBody @Valid DataEntry dto) {
        try {
            dto.setEntryId(id);
            return dao.update(dto);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to update data entry", e);
        }
    }
}
