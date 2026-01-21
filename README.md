🚀 Daily Routine Tracker + Groq AI
A high-performance, modern Android application designed to help users master their daily habits through intuitive tracking, smart analytics, and an integrated AI productivity coach powered by Groq (Llama 3).
License Platform AI UI
✨ Features
🧠 Smart AI Coaching (Groq Powered)
•
Real-time Motivation: A dashboard AI banner that provides context-aware productivity tips based on your current task load.
•
Intelligent Chatbot: A dedicated "Ask Assistant" floating interface to help you plan your day or solve routine bottlenecks.
•
Performance Analysis: AI analyzes your completion history to provide personalized insights on how to improve your success rate.
🎨 Premium Glassmorphism UI
•
Ambient Aesthetics: Features fluid background blobs and translucent "Glass" cards for a high-end, modern look.
•
Dynamic UX: Personalized headers that update based on the user's name, profile image, and daily progress.
•
Smooth Navigation: Integrated CoordinatorLayout with custom ScrollView behaviors for a professional app feel.
📊 Advanced Analytics
•
Weekly Trends: A custom-built WeeklyAnalyticsView to visualize your consistency over the last 7 days.
•
Real-time Stats: Instant tracking of total tasks, finished tasks, and success percentages.
•
Expandable History: A detailed archive of past performance with smart reporting.
⏰ Productivity Tools
•
Smart Reminders: Integrated AlarmManager for time-specific task notifications (Work-in-progress).
•
Theme Switching: Native support for Dark/Light modes via Material 3.
🤖 Built with AI Collaboration
This application represents a modern approach to software engineering: AI-Augmented Development.
•
Developer: Abel Berhane
•
AI Co-Developer: gemini
•
Collaboration Scope: The AI assisted in architecting the Glassmorphism design system, optimizing the Groq API Retrofit implementation, and troubleshooting complex Android layout behaviors to achieve a premium "Ladybug" standard UI.
🛠 Tech Stack
•
Language: Kotlin / Java
•
UI Framework: Material 3, Jetpack Compose (ready), XML (Modern Layouts)
•
AI Engine: Groq Cloud API (Llama 3-8b-8192)
•
Networking: Retrofit 2 & OkHttp 4
•
Image Handling: Glide (for profile and dynamic images)
•
Data Persistence: SharedPreferences & GSON
•
Architecture: MVVM (Model-View-ViewModel)
🚀 Getting Started
Prerequisites
•
Android Studio Ladybug (or newer)
•
Min SDK: 24 (Android 7.0)
•
A Groq API Key (Obtain one at Groq Cloud Console)
Installation
1.
Clone the repository:
Shell Script
git clone https://github.com/yourusername/DailyRoutinetracker.git
2.
Setup API Key: Open MainActivity.kt and insert your key:
Kotlin
private const val GROQ_API_KEY = "your_key_here"
3.
Build & Run: Sync Gradle and deploy to your device or emulator.
📂 Project Structure
Java
app/src/main/
├── java/.../dailyroutinetracker/
│   ├── api/             # Retrofit clients & Groq endpoints
│   ├── model/           # Task, DailyStats, and AI models
│   ├── ui/              # Custom WeeklyAnalyticsView logic
│   └── MainActivity.kt  # Central logic for AI and Tracking
└── res/layout/
    ├── activity_main.xml    # The primary Glassmorphism dashboard
    ├── list_item_task.xml   # Modern task row design
    └── list_item_history.xml # Expandable analytics cards
🤝 Contributing
Contributions make the open-source community an amazing place to learn and create.
1.
Fork the Project.
2.
Create your Feature Branch (git checkout -b feature/AmazingFeature).
3.
Commit your Changes (git commit -m 'Add some AmazingFeature').
4.
Push to the Branch (git push origin feature/AmazingFeature).
5.
Open a Pull Request.
📄 License
Distributed under the MIT License. See LICENSE for more information.
📬 Contact
Abel Berhane
Project Link: 
