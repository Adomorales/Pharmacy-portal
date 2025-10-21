package com.pharmacy.dao;

import com.pharmacy.model.Prescription;
import com.pharmacy.model.enums.RxStatus;

import java.util.List;

public interface PrescriptionDao {

    Prescription getById(long prescriptionId);

    List<Prescription> listByPatient(long patientId);

    List<Prescription> listByPrescriber(long prescriberId);

    List<Prescription> listByPatientAndStatus(long patientId, RxStatus status);

    Prescription create(Prescription rx);

    Prescription updateStatus(long prescriptionId, RxStatus status);

    Prescription updatePrescriber(long prescriptionId, long prescriberId);

    boolean deleteById(long prescriptionId);

}
