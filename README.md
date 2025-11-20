# 📱 StudyQuestFrontend  
Android mobile app frontend for the StudyQuest productivity & gamification platform.  
Built using Java + Android Studio and fully integrated with the StudyQuest FastAPI backend.

The app allows students to track progress, fight daily quiz bosses, communicate with an AI mentor, manage avatars/cosmetics, and access every backend endpoint through an API Playground — perfect for CMPS279.

---

## 🚀 Features

### ✅ User System
- Register user
- Fetch user profile
- Display XP, join date, streaks

### 📊 Progress Tracking
- Log study sessions
- View full session history
- View progress statistics

### 🎮 Quests & Levels
- Create quests (admin/testing)
- Complete quests
- Fetch level + XP-to-next

### 🧸 Cosmetics & Rewards
- Create/update avatar
- Fetch avatar
- List badges
- Create badges (admin/testing)

### 🤖 AI Text Mentor
- Send reflections to AI
- Receive summaries + XP rewards
- View reflection history

### 🧠 Boss Battle System
- Start boss battle
- Submit answers
- Check battle status

### 👥 Social Features
- Add friends
- Accept/decline friend requests
- View XP leaderboard

### 🔧 API Playground (NEW)
A dedicated screen exposing **all backend endpoints**.  
Choose endpoint → Fill inputs → Send → View JSON response.  
Guarantees full backend accessibility for demo.

---

## 🗂️ Folder Structure

StudyQuestFrontend/
│
├── app/
│   ├── src/main/java/com/example/studyquest/
│   │   ├── api/                         # Retrofit interfaces
│   │   ├── models/                      # Request/response models
│   │   ├── MainActivity.java            # Home menu
│   │   ├── HomeActivity.java
│   │   ├── ProgressActivity.java
│   │   ├── BossActivity.java
│   │   ├── TextAiActivity.java
│   │   ├── ApiPlaygroundActivity.java   # NEW
│   │   └── ...
│   │
│   ├── res/layout/
│   │   ├── activity_main.xml
│   │   ├── activity_home.xml
│   │   ├── activity_progress.xml
│   │   ├── activity_text_ai.xml
│   │   ├── activity_boss.xml
│   │   └── activity_api_playground.xml  # NEW
│   │
│   ├── AndroidManifest.xml
│   └── ...
│
├── build.gradle.kts
├── settings.gradle.kts
└── ...

---

## 🔌 API Integration (All Endpoints Included)

### User
POST /users/  
GET /users/  
GET /users/{username}

### Dashboard
GET /home/dashboard

### Progress
POST /progress/  
GET /progress/?user=  
GET /progress/stats?user=

### Quests
POST /quests/  
PUT /quests/{id}/complete  
GET /quests/level/{username}

### Cosmetics
POST /cosmetics/avatar  
GET /cosmetics/avatar/{user}  
POST /cosmetics/badge  
GET /cosmetics/badges

### AI Mentor
POST /text-ai  
GET /text-ai/?user=

### Boss Battle
POST /boss/start  
POST /boss/answer  
GET /boss/status?user=

### Social
POST /social/friends/add  
PATCH /social/friends/respond  
GET /social/leaderboard

---

## 🛠️ Technology Stack
- Java (Android)
- Retrofit2 + Gson
- XML layouts
- FastAPI backend
- PostgreSQL DB
- OpenAI API (AI mentor)

---

## 🎓 CMPS279 Project Summary
This project demonstrates:
- Full mobile frontend implementation  
- Complete backend integration  
- AI integration using OpenAI  
- Use of API keys and secure communication  
- Full CRUD capabilities through API Playground  
- Gamification, XP, quests, boss battles, and social features  

---

## 👨‍💻 Authors
Walid ElMasri  
American University of Beirut (AUB)  
CMPS279 – Web Programming  

