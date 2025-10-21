package com.pharmacy.dto;

import com.pharmacy.model.enums.RxStatus;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public class PrescriptionDto {

    private long prescriptionId;
    @Positive
    private long patientId;
    @Positive
    private long prescriberId;
    @PastOrPresent
    private LocalDateTime writtenAt;
    @NotNull
    private RxStatus status;

    public PrescriptionDto() {
        this.writtenAt = LocalDateTime.now();
        this.status = RxStatus.ACTIVE;
    }

    public PrescriptionDto(long prescriptionId, long patientId, long prescriberId, LocalDateTime writtenAt, RxStatus status) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.prescriberId = prescriberId;
        this.writtenAt = (writtenAt != null) ? writtenAt : LocalDateTime.now();
        this.status = (status != null) ? status : RxStatus.ACTIVE;
    }

    public long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(long prescriptionId) {
        this.prescriptionId = prescriptionId;
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

    public LocalDateTime getWrittenAt() {
        return writtenAt;
    }

    public void setWrittenAt(LocalDateTime writtenAt) {
        this.writtenAt = writtenAt;
    }

    public RxStatus getStatus() {
        return status;
    }

    public void setStatus(RxStatus status) {
        this.status = status;
    }

}
