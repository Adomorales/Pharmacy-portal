import api from './Api.js';

const API_BASE_URL = '/data-entry';

const DataEntryService = {
    getById: (id) => {
        return api.get(`${API_BASE_URL}/${id}`);
    },

    listQueue: () => {
        return api.get(`${API_BASE_URL}/queue`);
    },

    create: (dataEntryDto) => {
        return api.post(API_BASE_URL, dataEntryDto);
    },

    update: (id, dataEntryDto) => {
        return api.put(`${API_BASE_URL}/${id}`, dataEntryDto);
    }
};

export default DataEntryService;