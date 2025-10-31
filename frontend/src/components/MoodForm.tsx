import { useState } from "react";
import axios from "axios";

interface MoodFormProps {
  setResponse: (data: any) => void;
  stopAudio: () => void;
}

export default function MoodForm({ setResponse, stopAudio }: MoodFormProps) {
  const [mood, setMood] = useState("");

  const analyzeMood = async () => {
    try {
      stopAudio();

      const res = await axios.post(
        import.meta.env.VITE_BACKEND_URL + "/moods/analyze",
        { mood }
      );

      setResponse(res.data);
    } catch (err) {
      console.error("API error:", err);
    }
  };

  return (
    <div>
      <input
        type="text"
        placeholder="How are you feeling?"
        value={mood}
        onChange={(e) => setMood(e.target.value)}
        style={{ padding: "8px", fontSize: "16px", marginRight: "8px" }}
      />

      <button
        onClick={analyzeMood}
        style={{ padding: "8px 16px", fontSize: "16px", cursor: "pointer" }}
      >
        Reveal My Vibe ✨
      </button>
    </div>
  );
}

// Holds state + passes down handlers