package com.pharmacy.dao;

import com.pharmacy.model.Facility;

import java.util.List;

public interface FacilityDao {

    Facility getById(long facilityId);

    List<Facility> listAll();

    Facility create(Facility facility);

    Facility update(Facility facility);

    boolean deleteById(long facilityId);
}
