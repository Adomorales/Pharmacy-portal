package com.pharmacy.dto;

import com.pharmacy.model.enums.DawCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class PrescriptionItemDto {

    private long itemId;
    @Positive
    private long prescriptionId;
    @Positive
    private long medicationId;
    private String strength;
    private String dose;
    @NotNull
    private BigDecimal quantity;
    @NotBlank
    private String sig;
    @NotNull
    private DawCode daw;
    @Positive
    private int refills;

    public PrescriptionItemDto() {}

    public PrescriptionItemDto(long itemId, long prescriptionId, long medicationId, String strength, String dose, BigDecimal quantity, String sig, DawCode daw, int refills) {
        this.itemId = itemId;
        this.prescriptionId = prescriptionId;
        this.medicationId = medicationId;
        this.strength = strength;
        this.dose = dose;
        this.quantity = quantity;
        this.sig = sig;
        this.daw = daw;
        this.refills = refills;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(long medicationId) {
        this.medicationId = medicationId;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public DawCode getDaw() {
        return daw;
    }

    public void setDaw(DawCode daw) {
        this.daw = daw;
    }

    public int getRefills() {
        return refills;
    }

    public void setRefills(int refills) {
        this.refills = refills;
    }

}
