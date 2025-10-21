package com.pharmacy.model;

import java.time.LocalDateTime;
import java.util.Objects;


import com.pharmacy.model.enums.IntakeStatus;

public class DataEntry implements Insured, PatientCase {

    private long entryId;
    private long prescriptionId;

    private ContactInfo patient;
    private ContactInfo prescriber;

    private String medicationName;
    private boolean dispenseGeneric;
    private String strength;
    private String dosageForm;
    private int quantity;
    private String sig;

    private InsuranceInfo insurance;

    private LocalDateTime dateReceived;
    private IntakeStatus status;

    public DataEntry() {
        this.dateReceived = LocalDateTime.now();
        this.status = IntakeStatus.NEW;
    }

    public DataEntry(long prescriptionId, ContactInfo patient, ContactInfo prescriber, String medicationName, boolean dispenseGeneric, String strength, String dosageForm, int quantity, String sig, InsuranceInfo insurance, LocalDateTime dateReceived, IntakeStatus status) {
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

    @Override
    public InsuranceInfo getInsurance() {
        return insurance;
    }

    @Override
    public void setInsurance(InsuranceInfo insurance) {
        this.insurance = insurance;
    }

    @Override
    public long getCaseId() {
        return prescriptionId;
    }

    @Override
    public String getPatientFirstName() {
        return (patient != null) ? patient.getFirstName() : null;
    }

    @Override
    public String getPatientLastName() {
        return (patient != null) ? patient.getLastName() : null;
    }

    @Override
    public String getPatientPhone() {
        return (patient != null) ? patient.getPhone() : null;
    }

    @Override
    public ContactInfo getPatientContact() {
        return patient;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DataEntry dataEntry = (DataEntry) o;
        return Objects.equals(prescriptionId, dataEntry.prescriptionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prescriptionId);
    }

    @Override
    public String toString() {
        return "DataEntry{" +
                ", prescriptionId =" + prescriptionId +
                ", patientName ='" + patient + '\'' +
                ", medicationName ='" + medicationName + '\'' +
                ", quantity =" + quantity +
                ", status =" + status +
                '}';
    }

}
