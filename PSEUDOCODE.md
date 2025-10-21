# 🎵 MoodApp – Mood Visualizer

A full‑stack app where users type how they feel and instantly get:

* a **mood‑matching image** (via **Pexels API**), and
* a **related song/playlist** (via **Deezer API**) with a 30‑second preview.

This README is your **step‑by‑step guide** to build and run the project in **IntelliJ IDEA** (backend) and **Vite + React + TypeScript** (frontend).

---

## 🗺️ Architecture Overview

* **Frontend**: React + TypeScript (Vite), SCSS (BEM), Axios.
* **Backend**: Java 17+, Spring Boot, WebClient (HTTP), Lombok, Jackson.
* **External APIs**:

    * **Pexels** (images) — requires API key in an HTTP header: `Authorization: <PEXELS_API_KEY>`
    * **Deezer** (music) — public search endpoint, no key required.
* **Data Flow**: UI → `POST /api/moods/analyze` → backend calls Pexels & Deezer → aggregates → returns `{ imageUrl, tracks[] }` → UI renders image + audio preview.

---

## 📁 Project Structure (Monorepo layout)

```
MoodApp/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/lola/moodapp/
│       │   │   ├── MoodAppApplication.java
│       │   │   ├── config/
│       │   │   │   ├── WebClientConfig.java
│       │   │   │   └── CorsConfig.java
│       │   │   ├── controller/
│       │   │   │   └── MoodController.java
│       │   │   ├── service/
│       │   │   │   ├── MoodService.java
│       │   │   │   ├── PexelsClient.java
│       │   │   │   └── DeezerClient.java
│       │   │   ├── dto/
│       │   │   │   ├── MoodRequest.java
│       │   │   │   ├── TrackDto.java
│       │   │   │   └── MoodResponse.java
│       │   │   └── exception/
│       │   │       ├── ApiExceptionHandler.java
│       │   │       └── ExternalApiException.java
│       │   └── resources/
│       │       ├── application.yml
│       │       └── banners.txt (optional)
│       └── test/java/com/lola/moodapp/
│           └── MoodServiceTests.java
├── frontend/
│   ├── index.html
│   ├── package.json
│   ├── tsconfig.json
│   ├── vite.config.ts
│   └── src/
│       ├── App.tsx
│       ├── main.tsx
│       ├── api/
│       │   └── axios.ts
│       ├── services/
│       │   └── moodService.ts
│       ├── components/
│       │   ├── MoodForm.tsx
│       │   ├── MoodResult.tsx
│       │   └── Player.tsx
│       ├── pages/
│       │   └── Home.tsx
│       ├── hooks/
│       │   └── useMood.ts
│       └── styles/
│           ├── _variables.scss
│           ├── _mixins.scss
│           ├── base.scss
│           └── components/
│               ├── _mood-form.scss
│               ├── _mood-result.scss
│               └── _player.scss
├── .gitignore
└── README.md (this file)
```

---

## ✅ Prerequisites

