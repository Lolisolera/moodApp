import React from "react";
import "../styles/components/moodResult.scss";

interface MoodResultProps {
  response: any;
  isPlaying: boolean;
  togglePreview: () => void;
}

export default function MoodResult({
  response,
  isPlaying,
  togglePreview,
}: MoodResultProps) {
  if (!response) return null;

  return (
    <div className="mood-result">
      <h3 className="mood-result__title">Result:</h3>

      <p className="mood-result__text">
        <strong>Mood:</strong> {response.mood}
      </p>

      <p className="mood-result__text">
        <strong>Track:</strong> {response.track?.title} — {response.track?.artist}
      </p>

      {/* Play Button */}
      {response.track?.previewUrl && (
        <button
          className="mood-result__button"
          onClick={togglePreview}
        >
          {isPlaying ? "⏸️ Pause" : "▶️ Play Preview"}
        </button>
      )}

      {/* Mood Image */}
      {response.imageUrl && (
        <img
          className="mood-result__image"
          src={response.imageUrl}
          alt="Mood"
        />
      )}
    </div>
  );
}
