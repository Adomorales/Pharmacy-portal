package com.pharmacy.model;

import com.pharmacy.model.enums.FacilityType;

import java.util.Objects;

public class Facility {

    private long facilityId;
    private String name;
    private FacilityType type;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;

    public Facility() {}

    public Facility(long facilityId, String name, FacilityType type, String phone, String address, String city, String state, String zipCode) {
        this.facilityId = facilityId;
        this.name = name;
        this.type = type;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FacilityType getType() {
        return type;
    }

    public void setType(FacilityType type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Facility facility = (Facility) o;
        return facilityId == facility.facilityId && Objects.equals(name, facility.name) && type == facility.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(facilityId, name, type);
    }

    @Override
    public String toString() {
        return "Facility{" +
                "facilityId=" + facilityId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", city='" + city + '\'' +
                '}';
    }
}
