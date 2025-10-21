package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.Product;
import com.pharmacy.model.enums.ProductStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcProductDao implements ProductDao {

    private final JdbcTemplate jdbc;

    public JdbcProductDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Product mapRow(SqlRowSet rs) {
        Product p = new Product();
        p.setProductId(rs.getLong("product_id"));
        p.setNdc(rs.getString("ndc"));
        p.setName(rs.getString("name"));
        p.setGenericName(rs.getString("generic_name"));
        p.setManufacturer(rs.getString("manufacturer"));
        p.setDosageForm(rs.getString("dosage_form"));
        p.setStrength(rs.getString("strength"));
        p.setRx(rs.getBoolean("is_rx"));
        p.setVaccine(rs.getBoolean("is_vaccine"));
        p.setUnitPrice(rs.getDouble("unit_price"));
        p.setStockQty(rs.getInt("stock_qty"));
        p.setMedicationId(rs.getLong("medication_id"));
        if (rs.getTimestamp("created_at") != null) {
            p.setCreatedAt(rs.getTimestamp("created_at").toInstant().atOffset(OffsetDateTime.now().getOffset()));
        }
        if (rs.getTimestamp("updated_at") != null) {
            p.setUpdatedAt(rs.getTimestamp("updated_at").toInstant().atOffset(OffsetDateTime.now().getOffset()));
        }
        return p;
    }

    @Override
    public Product getById(long productId) {
        try {
            return jdbc.queryForObject("SELECT * FROM pharmacy.product WHERE product_id = ?",
                (rs, rowNum) -> {
                    Product p = new Product();
                    p.setProductId(rs.getLong("product_id"));
                    p.setNdc(rs.getString("ndc"));
                    p.setName(rs.getString("name"));
                    p.setGenericName(rs.getString("generic_name"));
                    p.setManufacturer(rs.getString("manufacturer"));
                    p.setDosageForm(rs.getString("dosage_form"));
                    p.setStrength(rs.getString("strength"));
                    p.setRx(rs.getBoolean("is_rx"));
                    p.setVaccine(rs.getBoolean("is_vaccine"));
                    p.setUnitPrice(rs.getDouble("unit_price"));
                    p.setStockQty(rs.getInt("stock_qty"));
                    p.setMedicationId(rs.getLong("medication_id"));
                    if (rs.getTimestamp("created_at") != null) {
                        p.setCreatedAt(rs.getTimestamp("created_at").toInstant().atOffset(OffsetDateTime.now().getOffset()));
                    }
                    if (rs.getTimestamp("updated_at") != null) {
                        p.setUpdatedAt(rs.getTimestamp("updated_at").toInstant().atOffset(OffsetDateTime.now().getOffset()));
                    }
                    return p;
                }, productId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Product not found");
        }
    }

    @Override
    public List<Product> getAll() {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.product ORDER BY name");
        List<Product> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Product> listAll() {
        return getAll();
    }

    @Override
    public Product create(Product product) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO pharmacy.product " +
                "(ndc, name, generic_name, manufacturer, dosage_form, strength, is_rx, is_vaccine, unit_price, stock_qty, medication_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING product_id",
                Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, product.getNdc());
            ps.setString(2, product.getName());
            ps.setString(3, product.getGenericName());
            ps.setString(4, product.getManufacturer());
            ps.setString(5, product.getDosageForm());
            ps.setString(6, product.getStrength());
            ps.setBoolean(7, product.isRx());
            ps.setBoolean(8, product.isVaccine());
            ps.setDouble(9, product.getUnitPrice());
            ps.setInt(10, product.getStockQty());
            if (product.getMedicationId() != null) {
                ps.setLong(11, product.getMedicationId());
            } else {
                ps.setNull(11, java.sql.Types.BIGINT);
            }

            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        if (generatedId != null) {
            product.setProductId(generatedId);
        }
        return product;
    }

    @Override
    public Product update(Product product) {
        String sql = "UPDATE pharmacy.product " +
                "SET ndc = ?, name = ?, generic_name = ?, manufacturer = ?, dosage_form = ?, strength = ?, " +
                "is_rx = ?, is_vaccine = ?, unit_price = ?, stock_qty = ?, medication_id = ?, updated_at = now() " +
                "WHERE product_id = ?";
        int rows = jdbc.update(sql,
                product.getNdc(),
                product.getName(),
                product.getGenericName(),
                product.getManufacturer(),
                product.getDosageForm(),
                product.getStrength(),
                product.isRx(),
                product.isVaccine(),
                product.getUnitPrice(),
                product.getStockQty(),
                product.getMedicationId(),
                product.getProductId()
        );
        if (rows == 0) {
            throw new NotFoundException("Product not found");
        }
        return product;
    }

    @Override
    public boolean delete(long productId) {
        return jdbc.update("DELETE FROM pharmacy.product WHERE product_id = ?", productId) > 0;
    }

    @Override
    public List<Product> findByNdc(String ndc) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.product WHERE ndc = ? ORDER BY name", ndc);
        List<Product> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Product> findByName(String name) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.product WHERE name ILIKE ? ORDER BY name", "%" + name + "%");
        List<Product> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Product> findByPrescriptionId(long prescriptionId) {
        String sql = "SELECT DISTINCT p.* FROM pharmacy.product p " +
                    "INNER JOIN pharmacy.fulfillment f ON p.product_id = f.product_id " +
                    "WHERE f.prescription_id = ? ORDER BY p.name";
        SqlRowSet rs = jdbc.queryForRowSet(sql, prescriptionId);
        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            products.add(mapRow(rs));
        }
        return products;
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        String sql = "SELECT DISTINCT p.* FROM pharmacy.product p " +
                    "INNER JOIN pharmacy.fulfillment f ON p.product_id = f.product_id " +
                    "WHERE f.status = ?::pharmacy.fulfillment_status ORDER BY p.name";
        SqlRowSet rs = jdbc.queryForRowSet(sql, status.name());
        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            products.add(mapRow(rs));
        }
        return products;
    }
}
