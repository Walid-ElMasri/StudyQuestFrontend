# 📱 StudyQuest Frontend  
### Native Android App (Java + XML) | FastAPI Backend | Productivity Gamification

This repository contains the **Android mobile frontend** for **StudyQuest**, a gamified study-productivity system.  
The app communicates with a **FastAPI backend** hosted on Vercel and provides:

- User dashboard  
- XP & level progress tracking  
- Daily boss battles  
- AI Text Mentor (server-based OpenAI assistant)

---

## 🧩 Project Structure

```
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
```

---

## 🚀 Features

### 1. User Dashboard  
Fetches user profile data using:

```
GET /users/{username}
```

Displays username, XP, and join date.

---

### 2. Progress Tracking  
Retrieves level, streak, XP trends:

```
GET /progress/?user={username}
```

---

### 3. Daily Boss Battle  

```
POST /boss/start
```

Simulates a battle and returns boss stats, user HP, and XP reward.

---

### 4. AI Text Mentor  

```
POST /text-ai
```

Uses server-based OpenAI to generate explanations, study tips, or answers.

---

## 🛠️ Technology Stack

### Frontend  
- Android Studio  
- Java  
- XML Layouts  
- Retrofit2  
- Gson  
- OkHttp Logging Interceptor  

### Backend  
- FastAPI  
- PostgreSQL  
- Vercel serverless deployment  
- OpenAI API (server-side)

---

## 🔗 Backend API Base URL

```
https://study-quest-mobile-app.vercel.app/
```

Configured in:

`api/RetrofitClient.java`:

```java
private static final String BASE_URL =
    "https://study-quest-mobile-app.vercel.app/";
```

---

## 📲 Running the App

Clone the repository:

```
git clone https://github.com/Walid-ElMasri/StudyQuestFrontend.git
```

Then:

1. Open the folder in **Android Studio**  
2. Let Gradle sync  
3. Run on emulator or physical device  
4. Test the interactive UI screens  

---

## 🧪 Example API Requests

### Get User
```
GET /users/demo
```

### Start Boss Battle
```
POST /boss/start
{
  "user": "demo"
}
```

### Ask AI
```
POST /text-ai
{
  "prompt": "Explain recursion simply"
}
```

---

## 🧠 Design Choice: Server-Based AI  
AI runs on the backend for efficiency, consistent updates, and reduced device load.

---

## 📄 License  
MIT License
