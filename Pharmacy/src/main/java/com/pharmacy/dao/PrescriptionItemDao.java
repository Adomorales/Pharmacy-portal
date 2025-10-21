package com.pharmacy.dao;

import com.pharmacy.model.PrescriptionItem;

import java.util.List;

public interface PrescriptionItemDao {

    PrescriptionItem getById(long prescriptionItemId);

    List<PrescriptionItem> listByPrescription(long prescriptionId);

    PrescriptionItem create(PrescriptionItem item);

    PrescriptionItem update(PrescriptionItem item);

    boolean deleteById(long prescriptionItemId);

    int deleteByPrescription(long prescriptionId);

}
