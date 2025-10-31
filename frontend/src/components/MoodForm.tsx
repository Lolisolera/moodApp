import { useState } from "react";
import axios from "axios";
import "../styles/components/moodForm.scss";

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
    <div className="mood-form">
      <input
        className="mood-form__input"
        type="text"
        placeholder="How are you feeling?"
        value={mood}
        onChange={(e) => setMood(e.target.value)}
      />

      <button
        className="mood-form__button"
        onClick={analyzeMood}
      >
        Reveal My Vibe ✨
      </button>
    </div>
  );
}
