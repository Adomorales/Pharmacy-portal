import api from './Api.js';

const API_BASE_URL = '/vaccine-appointments';

const VaccineService = {
    // Get appointment by ID
    getAppointmentById: (appointmentId) => {
        return api.get(`${API_BASE_URL}/${appointmentId}`);
    },

    // Create new appointment
    createAppointment: (appointmentDto) => {
        return api.post(API_BASE_URL, appointmentDto);
    },

    // Update appointment
    updateAppointment: (appointmentId, appointmentDto) => {
        return api.put(`${API_BASE_URL}/${appointmentId}`, appointmentDto);
    },

    // Update appointment status
    updateAppointmentStatus: (appointmentId, status) => {
        return api.put(`${API_BASE_URL}/${appointmentId}/status`, null, {
            params: { status }
        });
    },

    // Delete appointment
    deleteAppointment: (appointmentId) => {
        return api.delete(`${API_BASE_URL}/${appointmentId}`);
    },

    // Cancel appointment
    cancelAppointment: (appointmentId, reason = null) => {
        return api.post(`${API_BASE_URL}/${appointmentId}/cancel`, null, {
            params: { reason }
        });
    },

    // Get appointments by patient ID
    getAppointmentsByPatientId: (patientId) => {
        return api.get(`${API_BASE_URL}/patient/${patientId}`);
    },

    // Get appointments by status
    getAppointmentsByStatus: (status) => {
        return api.get(`${API_BASE_URL}/status/${status}`);
    },

    // Get upcoming appointments
    getUpcomingAppointments: () => {
        return api.get(`${API_BASE_URL}/upcoming`);
    },

    // Get appointments by date range
    getAppointmentsByDateRange: (startDate, endDate) => {
        return api.get(`${API_BASE_URL}/date-range`, {
            params: {
                startDate: startDate.toISOString(),
                endDate: endDate.toISOString()
            }
        });
    }
};

export default VaccineService;