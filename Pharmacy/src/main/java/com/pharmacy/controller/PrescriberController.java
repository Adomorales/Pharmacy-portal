package com.pharmacy.controller;

import com.pharmacy.dto.PrescriberDto;
import com.pharmacy.model.Prescriber;
import com.pharmacy.dao.PrescriberDao;
import com.pharmacy.exception.DaoException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescribers")
@CrossOrigin
public class PrescriberController {

    private final PrescriberDao dao;

    public PrescriberController(PrescriberDao dao) {
        this.dao = dao;
    }

    @GetMapping("/{prescriberId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Prescriber getById(@PathVariable("prescriberId") long prescriberId) {
        try {
            return dao.getById(prescriberId);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to get prescriber by ID", e);
        }
    }

    @GetMapping("/by-npi/{npi}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Prescriber getByNpi(@PathVariable String npi) {
        try {
            return dao.getByNpi(npi);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to get prescriber by NPI", e);
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Prescriber> searchByName(@RequestParam("q") String namePart) {
        try {
            return dao.searchByName(namePart);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to search prescribers by name", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Prescriber create(@RequestBody @Valid PrescriberDto dto) {
        try {
            Prescriber saved = dao.create(dto);
            return saved;
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to create prescriber", e);
        }
    }

    @PutMapping("/{prescriberId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Prescriber update(@PathVariable("prescriberId") long prescriberId, @RequestBody @Valid PrescriberDto dto) {
        try {
            dto.setPrescriberId(prescriberId);
            Prescriber updated = dao.update(dto);
            return updated;
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to update prescriber", e);
        }
    }
}
