package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.Fulfillment;
import com.pharmacy.model.enums.FulfillmentStatus;
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
public class JdbcFulfillmentDao implements FulfillmentDao {

    private final JdbcTemplate jdbc;

    public JdbcFulfillmentDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Fulfillment mapRow(SqlRowSet rs) {
        Fulfillment f = new Fulfillment();
        f.setFulfillmentId(rs.getLong("fulfillment_id"));
        f.setPrescriptionId(rs.getLong("prescription_id"));
        f.setProductId(rs.getLong("product_id"));
        if (rs.getLong("lot_id") != 0) {
            f.setLotId(rs.getLong("lot_id"));
        }
        f.setQuantityDispensed(rs.getInt("quantity_dispensed"));
        if (rs.getTimestamp("filled_at") != null) {
            f.setFilledAt(rs.getTimestamp("filled_at").toInstant().atOffset(OffsetDateTime.now().getOffset()));
        }
        if (rs.getLong("filled_by_user_id") != 0) {
            f.setFilledByUserId(rs.getLong("filled_by_user_id"));
        }
        if (rs.getLong("verified_by_pharmacist_id") != 0) {
            f.setVerifiedByPharmacistId(rs.getLong("verified_by_pharmacist_id"));
        }
        String status = rs.getString("status");
        if (status != null) {
            f.setStatus(FulfillmentStatus.valueOf(status));
        }
        return f;
    }

    @Override
    public Fulfillment getById(long fulfillmentId) {
        try {
            return jdbc.queryForObject("SELECT * FROM pharmacy.fulfillment WHERE fulfillment_id = ?",
                (rs, rowNum) -> {
                    Fulfillment f = new Fulfillment();
                    f.setFulfillmentId(rs.getLong("fulfillment_id"));
                    f.setPrescriptionId(rs.getLong("prescription_id"));
                    f.setProductId(rs.getLong("product_id"));
                    if (rs.getLong("lot_id") != 0) {
                        f.setLotId(rs.getLong("lot_id"));
                    }
                    f.setQuantityDispensed(rs.getInt("quantity_dispensed"));
                    if (rs.getTimestamp("filled_at") != null) {
                        f.setFilledAt(rs.getTimestamp("filled_at").toInstant().atOffset(OffsetDateTime.now().getOffset()));
                    }
                    if (rs.getLong("filled_by_user_id") != 0) {
                        f.setFilledByUserId(rs.getLong("filled_by_user_id"));
                    }
                    if (rs.getLong("verified_by_pharmacist_id") != 0) {
                        f.setVerifiedByPharmacistId(rs.getLong("verified_by_pharmacist_id"));
                    }
                    String status = rs.getString("status");
                    if (status != null) {
                        f.setStatus(FulfillmentStatus.valueOf(status));
                    }
                    return f;
                }, fulfillmentId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Fulfillment not found");
        }
    }

    @Override
    public List<Fulfillment> getByPrescriptionId(long prescriptionId) {
        SqlRowSet rs = jdbc.queryForRowSet(
            "SELECT * FROM pharmacy.fulfillment WHERE prescription_id = ? ORDER BY filled_at NULLS LAST, fulfillment_id",
            prescriptionId);
        List<Fulfillment> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Fulfillment> getByProductId(long productId) {
        SqlRowSet rs = jdbc.queryForRowSet(
            "SELECT * FROM pharmacy.fulfillment WHERE product_id = ? ORDER BY filled_at NULLS LAST, fulfillment_id",
            productId);
        List<Fulfillment> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Fulfillment create(Fulfillment fulfillment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO pharmacy.fulfillment " +
                "(prescription_id, product_id, lot_id, quantity_dispensed, filled_at, filled_by_user_id, verified_by_pharmacist_id, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING fulfillment_id",
                Statement.RETURN_GENERATED_KEYS);

            ps.setLong(1, fulfillment.getPrescriptionId());
            if (fulfillment.getProductId() != null) {
                ps.setLong(2, fulfillment.getProductId());
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }
            if (fulfillment.getLotId() != null) {
                ps.setLong(3, fulfillment.getLotId());
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
            }
            ps.setInt(4, fulfillment.getQuantityDispensed());
            if (fulfillment.getFilledAt() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(fulfillment.getFilledAt().toLocalDateTime()));
            } else {
                ps.setNull(5, java.sql.Types.TIMESTAMP);
            }
            if (fulfillment.getFilledByUserId() != null) {
                ps.setLong(6, fulfillment.getFilledByUserId());
            } else {
                ps.setNull(6, java.sql.Types.BIGINT);
            }
            if (fulfillment.getVerifiedByPharmacistId() != null) {
                ps.setLong(7, fulfillment.getVerifiedByPharmacistId());
            } else {
                ps.setNull(7, java.sql.Types.BIGINT);
            }
            if (fulfillment.getStatus() != null) {
                ps.setString(8, fulfillment.getStatus().name());
            } else {
                ps.setNull(8, java.sql.Types.VARCHAR);
            }

            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        if (generatedId != null) {
            fulfillment.setFulfillmentId(generatedId);
        }
        return fulfillment;
    }

    @Override
    public Fulfillment update(Fulfillment fulfillment) {
        String sql = "UPDATE pharmacy.fulfillment " +
                "SET prescription_id = ?, product_id = ?, lot_id = ?, quantity_dispensed = ?, " +
                "filled_at = ?, filled_by_user_id = ?, verified_by_pharmacist_id = ?, status = ? " +
                "WHERE fulfillment_id = ?";
        int rows = jdbc.update(sql,
                fulfillment.getPrescriptionId(),
                fulfillment.getProductId(),
                fulfillment.getLotId(),
                fulfillment.getQuantityDispensed(),
                fulfillment.getFilledAt() != null ? Timestamp.valueOf(fulfillment.getFilledAt().toLocalDateTime()) : null,
                fulfillment.getFilledByUserId(),
                fulfillment.getVerifiedByPharmacistId(),
                fulfillment.getStatus() != null ? fulfillment.getStatus().name() : null,
                fulfillment.getFulfillmentId()
        );
        if (rows == 0) {
            throw new NotFoundException("fulfillment not found");
        }
        return fulfillment;
    }

    @Override
    public boolean delete(long fulfillmentId) {
        return jdbc.update("DELETE FROM pharmacy.fulfillment WHERE fulfillment_id = ?", fulfillmentId) > 0;
    }
}
