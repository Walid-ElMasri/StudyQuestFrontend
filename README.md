📱 StudyQuest Frontend
Native Android App (Java + XML) | FastAPI Backend | Productivity Gamification

This repository contains the Android mobile frontend for StudyQuest, a gamified study-productivity system.
The app communicates with a FastAPI backend hosted on Vercel and provides:

User dashboard

XP & Level progress tracking

Daily boss battles

AI Text Mentor (server-based OpenAI assistant)

🧩 Project Structure
app/src/main/java/com/example/studyquest/
│
├── MainActivity.java
├── HomeActivity.java
├── ProgressActivity.java
├── BossActivity.java
├── TextAiActivity.java
│
├── api/
│   ├── RetrofitClient.java
│   └── ApiService.java
│
└── models/
    ├── User.java
    ├── ProgressResponse.java
    ├── BossStartRequest.java
    ├── BossStartResponse.java
    ├── TextAiRequest.java
    └── TextAiResponse.java

app/src/main/res/layout/
│
├── activity_main.xml
├── activity_home.xml
├── activity_progress.xml
├── activity_boss.xml
└── activity_text_ai.xml

🚀 Features
1. User Dashboard

Fetches user profile data using:

GET /users/{username}


Displays username, XP, and join date.

2. Progress Tracking

Retrieves XP, level, and streak data:

GET /progress/?user={username}

3. Daily Boss Battle
POST /boss/start


Displays boss stats, user HP, and XP rewards.

4. AI Text Mentor
POST /text-ai


Uses the backend’s OpenAI integration to generate responses for study support.

🛠️ Technology Stack
Frontend

Android Studio

Java

XML Layouts

Retrofit2

Gson Converter

OkHttp Interceptor

Backend

FastAPI

PostgreSQL

Vercel serverless deployment

Server-based OpenAI (not on-device)

🔗 Backend API

Base URL:

https://study-quest-mobile-app.vercel.app/


Configured in RetrofitClient.java:

private static final String BASE_URL =
    "https://study-quest-mobile-app.vercel.app/";

📲 Running the App
git clone https://github.com/Walid-ElMasri/StudyQuestFrontend.git


Open in Android Studio

Let Gradle sync

Run on emulator or physical Android device

Test endpoints through the UI

🧪 Example API Requests
Get user
GET /users/demo

Start a boss battle
POST /boss/start
{
  "user": "demo"
}

Ask AI
POST /text-ai
{
  "prompt": "Explain recursion simply"
}

🧠 Design Choice: Server-Based AI

The AI runs on the backend instead of on-device to reduce resource usage and centralize model access and safety.

📄 License

MIT License
