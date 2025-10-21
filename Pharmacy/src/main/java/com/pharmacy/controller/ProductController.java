package com.pharmacy.controller;

import com.pharmacy.model.Product;
import com.pharmacy.model.enums.ProductStatus;
import com.pharmacy.dao.ProductDao;
import com.pharmacy.exception.DaoException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    private final ProductDao dao;

    public ProductController(ProductDao dao) {
        this.dao = dao;
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Product getProductById(@PathVariable long productId) {
        try {
            return dao.getById(productId);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to get product by ID", e);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Product> getAllProducts() {
        try {
            return dao.listAll();
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to get all products", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Product createProduct(@Valid @RequestBody Product product) {
        try {
            return dao.create(product);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to create product", e);
        }
    }

    @GetMapping("/search/name")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Product> searchProductsByName(@RequestParam String name) {
        try {
            return dao.searchByName(name);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to search products by name", e);
        }
    }

    @GetMapping("/search/ndc")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Product> searchProductsByNdc(@RequestParam String ndc) {
        try {
            return dao.findByNdc(ndc);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to search products by NDC", e);
        }
    }

    @GetMapping("/prescription/{prescriptionId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Product> getProductsByPrescriptionId(@PathVariable long prescriptionId) {
        try {
            return dao.findByPrescriptionId(prescriptionId);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to get products by prescription ID", e);
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Product> getProductsByStatus(@PathVariable ProductStatus status) {
        try {
            return dao.findByStatus(status);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to get products by status", e);
        }
    }

    @PutMapping("/{productId}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void updateProductStatus( @PathVariable long productId, @RequestParam ProductStatus status) {
        try {
            dao.updateStatus(productId, status);
        } catch (DaoException e) {
            throw new DaoException(HttpStatus.FORBIDDEN, "Failed to update product status", e);
        }
    }
}