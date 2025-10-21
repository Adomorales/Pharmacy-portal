package com.pharmacy.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.pharmacy.model.enums.NotificationStatus;
import com.pharmacy.model.enums.CaseKind;

public class Notifications {

    private long notificationId;
    private long caseId;
    private CaseKind caseKind;

    private String patientPhone;
    private String message;

    private NotificationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    public Notifications() {
        this.status = NotificationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Notifications(long notificationId, long caseId, CaseKind caseKind, String patientPhone, String message, NotificationStatus status, LocalDateTime createdAt, LocalDateTime sentAt) {
        this.notificationId = notificationId;
        this.caseId = caseId;
        this.caseKind = caseKind;
        this.patientPhone = patientPhone;
        this.message = message;
        this.status = (status != null) ? status : NotificationStatus.PENDING;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.sentAt = sentAt;
    }

    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public long getCaseId() {
        return caseId;
    }

    public void setCaseId(long caseId) {
        this.caseId = caseId;
    }

    public CaseKind getCaseKind() {
        return caseKind;
    }

    public void setCaseKind(CaseKind caseKind) {
        this.caseKind = caseKind;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Notifications that = (Notifications) o;
        return Objects.equals(notificationId, that.notificationId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(notificationId);
    }

    @Override
    public String toString() {
        return "Notifications{" +
                "notificationId=" + notificationId +
                ", caseId=" + caseId +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", sentAt=" + sentAt +
                ", patientPhone='" + patientPhone + '\'' +
                '}';
    }

}