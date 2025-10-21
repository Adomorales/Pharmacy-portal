package com.pharmacy.model;

import java.util.Objects;

public class Medication {

    private long medicationId;
    private String name;
    private String genericName;
    private String brandName;

    public Medication() {}

    public Medication(long medicationId, String name, String genericName, String brandName) {
        this.medicationId = medicationId;
        this.name = name;
        this.genericName = genericName;
        this.brandName = brandName;
    }

    public long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(long medicationId) {
        this.medicationId = medicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Medication that = (Medication) o;
        return medicationId == that.medicationId && Objects.equals(name, that.name) && Objects.equals(genericName, that.genericName) && Objects.equals(brandName, that.brandName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medicationId, name, genericName, brandName);
    }

    @Override
    public String toString() {
        return "Medication{" +
                "medicationId=" + medicationId +
                ", name='" + name + '\'' +
                ", genericName='" + genericName + '\'' +
                ", brandName='" + brandName + '\'' +
                '}';
    }
}
