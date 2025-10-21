package com.pharmacy.model;

import java.util.UUID;

public interface PatientCase {

    long getCaseId();
    String getPatientFirstName();
    String getPatientLastName();
    String getPatientPhone();

    ContactInfo getPatientContact();

}
