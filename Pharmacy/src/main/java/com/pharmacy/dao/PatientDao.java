package com.pharmacy.dao;

import com.pharmacy.model.Patient;

import java.util.List;

public interface PatientDao {

    Patient getById(long patientId);

    List<Patient> listAll();

    Patient findByPhoneDigits(String phoneDigits);

    List<Patient> searchByName(String namePart);

    Patient create(Patient p);

    Patient update(Patient p);

    boolean deletePatient(long patientId);

}
