import api from './Api.js';

const API_BASE_URL = '/patients';

const PatientService = {
    // Get all patients
    getAllPatients: () => {
        return api.get(API_BASE_URL);
    },

    // Get patient by ID
    getPatientById: (patientId) => {
        return api.get(`${API_BASE_URL}/${patientId}`);
    },

    // Create new patient
    createPatient: (patientDto) => {
        return api.post(API_BASE_URL, patientDto);
    },

    // Update patient
    updatePatient: (patientId, patientDto) => {
        return api.put(`${API_BASE_URL}/${patientId}`, patientDto);
    },

    // Delete patient
    deletePatient: (patientId) => {
        return api.delete(`${API_BASE_URL}/${patientId}`);
    },

    // Search patients
    searchPatients: (query) => {
        return api.get(`${API_BASE_URL}/search`, {
            params: { query }
        });
    },

    // Lookup patient by contact info
    lookupPatientByContact: (firstName = null, lastName = null, phone = null) => {
        return api.post(`${API_BASE_URL}/lookup`, null, {
            params: {
                firstName,
                lastName,
                phone
            }
        });
    }
};

export default PatientService;
