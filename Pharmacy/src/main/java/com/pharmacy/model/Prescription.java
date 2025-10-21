package com.pharmacy.model;

import com.pharmacy.model.enums.RxStatus;
import com.pharmacy.model.enums.WorkflowStage;
import com.pharmacy.model.enums.WorkflowStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Prescription {

    private long prescriptionId;
    private String rxNumber;
    private long patientId;
    private long prescriberId;
    private long facilityId;
    private LocalDate dateWritten;
    private RxStatus status;

    // Workflow tracking fields
    private WorkflowStage currentStage;
    private WorkflowStatus workflowStatus;
    private LocalDateTime enteredWorkflowAt;
    private LocalDateTime currentStageEnteredAt;
    private String assignedToUserId;
    private String notes;
    private boolean priority;
    private LocalDateTime completedAt;

    public Prescription() {
        this.dateWritten = LocalDate.now();
        this.status = RxStatus.ACTIVE;
        this.currentStage = WorkflowStage.DATA_ENTRY;
        this.workflowStatus = WorkflowStatus.DATA_ENTRY_PENDING;
        this.enteredWorkflowAt = LocalDateTime.now();
        this.currentStageEnteredAt = LocalDateTime.now();
        this.priority = false;
    }

    public Prescription(long prescriptionId, String rxNumber, long patientId, long prescriberId, long facilityId, LocalDate dateWritten, RxStatus status) {
        this.prescriptionId = prescriptionId;
        this.rxNumber = rxNumber;
        this.patientId = patientId;
        this.prescriberId = prescriberId;
        this.facilityId = facilityId;
        this.dateWritten = (dateWritten != null) ? dateWritten : LocalDate.now();
        this.status = (status != null) ? status : RxStatus.ACTIVE;
        this.currentStage = WorkflowStage.DATA_ENTRY;
        this.workflowStatus = WorkflowStatus.DATA_ENTRY_PENDING;
        this.enteredWorkflowAt = LocalDateTime.now();
        this.currentStageEnteredAt = LocalDateTime.now();
        this.priority = false;
    }

    public long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getRxNumber() {
        return rxNumber;
    }

    public void setRxNumber(String rxNumber) {
        this.rxNumber = rxNumber;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public long getPrescriberId() {
        return prescriberId;
    }

    public void setPrescriberId(long prescriberId) {
        this.prescriberId = prescriberId;
    }

    public long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    public LocalDate getDateWritten() {
        return dateWritten;
    }

    public void setDateWritten(LocalDate dateWritten) {
        this.dateWritten = dateWritten;
    }

    public RxStatus getStatus() {
        return status;
    }

    public void setStatus(RxStatus status) {
        this.status = status;
    }

    public WorkflowStage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(WorkflowStage currentStage) {
        this.currentStage = currentStage;
        this.currentStageEnteredAt = LocalDateTime.now();
    }

    public WorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(WorkflowStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
        if (workflowStatus.getStage() != this.currentStage) {
            setCurrentStage(workflowStatus.getStage());
        }
    }

    public LocalDateTime getEnteredWorkflowAt() {
        return enteredWorkflowAt;
    }

    public void setEnteredWorkflowAt(LocalDateTime enteredWorkflowAt) {
        this.enteredWorkflowAt = enteredWorkflowAt;
    }

    public LocalDateTime getCurrentStageEnteredAt() {
        return currentStageEnteredAt;
    }

    public void setCurrentStageEnteredAt(LocalDateTime currentStageEnteredAt) {
        this.currentStageEnteredAt = currentStageEnteredAt;
    }

    public String getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(String assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Prescription that = (Prescription) o;
        return prescriptionId == that.prescriptionId && patientId == that.patientId && prescriberId == that.prescriberId && facilityId == that.facilityId && Objects.equals(rxNumber, that.rxNumber) && Objects.equals(dateWritten, that.dateWritten) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(prescriptionId, rxNumber, patientId, prescriberId, facilityId, dateWritten, status);
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "prescriptionId=" + prescriptionId +
                ", rxNumber='" + rxNumber + '\'' +
                ", patientId=" + patientId +
                ", prescriberId=" + prescriberId +
                ", facilityId=" + facilityId +
                ", dateWritten=" + dateWritten +
                ", status=" + status +
                ", currentStage=" + currentStage +
                ", workflowStatus=" + workflowStatus +
                '}';
    }
}
