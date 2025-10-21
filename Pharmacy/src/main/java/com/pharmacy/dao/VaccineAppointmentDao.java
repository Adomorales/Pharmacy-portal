package com.pharmacy.dao;

import com.pharmacy.model.VaccineAppointment;
import com.pharmacy.model.enums.AppointmentStatus;

import java.util.List;

public interface VaccineAppointmentDao {

    VaccineAppointment getById(long appointmentId);

    List<VaccineAppointment> listByPatient(long patientId);

    VaccineAppointment create(VaccineAppointment v);

    VaccineAppointment update(VaccineAppointment v);

    VaccineAppointment updateStatus(long appointmentId, AppointmentStatus status);

    boolean delete(long appointmentId);

    List<VaccineAppointment> listByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<VaccineAppointment> listByStatus(AppointmentStatus status);

    List<VaccineAppointment> listByDateRangeAndStatus(LocalDateTime startDate, LocalDateTime endDate, AppointmentStatus status);

    List<VaccineAppointment> listByDateRangeAndPatientId(LocalDateTime startDate, LocalDateTime endDate, long patientId);

    List<VaccineAppointment> listByDateRangeAndStatusAndPatientId(LocalDateTime startDate, LocalDateTime endDate, AppointmentStatus status, long patientId);

}
