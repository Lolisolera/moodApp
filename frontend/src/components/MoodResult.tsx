import React from "react";

interface MoodResultProps {
  response: any;
  isPlaying: boolean;
  togglePreview: () => void;
}

export default function MoodResult({ response, isPlaying, togglePreview }: MoodResultProps) {
  if (!response) return null; // don't render anything until we have data

  return (
    <div style={{ marginTop: "20px" }}>
      <h3>Result:</h3>

      <p><strong>Mood:</strong> {response.mood}</p>
      <p><strong>Track:</strong> {response.track?.title} — {response.track?.artist}</p>

      {/* Play Preview Button */}
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

      {/* Mood Image */}
      {response.imageUrl && (
        <img
          src={response.imageUrl}
          alt="Mood"
          style={{
            width: "200px",
            borderRadius: "10px",
            marginTop: "10px"
          }}
        />
      )}
    </div>
  );
}

// Input + API call