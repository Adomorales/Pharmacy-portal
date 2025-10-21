package com.pharmacy.dto;

import com.pharmacy.model.enums.CaseKind;
import com.pharmacy.model.enums.NotificationStatus;
import com.pharmacy.model.enums.FulfillmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class NotificationsDto {

    private long notificationId;
    @Positive
    private long caseId;
    @NotNull
    private CaseKind caseKind;

    @Pattern(regexp = "^$|^\\d{10}$", message = "Invalid phone")
    private String patientPhone;
    @NotBlank
    private String message;

    @NotNull
    private NotificationStatus status;

    @PastOrPresent
    private LocalDateTime createdAt;
    @PastOrPresent
    private LocalDateTime sentAt;

    // Workflow-related fields
    private FulfillmentStatus currentStage;
    private String assignedTo;
    private boolean isPriority;
    private List<String> notes;
    private LocalDateTime lastUpdated;
    private String lastUpdatedBy;

    public NotificationsDto() {
        this.status = NotificationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.currentStage = FulfillmentStatus.FILLED;
        this.isPriority = false;
        this.lastUpdated = LocalDateTime.now();
    }

    public NotificationsDto(long notificationId, long caseId, CaseKind caseKind, String patientPhone, String message, NotificationStatus status, LocalDateTime createdAt, LocalDateTime sentAt) {
        this.notificationId = notificationId;
        this.caseId = caseId;
        this.caseKind = caseKind;
        this.patientPhone = patientPhone;
        this.message = message;
        this.status = (status != null) ? status : NotificationStatus.PENDING;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.sentAt = sentAt;
        this.currentStage = FulfillmentStatus.FILLED;
        this.isPriority = false;
        this.lastUpdated = LocalDateTime.now();
    }

    public NotificationsDto(long notificationId, long caseId, CaseKind caseKind, String patientPhone, String message, NotificationStatus status, LocalDateTime createdAt, LocalDateTime sentAt, FulfillmentStatus currentStage, String assignedTo, boolean isPriority, List<String> notes, LocalDateTime lastUpdated, String lastUpdatedBy) {
        this.notificationId = notificationId;
        this.caseId = caseId;
        this.caseKind = caseKind;
        this.patientPhone = patientPhone;
        this.message = message;
        this.status = (status != null) ? status : NotificationStatus.PENDING;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.sentAt = sentAt;
        this.currentStage = (currentStage != null) ? currentStage : FulfillmentStatus.FILLED;
        this.assignedTo = assignedTo;
        this.isPriority = isPriority;
        this.notes = notes;
        this.lastUpdated = (lastUpdated != null) ? lastUpdated : LocalDateTime.now();
        this.lastUpdatedBy = lastUpdatedBy;
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

    public FulfillmentStatus getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(FulfillmentStatus currentStage) {
        this.currentStage = currentStage;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public void setPriority(boolean priority) {
        isPriority = priority;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

}
