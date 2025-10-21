package com.pharmacy.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

public class MedicationDto {

    @Positive
    private long medicationId;
    @NotBlank
    private String genericName;
    private String brandName;

    public MedicationDto() {}

    public MedicationDto(long medicationId, String genericName, String brandName) {
        this.medicationId = medicationId;
        this.genericName = genericName;
        this.brandName = brandName;
    }

    public long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(long medicationId) {
        this.medicationId = medicationId;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

}
