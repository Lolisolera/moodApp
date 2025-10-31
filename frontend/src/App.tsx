import { useState } from "react";
import MoodForm from "./components/MoodForm";
import MoodResult from "./components/MoodResult";

function App() {
  const [response, setResponse] = useState<any>(null);
  const [audio, setAudio] = useState<HTMLAudioElement | null>(null);
  const [isPlaying, setIsPlaying] = useState(false);

  const handleNewResponse = (data: any) => {
    // Stop any existing audio before setting new result
    if (audio) {
      audio.pause();
      setIsPlaying(false);
    }

    setResponse(data);
    setAudio(null);
  };

  const togglePreview = () => {
    if (!response?.track?.previewUrl) return;

    // If audio exists, toggle play/pause
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

    // First play
    const newAudio = new Audio(response.track.previewUrl);
    setAudio(newAudio);
    newAudio.play();
    setIsPlaying(true);

    newAudio.onended = () => setIsPlaying(false);
  };

  return (
    <div style={{ padding: "20px", fontFamily: "Arial" }}>
      <h1>MoodApp 🎭</h1>

      <MoodForm
        setResponse={handleNewResponse}
        stopAudio={() => {
          if (audio) audio.pause();
          setIsPlaying(false);
        }}
      />

      {response && (
        <MoodResult
          response={response}
          isPlaying={isPlaying}
          togglePreview={togglePreview}
        />
      )}
    </div>
  );
}

export default App;

// Holds state + passes down handlers