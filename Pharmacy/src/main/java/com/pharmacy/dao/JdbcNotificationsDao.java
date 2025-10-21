package com.pharmacy.dao;

import com.pharmacy.exception.NotFoundException;
import com.pharmacy.model.Notifications;
import com.pharmacy.model.enums.NotificationStatus;
import com.pharmacy.model.enums.CaseKind;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcNotificationsDao implements NotificationsDao {

    private final JdbcTemplate jdbc;
    public JdbcNotificationsDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static Notifications mapRow(SqlRowSet rs) {
        Notifications n = new Notifications();
        n.setNotificationId         (rs.getLong("notification_id"));
        n.setCaseId                 (rs.getLong("prescription_id"));
        n.setPatientPhone           (rs.getString("phone"));
        n.setMessage                (rs.getString("message"));
        java.sql.Timestamp cts = rs.getTimestamp("created_at");
        n.setCreatedAt        (cts != null ? cts.toLocalDateTime() : null);
        java.sql.Timestamp sts = rs.getTimestamp("sent_at");
        n.setSentAt           (sts != null ? sts.toLocalDateTime() : null);
        String st = rs.getString("status");
        if (st != null) n.setStatus(NotificationStatus.valueOf(st));
        Long pid = rs.getLong("prescription_id");
        if (!rs.wasNull()) {
            n.setCaseKind(CaseKind.PRESCRIPTION);
            n.setCaseId(pid);
        } else {
            Long patientId = rs.getLong("patient_id");
            if (!rs.wasNull()) {
                n.setCaseKind(CaseKind.PATIENT);
                n.setCaseId(patientId);
            }
        }
        return n;
    }

    @Override
    public Notifications getById(long notificationId) {
        String sql = "SELECT notification_id, patient_id, prescription_id, phone, message, created_at, sent_at, status " +
                "FROM pharmacy.patient_notification WHERE notification_id = ?";
        SqlRowSet rs = jdbc.queryForRowSet(sql, notificationId);
        if (rs.next()) return mapRow(rs);
        throw new NotFoundException("Notification not found: id=" + notificationId);
    }

    @Override
    public List<Notifications> listPending() {
        String sql = "SELECT notification_id, patient_id, prescription_id, phone, message, created_at, sent_at, status " +
                "FROM pharmacy.patient_notification WHERE status = 'PENDING' ORDER BY created_at ASC";
        SqlRowSet rs = jdbc.queryForRowSet(sql);
        List<Notifications> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    @Override
    public Notifications enqueue(Notifications n) {
        Long patientId = null;
        Long prescriptionId = null;
        if (n.getCaseKind() == CaseKind.PRESCRIPTION && n.getCaseId() > 0) {
            prescriptionId = n.getCaseId();
            patientId = jdbc.queryForObject("SELECT patient_id FROM pharmacy.prescription WHERE prescription_id = ?", Long.class, prescriptionId);
        } else if (n.getCaseKind() == CaseKind.PATIENT && n.getCaseId() > 0) {
            patientId = n.getCaseId();
        }

        String sql = "INSERT INTO pharmacy.patient_notification (patient_id, prescription_id, phone, message, status) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING notification_id";
        Long id = jdbc.queryForObject(sql, Long.class,
                patientId,
                prescriptionId,
                n.getPatientPhone(),
                n.getMessage(),
                (n.getStatus() == null ? NotificationStatus.PENDING.name() : n.getStatus().name()));
        n.setNotificationId(id);
        return n;
    }

    @Override
    public Notifications markSent(long notificationId) {
        int rows = jdbc.update("UPDATE pharmacy.patient_notification SET status = 'SENT', sent_at = now() WHERE notification_id = ?", notificationId);
        if (rows == 0) throw new NotFoundException("Notification not found: id=" + notificationId);
        return getById(notificationId);
    }

    @Override
    public Notifications updateStatus(long notificationId, NotificationStatus status) {
        int rows = jdbc.update("UPDATE pharmacy.patient_notification SET status = ? WHERE notification_id = ?", status.name(), notificationId);
        if (rows == 0) throw new NotFoundException("Notification not found: id=" + notificationId);
        return getById(notificationId);
    }
}
