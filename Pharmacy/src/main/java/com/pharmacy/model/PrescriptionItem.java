package com.pharmacy.model;

import com.pharmacy.model.enums.DawCode;

import java.math.BigDecimal;
import java.util.Objects;

public class PrescriptionItem {

    private long itemId;
    private long prescriptionId;
    private long medicationId;
    private String strength;
    private String dose;
    private BigDecimal quantity;
    private String sig;
    private DawCode daw;
    private int refills;

    public PrescriptionItem() {}

    public PrescriptionItem(long itemId, long prescriptionId, long medicationId, String strength, String dose, BigDecimal quantity, String sig, DawCode daw, int refills) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PrescriptionItem that = (PrescriptionItem) o;
        return itemId == that.itemId && prescriptionId == that.prescriptionId && medicationId == that.medicationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, prescriptionId, medicationId);
    }

    @Override
    public String toString() {
        return "PrescriptionItem{" +
                "itemId=" + itemId +
                ", prescriptionId=" + prescriptionId +
                ", medicationId=" + medicationId +
                ", strength='" + strength + '\'' +
                ", dose='" + dose + '\'' +
                ", quantity=" + quantity +
                ", sig='" + sig + '\'' +
                ", daw=" + daw +
                ", refills=" + refills +
                '}';
    }
}
