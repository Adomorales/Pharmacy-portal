package com.pharmacy.model;

import com.pharmacy.model.enums.Sex;

import java.util.Objects;

public class ContactInfo {

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String phone;
    private String email;
    private Sex sex;

    public ContactInfo() {}

    public ContactInfo(String firstName, String lastName, String address, String city, String state, String zipCode, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.phone = phone;
        this.email = email;
    }

    public ContactInfo(String firstName, String lastName, String address, String city, String state, String zipCode, String phone, String email, Sex sex) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.phone = phone;
        this.email = email;
        this.sex = sex;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        ContactInfo that = (ContactInfo) o;
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(address, that.address) && Objects.equals(city, that.city) && Objects.equals(state, that.state) && Objects.equals(zipCode, that.zipCode) && Objects.equals(phone, that.phone) && Objects.equals(email, that.email) && sex == that.sex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, address, city, state, zipCode, phone, email, sex);
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "first name ='" + firstName + '\'' +
                "last name ='" + lastName + '\'' +
                ", address ='" + address + '\'' +
                ", phone ='" + phone + '\'' +
                '}';
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String redactPhone(String p) {
        return (p == null || p.length() < 4) ? "****" : "****" + p.substring(p.length() - 4);
    }

}
