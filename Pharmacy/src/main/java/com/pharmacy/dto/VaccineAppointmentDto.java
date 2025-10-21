package com.pharmacy.dto;

import com.pharmacy.model.ContactInfo;
import com.pharmacy.model.InsuranceInfo;
import com.pharmacy.model.enums.AppointmentStatus;

import java.time.LocalDateTime;

public class VaccineAppointmentDto {

    private long appointmentId;
    private long patientId;
    private Long vaccineProductId;

    private ContactInfo patient;

    private String vaccineType;
    private LocalDateTime appointmentAt;
    private String questions;

    private AppointmentStatus status;

    private InsuranceInfo insurance;

    public VaccineAppointmentDto() {
        this.status = AppointmentStatus.SCHEDULED;
    }

    public VaccineAppointmentDto(long appointmentId, ContactInfo patient, String vaccineType, LocalDateTime appointmentAt, String questions, AppointmentStatus status, InsuranceInfo insurance) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.vaccineType = vaccineType;
        this.appointmentAt = appointmentAt;
        this.questions = questions;
        this.status = (status != null) ? status : AppointmentStatus.SCHEDULED;
        this.insurance = insurance;
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

    public InsuranceInfo getInsurance() { return insurance; }

    public void setInsurance(InsuranceInfo insurance) { this.insurance = insurance; }

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

}