* **Java 17+** (Amazon Corretto or OpenJDK)
* **Maven 3.9+**
* **Node.js 18+** and **npm** (or **pnpm**/**yarn**)
* **IntelliJ IDEA** (Community is fine)
* **Pexels API Key** (free) → [https://www.pexels.com/api/](https://www.pexels.com/api/)

> Deezer Search API needs no key. We’ll use `https://api.deezer.com/search?q=<query>`.

---

## 🧱 Backend Setup (Spring Boot)

### 1) Create project in IntelliJ

* New Project → **Spring Initializr** → Dependencies: **Spring Web**, **Lombok**, **Spring Boot DevTools**, **Spring Reactive Web** *(for WebClient)*.
* Group: `com.lola` — Artifact: `moodapp` — Packaging: `jar`.

### 2) Backend Dependencies

All required dependencies are already configured in [`pom.xml`](./pom.xml).

These include:

- **Spring Boot Starter Web** → for building RESTful APIs
- **Spring Boot Starter WebFlux** → for non-blocking HTTP calls to external APIs (like Pexels and Deezer)
- **Lombok** → for cleaner and more concise code (auto-generates getters/setters)
- **Jackson Databind** → for handling JSON serialization and deserialization
- **Spring Boot DevTools** → for automatic application reload during development
- **Spring Boot Starter Test** → for unit and integration testing

You can view or update these dependencies anytime in the main `pom.xml` file.


### 3) Configuration

```yaml
# backend/src/main/resources/application.yml
server:
  port: 8090

app:
  pexels:
    api-base: https://api.pexels.com/v1/search
    api-key: ${PEXELS_API_KEY:}
  deezer:
    api-base: https://api.deezer.com/search

spring:
  main:
    banner-mode: "off"
```

Set environment variable in your run configuration:

* **PEXELS_API_KEY** = `your_real_key_here`

### 4) WebClient config & CORS

```java
// WebClientConfig.java
@Configuration
public class WebClientConfig {
  @Bean
  public WebClient webClient(WebClient.Builder builder) { return builder.build(); }
}

// CorsConfig.java
@Configuration
public class CorsConfig {
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
          .allowedOrigins("http://localhost:5173")
          .allowedMethods("GET","POST");
      }
    }; }
}
```

### 5) DTOs

```java
// MoodRequest.java
@Data
public class MoodRequest { private String mood; }

// TrackDto.java
@Data @AllArgsConstructor @NoArgsConstructor
public class TrackDto {
  private String id; private String title; private String artist;
  private String previewUrl; private String link; private String albumCoverUrl;
}

// MoodResponse.java
@Data @AllArgsConstructor @NoArgsConstructor
public class MoodResponse {
  private String imageUrl; private List<TrackDto> tracks;
}
```

### 6) External clients

```java
// PexelsClient.java
@Service @RequiredArgsConstructor
public class PexelsClient {
  private final WebClient webClient;
  @Value("${app.pexels.api-base}") private String base;
  @Value("${app.pexels.api-key}") private String apiKey;

  public String findImage(String query) {
    var uri = UriComponentsBuilder.fromHttpUrl(base)
      .queryParam("query", query)
      .queryParam("per_page", 1).build(true).toUri();

    Map body = webClient.get().uri(uri)
      .header(HttpHeaders.AUTHORIZATION, apiKey)
      .retrieve().bodyToMono(Map.class).block();

    // naive extraction "photos[0].src.large"
    var photos = (List<Map>) body.getOrDefault("photos", List.of());
    if (photos.isEmpty()) return null;
    var src = (Map) photos.get(0).get("src");
    return src != null ? (String) src.getOrDefault("large", null) : null;
  }
}

// DeezerClient.java
@Service @RequiredArgsConstructor
public class DeezerClient {
  private final WebClient webClient;
  @Value("${app.deezer.api-base}") private String base;

  public List<TrackDto> searchTracks(String query) {
    var uri = UriComponentsBuilder.fromHttpUrl(base)
      .queryParam("q", query).build(true).toUri();

    Map body = webClient.get().uri(uri)
      .retrieve().bodyToMono(Map.class).block();

    var data = (List<Map>) body.getOrDefault("data", List.of());
    return data.stream().limit(5).map(item -> {
      var artist = (Map) item.get("artist");
      var album = (Map) item.get("album");
      return new TrackDto(
        Objects.toString(item.get("id"), null),
        Objects.toString(item.get("title"), null),
        artist != null ? Objects.toString(artist.get("name"), null) : null,
        Objects.toString(item.get("preview"), null),
        Objects.toString(item.get("link"), null),
        album != null ? Objects.toString(album.get("cover_medium"), null) : null
      );
    }).filter(t -> t.getPreviewUrl() != null).toList();
  }
}
```

### 7) Service & Controller

```java
// MoodService.java
@Service @RequiredArgsConstructor
public class MoodService {
  private final PexelsClient pexelsClient; private final DeezerClient deezerClient;

  public MoodResponse analyze(String mood) {
    var image = pexelsClient.findImage(mood);
    var tracks = deezerClient.searchTracks(mood);
    if (image == null && tracks.isEmpty())
      throw new ExternalApiException("No results for mood: " + mood);
    return new MoodResponse(image, tracks);
  }
}

// MoodController.java
@RestController @RequestMapping("/api/moods") @RequiredArgsConstructor
public class MoodController {
  private final MoodService service;

  @PostMapping("/analyze")
  public ResponseEntity<MoodResponse> analyze(@RequestBody MoodRequest req) {
    var mood = Optional.ofNullable(req.getMood()).orElse("").trim();
    if (mood.isBlank()) return ResponseEntity.badRequest().build();
    return ResponseEntity.ok(service.analyze(mood));
  }
}
```

### 8) Error handling

```java
// ExternalApiException.java
public class ExternalApiException extends RuntimeException { public ExternalApiException(String m){super(m);} }

// ApiExceptionHandler.java
@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(ExternalApiException.class)
  public ResponseEntity<Map<String,String>> handle(ExternalApiException ex){
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
      .body(Map.of("error", ex.getMessage()));
  }
}
```

### 9) Run backend

* In IntelliJ → run **MoodAppApplication** (port **8090**).
* Test with curl:

```bash
curl -X POST http://localhost:8090/api/moods/analyze \
  -H 'Content-Type: application/json' \
  -d '{"mood":"calm and dreamy"}'
```

---

## 🎨 Frontend Setup (Vite + React + TS)

### 1) Create project

```bash
cd frontend
npm create vite@latest . -- --template react-ts
npm i
npm i axios sass
```

### 2) Axios instance

```ts
// src/api/axios.ts
import axios from 'axios';
export const api = axios.create({ baseURL: import.meta.env.VITE_BACKEND_URL || 'http://localhost:8090' });
```

### 3) Service

```ts
// src/services/moodService.ts
import { api } from '../api/axios';
export interface Track { id:string; title:string; artist:string; previewUrl:string; link:string; albumCoverUrl?:string }
export interface MoodResponse { imageUrl?:string; tracks: Track[] }
export async function analyzeMood(mood:string){
  const { data } = await api.post<MoodResponse>('/api/moods/analyze', { mood });
  return data;
}
```

### 4) Hooks

```ts
// src/hooks/useMood.ts
import { useState } from 'react';
import { analyzeMood, MoodResponse } from '../services/moodService';
export function useMood(){
  const [loading,setLoading]=useState(false);
  const [error,setError]=useState<string|null>(null);
  const [result,setResult]=useState<MoodResponse|null>(null);
  async function generate(mood:string){
    try{ setLoading(true); setError(null); setResult(null);
      const r = await analyzeMood(mood); setResult(r);
    }catch(e:any){ setError(e?.response?.data?.error||'Something went wrong'); }
    finally{ setLoading(false); }
  }
  return { loading, error, result, generate };
}
```

### 5) Components

```tsx
// src/components/MoodForm.tsx
import { useState } from 'react';
export default function MoodForm({onSubmit}:{onSubmit:(mood:string)=>void}){
  const [mood,setMood]=useState('');
  return (
    <form className="mood-form" onSubmit={(e)=>{e.preventDefault(); onSubmit(mood)}}>
      <label className="mood-form__label" htmlFor="mood">How are you feeling today?</label>
      <input id="mood" className="mood-form__input" value={mood} onChange={e=>setMood(e.target.value)} placeholder="calm and dreamy"/>
      <button className="mood-form__button" type="submit">Generate</button>
    </form>
  );
}

// src/components/Player.tsx
import { Track } from '../services/moodService';
export default function Player({track}:{track:Track}){
  return (
    <div className="player">
      <img className="player__cover" src={track.albumCoverUrl} alt="album"/>
      <div className="player__meta">
        <div className="player__title">{track.title}</div>
        <div className="player__artist">{track.artist}</div>
        <audio className="player__audio" controls src={track.previewUrl}></audio>
        <a className="player__link" href={track.link} target="_blank">Open in Deezer</a>
      </div>
    </div>
  );
}

// src/components/MoodResult.tsx
import { MoodResponse } from '../services/moodService';
import Player from './Player';
export default function MoodResult({result}:{result:MoodResponse}){
  return (
    <section className="mood-result" style={{backgroundImage: result.imageUrl?`url(${result.imageUrl})`:undefined}}>
      <div className="mood-result__overlay">
        {result.tracks?.[0] ? <Player track={result.tracks[0]} /> : <p>No tracks found.</p>}
      </div>
    </section>
  );
}
```

### 6) Pages & App

```tsx
// src/pages/Home.tsx
import MoodForm from '../components/MoodForm';
import MoodResult from '../components/MoodResult';
import { useMood } from '../hooks/useMood';
export default function Home(){
  const { loading,error,result,generate } = useMood();
  return (
    <main className="mood-app">
      <MoodForm onSubmit={generate} />
      {loading && <p className="mood-app__status">Loading…</p>}
      {error && <p className="mood-app__error">{error}</p>}
      {result && <MoodResult result={result} />}
    </main>
  );
}

// src/App.tsx
import Home from './pages/Home';
import './styles/base.scss';
export default function App(){ return <Home/> }
```

### 7) SCSS (BEM)

```scss
/* styles/base.scss */
@use 'variables';
@use 'mixins';
@use 'components/mood-form';
@use 'components/mood-result';
@use 'components/player';

.mood-app { min-height: 100vh; color: #fff; background: #0f0f12; }
.mood-app__status { padding: 1rem; }
.mood-app__error { padding: 1rem; color: #ffb4b4; }

/* components/_mood-form.scss */
.mood-form { display:flex; gap: .75rem; padding: 1rem; justify-content:center; }
.mood-form__label { position:absolute; left:-9999px; }
.mood-form__input { flex:1; max-width: 420px; padding:.75rem 1rem; border-radius: 10px; border:none; }
.mood-form__button { padding:.75rem 1rem; border-radius: 10px; border:none; cursor:pointer; }

/* components/_mood-result.scss */
.mood-result { min-height: 70vh; background-size:cover; background-position:center; position:relative; }
.mood-result__overlay { backdrop-filter: blur(1.5px); background: rgba(0,0,0,.35); min-height: inherit; display:flex; align-items:center; justify-content:center; }

/* components/_player.scss */
.player { display:flex; gap:1rem; padding:1rem; background: rgba(20,20,24,.6); border-radius:14px; }
.player__cover { width:96px; height:96px; object-fit:cover; border-radius:10px; }
.player__meta { display:flex; flex-direction:column; gap:.5rem; }
.player__title { font-weight:700; }
.player__artist { opacity:.85; }
.player__audio { width: 280px; }
.player__link { color:#e1e1ff; text-decoration:underline; }
```

### 8) Environment var for frontend

```
# frontend/.env.local
VITE_BACKEND_URL=http://localhost:8090
```

### 9) Run frontend

```bash
npm run dev
# open http://localhost:5173
```

---

## 🔗 End‑to‑end Test

1. Run **backend** on port **8090** (with `PEXELS_API_KEY` set).
2. Run **frontend** on port **5173**.
3. Type a mood like `energetic and happy` → **Generate**.
4. You should see a background image + a playable track preview.

---

## 🧪 Testing (backend)

* Unit test `MoodService` to mock clients and assert aggregation.
* Add an integration test with **WebTestClient** against `/api/moods/analyze`.

---

## 🚀 Deployment (optional)

* **Backend**: Railway / Render / Fly.io

    * Provide `PEXELS_API_KEY` env var.
    * Set CORS to allow your frontend domain.
* **Frontend**: Netlify / Vercel

    * Set `VITE_BACKEND_URL` to the deployed backend URL.

---

## 🛠️ Troubleshooting

* **CORS error**: confirm `CorsConfig` allows your origin.
* **No image**: verify `PEXELS_API_KEY` and that Pexels returns results for that mood.
* **No audio**: Deezer sometimes returns tracks without `preview`; we filter them out — try a broader mood keyword.
* **Build issues**: ensure Java 17 SDK is selected in IntelliJ Project Structure.

---

## 🧭 Backlog / Nice‑to‑haves

* Dropdown of suggested moods.
* History of previous moods.
* Toggle between multiple images/tracks.
* Loading skeletons and error toasts.
* Persist favourite tracks.

---

## ✅ Daily Quick‑Start (TL;DR)

1. Start **backend** (IntelliJ) with `PEXELS_API_KEY` set.
2. `cd frontend && npm run dev`.
3. Open app → type mood → Generate → image + 30s preview.

Happy building! 🎧✨
