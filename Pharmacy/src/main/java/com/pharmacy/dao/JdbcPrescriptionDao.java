package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.Prescription;
import com.pharmacy.model.enums.RxStatus;
import com.pharmacy.model.enums.WorkflowStage;
import com.pharmacy.model.enums.WorkflowStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcPrescriptionDao implements PrescriptionDao {

    private final JdbcTemplate jdbc;
    public JdbcPrescriptionDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Prescription mapRow(SqlRowSet rs) {
        Prescription p = new Prescription();
        p.setPrescriptionId                 (rs.getLong("prescription_id"));
        p.setRxNumber                       (rs.getString("rx_number"));
        p.setPatientId                      (rs.getLong("patient_id"));
        p.setPrescriberId                   (rs.getLong("prescriber_id"));
        p.setFacilityId                     (rs.getLong("facility_id"));
        java.sql.Date d = rs.getDate("date_written");
        p.setDateWritten                    (d == null ? null : d.toLocalDate());
        p.setStatus                         (RxStatus.valueOf(rs.getString("status")));

        // Workflow fields
        String stageStr = rs.getString("current_stage");
        if (stageStr != null) {
            p.setCurrentStage(WorkflowStage.valueOf(stageStr));
        }

        String workflowStatusStr = rs.getString("workflow_status");
        if (workflowStatusStr != null) {
            p.setWorkflowStatus(WorkflowStatus.valueOf(workflowStatusStr));
        }

        java.sql.Timestamp enteredAt = rs.getTimestamp("entered_workflow_at");
        if (enteredAt != null) {
            p.setEnteredWorkflowAt(enteredAt.toLocalDateTime());
        }

        java.sql.Timestamp stageEnteredAt = rs.getTimestamp("current_stage_entered_at");
        if (stageEnteredAt != null) {
            p.setCurrentStageEnteredAt(stageEnteredAt.toLocalDateTime());
        }

        p.setAssignedToUserId               (rs.getString("assigned_to_user_id"));
        p.setNotes                          (rs.getString("notes"));
        p.setPriority                       (rs.getBoolean("priority"));

        java.sql.Timestamp completedAt = rs.getTimestamp("completed_at");
        if (completedAt != null) {
            p.setCompletedAt(completedAt.toLocalDateTime());
        }

        return p;
    }

    @Override
    public Prescription getById(long prescriptionId) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.prescription WHERE prescription_id=?",
                        prescriptionId);
        if (rs.next()) {
            return mapRow(rs);
        }
        throw new NotFoundException("Prescription not found");
    }

    @Override
    public List<Prescription> listByPatient(long patientId) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.prescription WHERE patient_id = ? ORDER BY date_written DESC",
                        patientId);
        List<Prescription> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Prescription> listByPrescriber(long prescriberId) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.prescription WHERE prescriber_id = ? ORDER BY date_written DESC",
                        prescriberId);
        List<Prescription> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Prescription> listByPatientAndStatus(long patientId, RxStatus status) {
        SqlRowSet rs = jdbc.queryForRowSet("SELECT * FROM pharmacy.prescription WHERE patient_id = ? AND status = ? ORDER BY date_written DESC",
                        patientId, status.name());
        List<Prescription> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public Prescription create(Prescription rx) {
        String sql = "INSERT INTO pharmacy.prescription (rx_number, patient_id, prescriber_id, facility_id, date_written, status, current_stage, workflow_status, entered_workflow_at, current_stage_entered_at, assigned_to_user_id, notes, priority) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING prescription_id";
        Long id = jdbc.queryForObject(sql, Long.class,
                rx.getRxNumber(),
                rx.getPatientId(),
                rx.getPrescriberId(),
                rx.getFacilityId(),
                rx.getDateWritten(),
                rx.getStatus().name(),
                rx.getCurrentStage().name(),
                rx.getWorkflowStatus().name(),
                rx.getEnteredWorkflowAt(),
                rx.getCurrentStageEnteredAt(),
                rx.getAssignedToUserId(),
                rx.getNotes(),
                rx.isPriority());
        rx.setPrescriptionId(id);
        return rx;
    }

    @Override
    public Prescription updateStatus(long prescriptionId, RxStatus status) {
        int rows = jdbc.update("UPDATE pharmacy.prescription SET status = ? WHERE prescription_id = ?",
                        status.name(), prescriptionId);
        if (rows == 0) {
            throw new NotFoundException("Prescription not found");
        }
        return getById(prescriptionId);
    }

    @Override
    public Prescription updatePrescriber(long prescriptionId, long prescriberId) {
        int rows = jdbc.update("UPDATE pharmacy.prescription SET prescriber_id = ? WHERE prescription_id = ?",
                        prescriberId, prescriptionId);
        if (rows == 0) {
            throw new NotFoundException("Prescription not found");
        }
        return getById(prescriptionId);
    }

    @Override
    public boolean deleteById(long prescriptionId) {
        return jdbc.update("DELETE FROM pharmacy.prescription WHERE prescription_id = ?", prescriptionId) > 0;
    }

}
