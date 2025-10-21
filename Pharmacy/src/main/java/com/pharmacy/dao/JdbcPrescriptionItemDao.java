package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.PrescriptionItem;
import com.pharmacy.model.enums.DawCode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcPrescriptionItemDao implements PrescriptionItemDao {

    private final JdbcTemplate jdbc;
    public JdbcPrescriptionItemDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static PrescriptionItem mapRow(SqlRowSet rs) {
        PrescriptionItem pi = new PrescriptionItem();
        pi.setItemId                        (rs.getLong("item_id"));
        pi.setPrescriptionId                (rs.getLong("prescription_id"));
        pi.setMedicationId                  (rs.getLong("medication_id"));
        pi.setStrength                      (rs.getString("strength"));
        pi.setDose                          (rs.getString("dose"));
        BigDecimal qty = rs.getBigDecimal("quantity");
        pi.setQuantity                      (qty != null ? qty : BigDecimal.ONE);
        pi.setSig                           (rs.getString("sig"));
        String dawStr = rs.getString("daw");
        if (dawStr != null && !dawStr.isEmpty()) {
            pi.setDaw(DawCode.valueOf(dawStr));
        }
        pi.setRefills                       (rs.getInt("refills"));
        return pi;
    }

    @Override
    public PrescriptionItem getById(long itemId) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.prescription_item WHERE item_id = ?",
                        itemId);
        if (rs.next()) {
            return mapRow(rs);
        }
        throw new NotFoundException("Prescription item not found");
    }

    @Override
    public List<PrescriptionItem> listByPrescription(long prescriptionId) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.prescription_item WHERE prescription_id = ? ORDER BY item_id",
                        prescriptionId);
        List<PrescriptionItem> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public PrescriptionItem create(PrescriptionItem item) {
        String sql = "INSERT INTO pharmacy.prescription_item (prescription_id, medication_id, strength, dose, quantity, sig, daw, refills) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING item_id";
        String dawCode = item.getDaw() == null ? "0" : item.getDaw().name();
        Long id = jdbc.queryForObject(sql, Long.class,
                item.getPrescriptionId(),
                item.getMedicationId(),
                item.getStrength(),
                item.getDose(),
                item.getQuantity(),
                item.getSig(),
                dawCode,
                item.getRefills());
        item.setItemId(id);
        return item;
    }

    @Override
    public PrescriptionItem update(PrescriptionItem item) {
        String sql = "UPDATE pharmacy.prescription_item SET medication_id = ?, strength = ?, dose = ?, quantity = ?, sig = ?, daw = ?, refills = ? WHERE item_id = ?";
        int rows = jdbc.update(sql,
                item.getMedicationId(),
                item.getStrength(),
                item.getDose(),
                item.getQuantity(),
                item.getSig(),
                item.getDaw() == null ? "0" : item.getDaw().name(),
                item.getRefills(),
                item.getItemId());
        if (rows == 0) {
            throw new NotFoundException("Prescription item not found");
        }
        return item;
    }

    @Override
    public boolean deleteById(long itemId) {
        return jdbc.update("DELETE FROM pharmacy.prescription_item WHERE item_id = ?", itemId) > 0;
    }

    @Override
    public int deleteByPrescription(long prescriptionId) {
        return jdbc.update("DELETE FROM pharmacy.prescription_item WHERE prescription_id = ?", prescriptionId);
    }

}
