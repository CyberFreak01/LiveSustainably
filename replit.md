# Live Sustainably - Android App

## Overview
This is a native Android application built with Kotlin and Jetpack Compose. It's a sustainability-focused app with features including:
- User authentication (Firebase)
- Feed/content discovery
- Chatbot functionality
- Leaderboard system
- Google Maps integration
- User profiles

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Build System**: Gradle (Kotlin DSL)
- **Authentication**: Firebase Auth
- **Database**: Firebase Firestore
- **Maps**: Google Maps SDK
- **Dependency Injection**: Hilt
- **Networking**: Ktor
- **Target SDK**: 36 (Android 15)
- **Minimum SDK**: 29 (Android 10)

## Project Structure
```
app/
├── src/main/
│   ├── java/com/superhuman/livesustainably/
│   │   ├── auth/          - Authentication (login, signup)
│   │   ├── chatbot/       - AI chatbot feature
│   │   ├── feed/          - Content feed
│   │   ├── home/          - Home screen
│   │   ├── leaderboard/   - User rankings
│   │   ├── map/           - Google Maps integration
│   │   ├── navigation/    - Navigation components
│   │   ├── profile/       - User profile
│   │   └── ui/theme/      - Material Design theme
│   ├── res/               - Android resources
│   └── assets/            - JSON mock data
└── build.gradle.kts       - App-level build config
```

## Running This Project

### Important Note
This is a **native Android application** and cannot run directly in Replit. To run this app, you need:

1. **Android Studio** (recommended) or IntelliJ IDEA with Android plugin
2. **Android SDK** with API level 36
3. **An Android emulator** or physical Android device (Android 10+)

### Local Development Setup

1. Clone the repository to your local machine
2. Open the project in Android Studio
3. Sync Gradle files
4. Configure Firebase:
   - The `google-services.json` is already included
   - Ensure your Firebase project matches the configuration
5. Run on emulator or device

### Build Commands (requires Android SDK)
```bash
./gradlew build          # Build the project
./gradlew assembleDebug  # Create debug APK
./gradlew test           # Run unit tests
./gradlew ktlintCheck    # Check code style
```

## Firebase Configuration
The app uses Firebase for:
- Authentication (email, Google Sign-In)
- Firestore database

Ensure your Firebase project has these services enabled.

## API Keys Required
- Google Maps API key (configured in AndroidManifest.xml)
- Firebase configuration (google-services.json)
