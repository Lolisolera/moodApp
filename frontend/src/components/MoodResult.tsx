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

  const handleImageLoad = (e: React.SyntheticEvent<HTMLImageElement>) => {
    const img = e.currentTarget;

    // Detect if portrait or landscape and pass to SCSS via attribute
    img.setAttribute(
      "landscape",
      img.naturalWidth >= img.naturalHeight ? "true" : "false"
    );
  };

  return (
    <div className="mood-result">
      <h3 className="mood-result__title">Result:</h3>

      <div className="mood-result__text">
        <strong>Mood:</strong> {response.mood}
      </div>

      <div className="mood-result__text">
        <strong>Track:</strong>{" "}
        {response.track?.title} — {response.track?.artist}
      </div>

      {response.track?.previewUrl && (
        <button className="mood-result__button" onClick={togglePreview}>
          {isPlaying ? "⏸️ Pause" : "▶️ Play Preview"}
        </button>
      )}

      {response.imageUrl && (
        <div className="mood-result__image-wrapper">
          <img
            key={response.imageUrl} // Force animation reload on new image
            className={`mood-result__image glow`}
            src={response.imageUrl}
            alt="Mood"
            onLoad={handleImageLoad}
          />
        </div>
      )}
    </div>
  );
}
