# Daily Routine Tracker + Groq AI

A high-performance, modern Android application designed to help users master their daily habits through intuitive tracking, smart analytics, and an integrated AI productivity coach powered by **Groq (Llama 3)**.

---

## Features

### Smart AI Coaching (Groq Powered)
- **Real-time Motivation**: A dashboard AI banner that provides context-aware productivity tips based on your current task load.
- **Intelligent Chatbot**: A dedicated *Ask Assistant* floating interface to help users plan their day or solve routine bottlenecks.
- **Performance Analysis**: AI analyzes completion history and provides personalized insights to improve success rates.

### Premium Glassmorphism UI
- **Ambient Aesthetics**: Fluid background blobs and translucent glass cards for a modern, premium look.
- **Dynamic UX**: Personalized headers that update based on user name, profile image, and daily progress.
- **Smooth Navigation**: CoordinatorLayout with custom scroll behaviors for a polished experience.

### Advanced Analytics
- **Weekly Trends**: Custom `WeeklyAnalyticsView` visualizing consistency over the last 7 days.
- **Real-time Stats**: Instant tracking of total tasks, completed tasks, and success percentages.
- **Expandable History**: Detailed archive of past performance with structured reporting.

### Productivity Tools
- **Smart Reminders**: Task notifications using `AlarmManager` (work in progress).
- **Theme Switching**: Native Dark / Light mode support using Material 3.

---

## Built with AI Collaboration

This application represents a modern approach to **AI-Augmented Development**.

- **Developer**: Abel Berhane  
- **AI Co-Developer**: Gemini  

**Collaboration Scope:**
- Assisted in architecting the Glassmorphism design system  
- Optimized the Groq API Retrofit implementation  
- Troubleshot complex Android layout behaviors to achieve a premium *Ladybug-standard* UI  

---

## Tech Stack

- **Language**: Kotlin  
- **UI Framework**: Material 3, XML (Modern Layouts), Jetpack Compose (ready)  
- **AI Engine**: Groq Cloud API (Llama 3-8b-8192)  
- **Networking**: Retrofit 2, OkHttp 4  
- **Image Handling**: Glide  
- **Data Persistence**: SharedPreferences, GSON  
- **Architecture**: MVVM (Model-View-ViewModel)

---

## Getting Started

### Prerequisites
- Android Studio Ladybug or newer  
- Minimum SDK: 24 (Android 7.0)  
- Groq API Key (from Groq Cloud Console)

### Installation

#### Clone the repository
```bash
git clone https://github.com/yourusername/DailyRoutineTracker.git



Build & Run

Sync Gradle and deploy to an emulator or physical device.

Project Structure
app/src/main/
├── java/.../dailyroutinetracker/
│   ├── api/        # Retrofit clients & Groq endpoints
│   ├── model/      # Task, DailyStats, and AI models
│   ├── ui/         # Custom WeeklyAnalyticsView logic
│   └── MainActivity.kt
└── res/layout/
    ├── activity_main.xml       # Glassmorphism dashboard
    ├── list_item_task.xml      # Modern task row UI
    └── list_item_history.xml   # Expandable analytics cards

Contributing

Contributions are welcome and appreciated.

Fork the project

Create your feature branch

git checkout -b feature/AmazingFeature


Commit your changes

git commit -m "Add AmazingFeature"


Push to the branch

git push origin feature/AmazingFeature


Open a Pull Request

License

Distributed under the MIT License.
See LICENSE for more information.

Contact

Abel Berhane
GitHub: https://github.com/Abel-Berhane-web
