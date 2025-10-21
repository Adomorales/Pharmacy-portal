package com.pharmacy.controller;

import com.pharmacy.dao.DataReviewDao;
import com.pharmacy.exception.DaoException;
import com.pharmacy.model.DataReview;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/data-reviews")
@CrossOrigin
public class DataReviewController {

    private final DataReviewDao dao;

    public DataReviewController(DataReviewDao dao) {
        this.dao = dao;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DataReview getById(@PathVariable("id") long id) {
        try{
            return dao.getById(id);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to get data review by ID", e);
        }
    }

    @GetMapping("/by-entry/{entryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DataReview> listByDataEntry(@PathVariable long entryId) {
        try{
            return dao.listByDataEntry(entryId);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to list data reviews by entry ID", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DataReview create(@RequestBody @Valid DataReview dto) {
        try{
            return dao.create(dto);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to create data review", e);
        }
    }
}
