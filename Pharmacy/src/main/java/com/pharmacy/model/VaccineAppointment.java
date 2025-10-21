package com.pharmacy.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.pharmacy.model.enums.AppointmentStatus;

public class VaccineAppointment implements Insured, PatientCase {

    private long appointmentId;
    private long patientId;
    private Long vaccineProductId;

    private ContactInfo patient;

    private String vaccineType;
    private LocalDateTime appointmentAt;
    private String questions;

    private AppointmentStatus status;

    private InsuranceInfo insurance;

    public VaccineAppointment() {
        this.status = AppointmentStatus.SCHEDULED;
    }

    public VaccineAppointment(long appointmentId, ContactInfo patient, String vaccineType, LocalDateTime appointmentAt, String questions, AppointmentStatus status, InsuranceInfo insurance) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.vaccineType = vaccineType;
        this.appointmentAt = appointmentAt;
        this.questions = questions;
        this.status = (status != null) ? status : AppointmentStatus.SCHEDULED;
        this.insurance = insurance;
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
        return appointmentId;
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

    public long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public Long getVaccineProductId() {
        return vaccineProductId;
    }

    public void setVaccineProductId(Long vaccineProductId) {
        this.vaccineProductId = vaccineProductId;
    }

    public ContactInfo getPatient() {
        return patient;
    }

    public void setPatient(ContactInfo patient) {
        this.patient = patient;
    }

    public String getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(String vaccineType) {
        this.vaccineType = vaccineType;
    }

    public LocalDateTime getAppointmentAt() {
        return appointmentAt;
    }

    public void setAppointmentAt(LocalDateTime appointmentAt) {
        this.appointmentAt = appointmentAt;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VaccineAppointment that = (VaccineAppointment) o;
        return Objects.equals(appointmentId, that.appointmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(appointmentId);
    }

    @Override
    public String toString() {
        return "VaccineAppointment{" +
                "appointmentId =" + appointmentId +
                ", patientName ='" + patient + '\'' +
                ", vaccineType ='" + vaccineType + '\'' +
                ", appointmentAt =" + appointmentAt +
                ", status=" + status +
                '}';
    }

}
