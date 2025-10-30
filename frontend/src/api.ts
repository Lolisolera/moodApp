import axios from "axios";

// Backend base URL (points to my Spring Boot app)
const api = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL,
});

// Example GET request
export const getMoods = () => {
  return api.get("/moods");
};

// Example POST request
export const addMood = (moodData: { mood: string; note: string }) => {
  return api.post("/moods", moodData);
};


export default api;
