package com.pharmacy.dao;

import com.pharmacy.model.InsuranceInfo;

import java.util.List;

public interface InsuranceDao {

    List<InsuranceInfo> listAll();

    InsuranceInfo getById(long insuranceId);

    List<InsuranceInfo> listByPatient(long patientId);

    InsuranceInfo create(InsuranceInfo info);

    InsuranceInfo update(InsuranceInfo info);

    boolean delete(long insuranceId);

}
