import api from './Api.js';

const API_BASE_URL = '/products';

const ProductService = {
    // Get product by fulfillment ID
    getProductById: (fulfillmentId) => {
        return api.get(`${API_BASE_URL}/${fulfillmentId}`);
    },

    // Create new product
    createProduct: (productDto) => {
        return api.post(API_BASE_URL, productDto);
    },

    // Update product
    updateProduct: (fulfillmentId, productDto) => {
        return api.put(`${API_BASE_URL}/${fulfillmentId}`, productDto);
    },

    // Delete product
    deleteProduct: (fulfillmentId) => {
        return api.delete(`${API_BASE_URL}/${fulfillmentId}`);
    },

    // Get products by prescription ID
    getProductsByPrescriptionId: (prescriptionId) => {
        return api.get(`${API_BASE_URL}/prescription/${prescriptionId}`);
    },

    // Get products by status
    getProductsByStatus: (status) => {
        return api.get(`${API_BASE_URL}/status/${status}`);
    },

    // Update product status
    updateProductStatus: (fulfillmentId, status) => {
        return api.put(`${API_BASE_URL}/${fulfillmentId}/status`, null, {
            params: { status }
        });
    }
};

export default ProductService;