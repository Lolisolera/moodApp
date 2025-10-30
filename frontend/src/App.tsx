import { useEffect } from "react";
import axios from "axios";

function App() {
  useEffect(() => {
    axios.post(import.meta.env.VITE_BACKEND_URL + "/moods/analyze", {
      mood: "happy"
    })
      .then(res => console.log("✅ Backend response:", res.data))
      .catch(err => console.error("❌ API error:", err));
  }, []);

  return <h1>MoodApp Connected ✅</h1>;
}

export default App;
