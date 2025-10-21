package com.pharmacy.dto;

import com.pharmacy.model.ContactInfo;
import com.pharmacy.model.enums.Sex;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public class PatientDto {

    private long patientId;
    @NotNull
    private ContactInfo contact;
    @NotNull
    @Past
    private LocalDate dateOfBirth;
    @NotNull
    private Sex sex;

    public PatientDto() {}

    public PatientDto(long patientId, ContactInfo contact, LocalDate dateOfBirth, Sex sex) {
        this.patientId = patientId;
        this.contact = contact;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public ContactInfo getContact() {
        return contact;
    }

    public void setContact(ContactInfo contact) {
        this.contact = contact;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "PatientDto{" +
                "patientId=" + patientId +
                ", contact=" + contact +
                ", dateOfBirth=" + dateOfBirth +
                ", sex=" + sex +
                '}';
    }
}
