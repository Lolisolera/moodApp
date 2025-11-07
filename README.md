# 🎧 MoodApp — Visualize Your Mood Through Music and Images

**MoodApp** is an interactive full-stack application that transforms your emotions into visuals and sound.  
Simply type how you’re feeling, and the app generates:

- 🎵 A **mood-matching song** (via the free **Deezer API**)
- 🖼️ A **related background image** (via the free **Pexels API**)

It’s an artistic fusion of **music, mood, and visual design** — built using modern web technologies.

---

## 🌟 Features

- Type any mood or emotion (e.g., *“calm and dreamy”*, *“energetic and happy”*)
- Instantly see a matching background image
- Listen to a 30-second track preview directly in the browser
- Responsive and minimal UI built with **React + TypeScript + SCSS**
- Backend powered by **Spring Boot** for API integration

---

## 🖥️ Live Demo (Coming Soon)

A deployed version of MoodApp will be available soon!  
*(Once live, I'll add the Netlify and Railway links here.)*

---

## ⚙️ Tech Stack

### 🎨 Frontend
- React + TypeScript (Vite)
- SCSS (BEM)
- Axios

### 🧩 Backend
- Java 17+
- Spring Boot (Web, WebFlux, DevTools)
- Lombok
- Jackson
- Maven

### 🌐 APIs
- **Pexels API** — mood-related images
- **Deezer API** — music previews matching mood keywords

---

## 🚀 Getting Started (Local)

### Clone the repo
```bash
git clone https://github.com/Lolisolera/moodApp.git
cd moodApp

```
### Backend (Spring Boot)

1. Open the project in **IntelliJ IDEA (Community Edition)**.
2. Set your **Pexels API key** as an environment variable:

```bash
   export PEXELS_API_KEY=your_real_key_here
```
### Run the backend application by executing MoodAppApplication.java.
The backend will start on:
👉 http://localhost:8090

### Test the backend endpoint:

```bash
  curl -X POST http://localhost:8090/api/moods/analyze \
  -H 'Content-Type: application/json' \
  -d '{"mood":"calm and dreamy"}'

```

### Frontend (Vite + React + TypeScript)

1. cd frontend 
2. npm install 
3. npm run dev 
4. Open your browser and go to: 👉 http://localhost:5173

## 🎮 How to Use

1. Type a mood (e.g., “calm and dreamy”). 
2. Click Generate. 
3. Enjoy the background image and 30-second music preview. 
4. Try different moods to explore new visual and audio combinations! 🎶

## 🧠 Developer Information

Lola Márquez — Junior Full-Stack Developer & Visual Artist (London, UK)
Blending creativity and technology through code and design.

GitHub: @Lolisolera

Open to suggestions, issues, and pull requests!