import { useState } from "react";
import axios from "axios";

function App() {
  const [mood, setMood] = useState("");
  const [response, setResponse] = useState<any>(null);
  const [audio, setAudio] = useState<HTMLAudioElement | null>(null);
  const [isPlaying, setIsPlaying] = useState(false);

  const analyzeMood = async () => {
    try {
      // Stop previous audio if playing
      if (audio) {
        audio.pause();
        setIsPlaying(false);
      }

      const res = await axios.post(
        import.meta.env.VITE_BACKEND_URL + "/moods/analyze",
        { mood }
      );

      setResponse(res.data);
      setAudio(null); // reset audio element
    } catch (err) {
      console.error("API error:", err);
    }
  };

  const togglePreview = () => {
    if (!response?.track?.previewUrl) return;

    // If audio exists already, toggle play/pause
    if (audio) {
      if (isPlaying) {
        audio.pause();
        setIsPlaying(false);
      } else {
        audio.play();
        setIsPlaying(true);
      }
      return;
    }

    // First time play: create new audio object
    const newAudio = new Audio(response.track.previewUrl);
    setAudio(newAudio);
    newAudio.play();
    setIsPlaying(true);

    // Reset when track ends
    newAudio.onended = () => setIsPlaying(false);
  };

  return (
    <div style={{ padding: "20px", fontFamily: "Arial" }}>
      <h1>MoodApp 🎭</h1>

      <input
        type="text"
        placeholder="How are you feeling?"
        value={mood}
        onChange={(e) => setMood(e.target.value)}
        style={{
          padding: "8px",
          fontSize: "16px",
          marginRight: "8px"
        }}
      />

      <button
        onClick={analyzeMood}
        style={{
          padding: "8px 16px",
          fontSize: "16px",
          cursor: "pointer"
        }}
      >
        Reveal My Vibe 💫
      </button>

      {response && (
        <div style={{ marginTop: "20px" }}>
          <h3>Result:</h3>
          <p><strong>Mood:</strong> {response.mood}</p>
          <p><strong>Track:</strong> {response.track?.title} — {response.track?.artist}</p>

          {/* Play/Pause Button */}
          {response.track?.previewUrl && (
            <button
              onClick={togglePreview}
              style={{
                padding: "6px 12px",
                fontSize: "14px",
                marginTop: "10px",
                cursor: "pointer"
              }}
            >
              {isPlaying ? "⏸️ Pause" : "▶️ Play Preview"}
            </button>
          )}

          {response.imageUrl && (
            <img
              src={response.imageUrl}
              alt="Mood"
              style={{ width: "200px", borderRadius: "10px", marginTop: "10px" }}
            />
          )}
        </div>
      )}
    </div>
  );
}

export default App;
