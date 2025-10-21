package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.ContactInfo;
import com.pharmacy.model.VaccineAppointment;
import com.pharmacy.model.enums.AppointmentStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcVaccineAppointmentDao implements VaccineAppointmentDao {

    private final JdbcTemplate jdbc;
    public JdbcVaccineAppointmentDao(JdbcTemplate jdbc){ this.jdbc = jdbc; }

    private static VaccineAppointment mapRow(SqlRowSet rs){
        VaccineAppointment v = new VaccineAppointment();
        v.setAppointmentId     (rs.getLong("appointment_id"));
        v.setPatientId         (rs.getLong("patient_id"));
        Long productId = rs.getLong("vaccine_product_id");
        if (!rs.wasNull()) v.setVaccineProductId(productId);
        java.sql.Timestamp shts = rs.getTimestamp("scheduled_at");
        v.setAppointmentAt     (shts != null ? shts.toLocalDateTime() : null);
        v.setStatus            (AppointmentStatus.valueOf(rs.getString("status")));
        v.setQuestions         (rs.getString("notes"));
        try {
            String first = rs.getString("first_name");
            if (first != null) {
                ContactInfo c = new ContactInfo(
                        first,
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("zip_code"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
                v.setPatient(c);
            }
        } catch (Exception ignored) {}
        try { v.setVaccineType(rs.getString("product_name")); } catch (Exception ignored) {}
        return v;
    }

    @Override
    public VaccineAppointment getById(long appointmentId){
        String sql = "SELECT a.appointment_id, a.patient_id, a.vaccine_product_id, a.scheduled_at, a.status, a.notes " +
                "FROM pharmacy.vaccine_appointment a WHERE a.appointment_id = ?";
        SqlRowSet rs = jdbc.queryForRowSet(sql, appointmentId);
        if (rs.next()) return mapRow(rs);
        throw new NotFoundException("Vaccine appointment not found: id=" + appointmentId);
    }

    @Override
    public List<VaccineAppointment> listByPatient(long patientId){
        String sql = "SELECT a.appointment_id, a.patient_id, a.vaccine_product_id, a.scheduled_at, a.status, a.notes " +
                "FROM pharmacy.vaccine_appointment a WHERE a.patient_id = ? ORDER BY a.scheduled_at DESC";
        SqlRowSet rs = jdbc.queryForRowSet(sql, patientId);
        List<VaccineAppointment> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    @Override
    public VaccineAppointment create(VaccineAppointment v){
        String sql = "INSERT INTO pharmacy.vaccine_appointment (patient_id, vaccine_product_id, scheduled_at, status, notes) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING appointment_id";
        Long id = jdbc.queryForObject(sql, Long.class,
                v.getPatientId(),
                v.getVaccineProductId(),
                v.getAppointmentAt(),
                v.getStatus() == null ? AppointmentStatus.SCHEDULED.name() : v.getStatus().name(),
                v.getQuestions());
        v.setAppointmentId(id);
        return v;
    }

    @Override
    public VaccineAppointment update(VaccineAppointment v){
        String sql = "UPDATE pharmacy.vaccine_appointment SET patient_id = ?, vaccine_product_id = ?, scheduled_at = ?, status = ?, notes = ? " +
                "WHERE appointment_id = ?";
        int rows = jdbc.update(sql,
                v.getPatientId(),
                v.getVaccineProductId(),
                v.getAppointmentAt(),
                v.getStatus().name(),
                v.getQuestions(),
                v.getAppointmentId());
        if (rows == 0) throw new NotFoundException("Vaccine appointment not found: id=" + v.getAppointmentId());
        return v;
    }

    @Override
    public VaccineAppointment updateStatus(long appointmentId, AppointmentStatus status){
        int rows = jdbc.update("UPDATE pharmacy.vaccine_appointment SET status = ? WHERE appointment_id = ?", status.name(), appointmentId);
        if (rows == 0) throw new NotFoundException("Vaccine appointment not found: id=" + appointmentId);
        return getById(appointmentId);
    }
}
