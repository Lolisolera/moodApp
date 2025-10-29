import axios from "axios";

// Backend base URL (points to my Spring Boot app)
const api = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL || "http://localhost:8080/api",
});

export default api;
