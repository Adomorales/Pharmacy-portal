import api from './Api.js';

const API_BASE_URL = '/medications';

const MedicationService = {
    // Get medication by ID
    getById: (id) => {
        return api.get(`${API_BASE_URL}/${id}`);
    },

    // Get all medications
    listAll: () => {
        return api.get(API_BASE_URL);
    },

    // Create new medication
    create: (medicationDto) => {
        return api.post(API_BASE_URL, medicationDto);
    },

    // Update medication
    update: (id, medicationDto) => {
        return api.put(`${API_BASE_URL}/${id}`, medicationDto);
    },

    // Delete medication
    delete: (id) => {
        return api.delete(`${API_BASE_URL}/${id}`);
    }
};

export default MedicationService;
