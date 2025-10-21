package com.pharmacy.model;

import java.util.Objects;

public class Prescriber {

    private long prescriberId;
    private ContactInfo contact;
    private String npi;

    public Prescriber() {}

    public Prescriber(long prescriberId, ContactInfo contact, String npi) {
        this.prescriberId = prescriberId;
        this.contact = contact;
        this.npi = npi;
    }

    public long getPrescriberId() {
        return prescriberId;
    }

    public void setPrescriberId(long prescriberId) {
        this.prescriberId = prescriberId;
    }

    public ContactInfo getContact() {
        return contact;
    }

    public void setContact(ContactInfo contact) {
        this.contact = contact;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Prescriber that = (Prescriber) o;
        return prescriberId == that.prescriberId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prescriberId);
    }

    @Override
    public String toString() {
        return "Prescriber{" +
                "prescriberId=" + prescriberId +
                ", contact=" + contact +
                ", npi='" + npi + '\'' +
                '}';
    }

}
