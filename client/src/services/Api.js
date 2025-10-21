import axios from "axios";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE || "/api",
    timeout: 15000,
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const { response } = error || {};
        const status = response ? response.status : null;
        const message = response && response.data ? response.data.message : error.message;
        console.error(`API Error: ${status} - ${message}`);
        if (status === 401) {
            
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            
            if (typeof window !== "undefined" && !window.location.pathname.includes("/login")) {
                window.location.href = "/login";
            }
            return Promise.reject({ status: 401, message: "Unauthorized" });
        }
        return Promise.reject(error);
    }
);

export default api;