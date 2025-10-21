package com.pharmacy.dao;

import com.pharmacy.model.enums.Sex;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.ContactInfo;
import com.pharmacy.model.Patient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcPatientDao implements PatientDao {

    private final JdbcTemplate jdbc;
    public JdbcPatientDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Patient mapRow(SqlRowSet rs) {
        ContactInfo c = new ContactInfo(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("address"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("zip_code"),
                rs.getString("phone"),
                rs.getString("email")
        );
        Patient p = new Patient();
        p.setPatientId          (rs.getLong("patient_id"));
        p.setContact            (c);
        java.sql.Date dob = rs.getDate("date_of_birth");
        p.setDateOfBirth        (dob != null ? dob.toLocalDate() : null);
        p.setSex    (Sex.valueOf(rs.getString("sex")));
        return p;
    }

    @Override
    public Patient getById(long patientId) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.patient WHERE patient_id = ?", patientId);
        if (rs.next()) {
            return mapRow(rs);
        }
        throw new NotFoundException("Patient not found");
    }

    @Override
    public List<Patient> listAll() {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.patient ORDER BY last_name, first_name");
        List<Patient> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Patient findByPhoneDigits(String phoneDigits) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT patient_id " +
                        "FROM pharmacy.patient WHERE phone_digits = ?",
                        phoneDigits);
        if (!rs.next()) {
            throw new NotFoundException("Patient not found");
        }
        return getById(rs.getLong("patient_id"));
    }

    @Override
    public List<Patient> searchByName(String namePart) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.patient " +
                        "WHERE first_name ILIKE ? OR last_name ILIKE ? ORDER BY last_name, first_name",
                        "%" + namePart + "%", "%" + namePart + "%");
        List<Patient> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Patient create(Patient p) {
        String sql = "INSERT INTO pharmacy.patient (first_name, last_name, address, city, state, zip_code, phone, email, date_of_birth, sex) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING patient_id";
        ContactInfo c = p.getContact();
        Long id = jdbc.queryForObject(sql, Long.class,
                c.getFirstName(),
                c.getLastName(),
                c.getAddress(),
                c.getCity(),
                c.getState(),
                c.getZipCode(),
                c.getPhone(),
                c.getEmail(),
                p.getDateOfBirth(),
                p.getSex().name());
        p.setPatientId(id);
        return p;
    }

    @Override
    public Patient update(Patient p) {
        ContactInfo c = p.getContact();
        int rows = jdbc.update("UPDATE pharmacy.patient " +
                "SET first_name = ?, last_name = ?, address = ?, city = ?, state = ?, zip_code = ?, phone = ?, email = ?, date_of_birth = ?, sex = ? WHERE patient_id = ?",
                c.getFirstName(),
                c.getLastName(),
                c.getAddress(),
                c.getCity(),
                c.getState(),
                c.getZipCode(),
                c.getPhone(),
                c.getEmail(),
                p.getDateOfBirth(),
                p.getSex().name(),
                p.getPatientId());
        if (rows == 0) {
            throw new NotFoundException("Patient not found");
        }
        return p;
    }

}
