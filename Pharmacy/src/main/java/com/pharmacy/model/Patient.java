package com.pharmacy.model;

import com.pharmacy.model.enums.Sex;

import java.time.LocalDate;
import java.util.Objects;

public class Patient {

    private long patientId;
    private ContactInfo contact;
    private LocalDate dateOfBirth;
    private Sex sex;

    public Patient() {}

    public Patient(long patientId, ContactInfo contact, LocalDate dateOfBirth, Sex sex) {
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return patientId == patient.patientId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(patientId);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", contact=" + contact +
                ", dateOfBirth=" + dateOfBirth +
                ", sex=" + sex +
                '}';
    }

}
