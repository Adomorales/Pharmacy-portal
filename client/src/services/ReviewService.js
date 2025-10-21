import api from './Api.js';

const API_BASE_URL = '/data-reviews';

const ReviewService = {
    // Get review by ID
    getById: (id) => {
        return api.get(`${API_BASE_URL}/${id}`);
    },

    // Get reviews by data entry ID
    listByDataEntry: (entryId) => {
        return api.get(`${API_BASE_URL}/by-entry/${entryId}`);
    },

    // Create new review
    create: (reviewDto) => {
        return api.post(API_BASE_URL, reviewDto);
    }
};

export default ReviewService;