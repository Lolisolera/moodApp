import React, { useEffect, useState } from "react";
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
  const [flash, setFlash] = useState(false);

  if (!response) return null;

  const handleImageLoad = (e: React.SyntheticEvent<HTMLImageElement>) => {
    const img = e.currentTarget;

    // Detect if portrait or landscape and pass to SCSS via attribute
    img.setAttribute(
      "landscape",
      img.naturalWidth >= img.naturalHeight ? "true" : "false"
    );

    // Trigger double flash animation
    setFlash(true);
    const timer = setTimeout(() => setFlash(false), 1500);
    return () => clearTimeout(timer);
  };

  return (
    <div className="mood-result">
      <h3 className="mood-result__title">Result:</h3>

    <div className="mood-result__text">
      Feeling{" "}
      <span
        key={response.mood}
        className={`mood-result__mood ${flash ? "flash" : ""}`}
      >
        {response.mood}
      </span>
      ?<br />
      Me too! Let’s turn it into a moment 🎶
      <br />
      <br />
      “{response.track?.title}” by {response.track?.artist} is your emotional banger of the day 🎧
    </div>


      {response.track?.previewUrl && (
        <button className="mood-result__button" onClick={togglePreview}>
          {isPlaying ? "⏸️ Pause" : "▶️ Play Preview"}
        </button>
      )}

      {response.imageUrl && (
        <div
          className={`mood-result__image-wrapper ${flash ? "flash" : ""}`}
        >
          <img
            key={response.imageUrl} // Force animation reload on new image
            className="mood-result__image"
            src={response.imageUrl}
            alt="Mood"
            onLoad={handleImageLoad}
          />
        </div>
      )}
    </div>
  );
}
