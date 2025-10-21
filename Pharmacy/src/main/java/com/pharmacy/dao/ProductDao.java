package com.pharmacy.dao;

import com.pharmacy.model.Product;
import com.pharmacy.model.enums.ProductStatus;

import java.util.List;

public interface ProductDao {

    Product getById(long productId);

    List<Product> listAll();

    Product create(Product product);

    Product update(Product product);

    boolean delete(long productId);

    List<Product> findByNdc(String ndc);

    List<Product> findByName(String name);

    List<Product> findByPrescriptionId(long prescriptionId);

    List<Product> findByStatus(ProductStatus status);

}
