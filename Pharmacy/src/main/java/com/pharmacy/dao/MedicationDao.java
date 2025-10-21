package com.pharmacy.dao;

import com.pharmacy.model.Medication;

import java.util.List;

public interface MedicationDao {

    Medication getById(long medicationId);

    Medication getByNdc(String ndc);

    List<Medication> searchByName(String q);

    List<Medication> listAll();

    Medication create(Medication med);

    Medication update(Medication med);

    boolean deleteById(long medicationId);

}
