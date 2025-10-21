package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.Medication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcMedicationDao implements MedicationDao {

    private final JdbcTemplate jdbc;
    public JdbcMedicationDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Medication mapRow(SqlRowSet rs) {
        Medication m = new Medication();
        m.setMedicationId   (rs.getLong("medication_id"));
        m.setName           (rs.getString("generic_name"));
        m.setGenericName    (rs.getString("generic_name"));
        m.setBrandName      (rs.getString("brand_name"));
        return m;
    }

    @Override
    public Medication getById(long medicationId) {
        String sql = "SELECT medication_id, generic_name, brand_name, dosage_form FROM pharmacy.medication WHERE medication_id = ?";
        SqlRowSet rs = jdbc.queryForRowSet(sql, medicationId);
        if (rs.next()) {
            return mapRow(rs);
        }
        throw new NotFoundException("Medication not found");
    }

    @Override
    public Medication getByNdc(String ndc) {
        String sql = "SELECT m.medication_id, m.generic_name, m.brand_name, m.dosage_form " +
                     "FROM pharmacy.medication m " +
                     "JOIN pharmacy.product p ON m.medication_id = p.medication_id " +
                     "WHERE p.ndc = ?";
        SqlRowSet rs = jdbc.queryForRowSet(sql, ndc);
        if (rs.next()) {
            return mapRow(rs);
        }
        throw new NotFoundException("Medication not found");
    }

    @Override
    public List<Medication> searchByName(String q) {
        String sql = "SELECT medication_id, generic_name, brand_name, dosage_form FROM pharmacy.medication WHERE generic_name ILIKE ? ORDER BY generic_name";
        SqlRowSet rs = jdbc.queryForRowSet(sql, "%" + q + "%");
        List<Medication> meds = new ArrayList<>();
        while (rs.next()) meds.add(mapRow(rs));
        return meds;
    }

    @Override
    public List<Medication> listAll() {
        String sql = "SELECT medication_id, generic_name, brand_name, dosage_form FROM pharmacy.medication ORDER BY generic_name";
        SqlRowSet rs = jdbc.queryForRowSet(sql);
        List<Medication> meds = new ArrayList<>();
        while (rs.next()) meds.add(mapRow(rs));
        return meds;
    }

    @Override
    public Medication create(Medication med) {
        String sql = "INSERT INTO pharmacy.medication (generic_name, brand_name, dosage_form) VALUES (?, ?, ?) RETURNING medication_id";
        Long id = jdbc.queryForObject(sql, Long.class, med.getGenericName(), med.getBrandName(), "Tablet");
        med.setMedicationId(id);
        return med;
    }

    @Override
    public Medication update(Medication med) {
        String sql = "UPDATE pharmacy.medication SET generic_name = ?, brand_name = ? WHERE medication_id = ?";
        int rows = jdbc.update(sql, med.getGenericName(), med.getBrandName(), med.getMedicationId());
        if (rows == 0) throw new NotFoundException("Medication not found");
        return med;
    }

    @Override
    public boolean deleteById(long medicationId) {
        String sql = "DELETE FROM pharmacy.medication WHERE medication_id = ?";
        return jdbc.update(sql, medicationId) > 0;
    }

}
