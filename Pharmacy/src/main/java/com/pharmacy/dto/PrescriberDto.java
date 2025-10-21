package com.pharmacy.dto;

import com.pharmacy.model.ContactInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PrescriberDto {

    private long prescriberId;
    @NotNull
    private ContactInfo contact;
    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "NPI must be 10 digits")
    private String npi;

    public PrescriberDto() {}

    public PrescriberDto(long prescriberId, ContactInfo contact, String npi) {
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

}
