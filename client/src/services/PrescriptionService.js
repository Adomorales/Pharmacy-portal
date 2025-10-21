import api from './Api.js';

const API_BASE_URL = '/prescriptions';

const PrescriptionService = {
    // Get prescription by ID
    getById: (id) => {
        return api.get(`${API_BASE_URL}/${id}`);
    },

    // Get prescriptions by patient ID
    listByPatient: (patientId) => {
        return api.get(`${API_BASE_URL}/by-patient/${patientId}`);
    },

    // Get prescriptions by prescriber ID
    listByPrescriber: (prescriberId) => {
        return api.get(`${API_BASE_URL}/by-prescriber/${prescriberId}`);
    },

    // Get prescriptions by patient and status
    listByPatientAndStatus: (patientId, status) => {
        return api.get(`${API_BASE_URL}/by-patient/${patientId}/status/${status}`);
    },

    // Create new prescription
    create: (prescriptionDto) => {
        return api.post(API_BASE_URL, prescriptionDto);
    },

    // Update prescription status
    updateStatus: (id, status) => {
        return api.patch(`${API_BASE_URL}/${id}/status/${status}`);
    },

    // Update prescription prescriber
    updatePrescriber: (id, prescriberId) => {
        return api.patch(`${API_BASE_URL}/${id}/prescriber/${prescriberId}`);
    },

    // Delete prescription
    delete: (id) => {
        return api.delete(`${API_BASE_URL}/${id}`);
    }
};

export default PrescriptionService;
