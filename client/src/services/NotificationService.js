import api from '/api';

const NotificationService = {
    getNotificationById(notificationId) {
        return api.get(`/notifications/${notificationId}`);
    },

    createNotification(notificationDto) {
        return api.post('/notifications', notificationDto);
    },

    updateNotificationStatus(notificationId, status) {
        return api.put(`/notifications/${notificationId}/status`, null, {
            params: { status }
        });
    },

    markNotificationAsSent(notificationId) {
        return api.put(`/notifications/${notificationId}/mark-sent`);
    },

    deleteNotification(notificationId) {
        return api.delete(`/notifications/${notificationId}`);
    },

    getPendingNotifications() {
        return api.get('/notifications/pending');
    },

    getNotificationsByCaseId(caseId) {
        return api.get(`/notifications/case/${caseId}`);
    },

    getNotificationsByStatus(status) {
        return api.get(`/notifications/status/${status}`);
    }
};

export default NotificationService;