package com.pharmacy.dao;

import com.pharmacy.model.Prescriber;

import java.util.List;

public interface PrescriberDao {

    Prescriber getById(long prescriberId);

    List<Prescriber> listAll();

    Prescriber getByNpi(String npi);

    List<Prescriber> searchByName(String namePart);

    Prescriber create(Prescriber p);

    Prescriber update(Prescriber p);

}
