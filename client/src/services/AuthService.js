import api from '@/services/Api.js'; 

const AuthService = {
    login(username, password) {
        return api.post('/auth/login', { username, password })
            .then((response) => {
                if (response.data.token && response.data.role) {
                    localStorage.setItem('user', JSON.stringify(response.data));
                }
                return response.data;
            });
    },

    register(user) {
        return api.post('/auth/register', user);
    },

    logout() {
        localStorage.removeItem('user');
    },

    getCurrentUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    }
};

export default AuthService;