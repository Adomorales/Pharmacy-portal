package com.pharmacy.dto;

import com.pharmacy.model.ContactInfo;
import com.pharmacy.model.InsuranceInfo;
import com.pharmacy.model.enums.IntakeStatus;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public class DataEntryDto {

    private long entryId; 
    @Positive
    private long prescriptionId;

    @NotNull
    private ContactInfo patient;
    @NotNull
    private ContactInfo prescriber;

    @NotBlank
    private String medicationName;
    private boolean dispenseGeneric;
    private String strength;
    private String dosageForm;
    @Positive
    private int quantity;
    @NotBlank
    private String sig;

    @NotNull
    private InsuranceInfo insurance;

    @NotNull
    @PastOrPresent
    private LocalDateTime dateReceived;
    @NotNull
    private IntakeStatus status;

    public DataEntryDto() {
        this.dateReceived = LocalDateTime.now();
        this.status = IntakeStatus.NEW;
    }

    public DataEntryDto(long prescriptionId, ContactInfo patient, ContactInfo prescriber, String medicationName, boolean dispenseGeneric, String strength, String dosageForm, int quantity, String sig, InsuranceInfo insurance, LocalDateTime dateReceived, IntakeStatus status) {
        this.prescriptionId = prescriptionId;
        this.patient = patient;
        this.prescriber = prescriber;
        this.medicationName = medicationName;
        this.dispenseGeneric = dispenseGeneric;
        this.strength = strength;
        this.dosageForm = dosageForm;
        this.quantity = quantity;
        this.sig = sig;
        this.insurance = insurance;
        this.dateReceived = (dateReceived != null) ? dateReceived : LocalDateTime.now();
        this.status = (status != null) ? status : IntakeStatus.NEW;
    }

    public long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }

    public ContactInfo getPatient() {
        return patient;
    }

    public void setPatient(ContactInfo patient) {
        this.patient = patient;
    }

    public ContactInfo getPrescriber() {
        return prescriber;
    }

    public void setPrescriber(ContactInfo prescriber) {
        this.prescriber = prescriber;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public boolean isDispenseGeneric() {
        return dispenseGeneric;
    }

    public void setDispenseGeneric(boolean dispenseGeneric) {
        this.dispenseGeneric = dispenseGeneric;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public InsuranceInfo getInsurance() { return insurance; }

    public void setInsurance(InsuranceInfo insurance) { this.insurance = insurance; }

    public LocalDateTime getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(LocalDateTime dateReceived) {
        this.dateReceived = dateReceived;
    }

    public IntakeStatus getStatus() {
        return status;
    }

    public void setStatus(IntakeStatus status) {
        this.status = status;
    }

}
