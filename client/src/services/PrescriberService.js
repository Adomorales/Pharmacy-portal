import api from './Api.js';

const API_BASE_URL = '/prescribers';

const PrescriberService = {
    // Get prescriber by ID
    getById: (id) => {
        return api.get(`${API_BASE_URL}/${id}`);
    },

    // Get prescriber by NPI
    getByNpi: (npi) => {
        return api.get(`${API_BASE_URL}/by-npi/${npi}`);
    },

    // Search prescribers by name
    searchByName: (query) => {
        return api.get(`${API_BASE_URL}/search`, {
            params: { q: query }
        });
    },

    // Create new prescriber
    create: (prescriberDto) => {
        return api.post(API_BASE_URL, prescriberDto);
    },

    // Update prescriber
    update: (id, prescriberDto) => {
        return api.put(`${API_BASE_URL}/${id}`, prescriberDto);
    }
};

export default PrescriberService;
