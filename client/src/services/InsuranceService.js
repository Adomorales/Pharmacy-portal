import api from './Api.js';

const API_BASE_URL = '/insurance';

const InsuranceService = {
    // Get insurance by ID
    getById: (id) => {
        return api.get(`${API_BASE_URL}/${id}`);
    },

    // Get insurance by patient ID
    listByPatient: (patientId) => {
        return api.get(`${API_BASE_URL}/by-patient/${patientId}`);
    },

    // Create new insurance
    create: (insuranceDto) => {
        return api.post(API_BASE_URL, insuranceDto);
    },

    // Update insurance
    update: (id, insuranceDto) => {
        return api.put(`${API_BASE_URL}/${id}`, insuranceDto);
    },

    // Delete insurance
    delete: (id) => {
        return api.delete(`${API_BASE_URL}/${id}`);
    }
};

export default InsuranceService;
