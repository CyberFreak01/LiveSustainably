# Live Sustainably - Android App

## Overview
Live Sustainably is a native Android application built with Kotlin and Jetpack Compose. The app helps users make smarter daily choices for sustainable living, track their CO2e savings, and compete with friends on a leaderboard.

**Current State**: Development (code can be edited but requires Android Studio for building APKs)

**Note**: This is a native Android app and cannot run in a web browser. The full Android SDK is not available in Replit. To build and test the APK, export the project to Android Studio.

## Project Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material3
- **Dependency Injection**: Hilt
- **Backend**: Firebase (Auth, Firestore)
- **Networking**: Ktor Client
- **Build System**: Gradle (Kotlin DSL)
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 36

### Directory Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/superhuman/livesustainably/
│   │   │   ├── auth/           # Authentication (Sign In/Sign Up)
│   │   │   ├── chatbot/        # AI Chatbot feature
│   │   │   ├── feed/           # Stories/Feed feature
│   │   │   ├── home/           # Home screen with activities
│   │   │   ├── leaderboard/    # Competitive leaderboard
│   │   │   ├── map/            # Location-based features
│   │   │   ├── navigation/     # App navigation
│   │   │   ├── profile/        # User profile
│   │   │   ├── report/         # Sustainability reports
│   │   │   └── ui/theme/       # App theming
│   │   ├── assets/             # Mock JSON data files
│   │   └── res/                # Android resources
│   └── test/                   # Unit tests
├── build.gradle.kts            # App module build config
└── google-services.json        # Firebase config
```

## Recent Changes (December 2024)

### Responsive UI Improvements
- **HomeView**: Added collapsing app bar with curved edges, responsive layout for different screen sizes
- **LeaderboardView**: Added collapsing app bar, responsive layout adapts to compact screens
- **AuthView & SignUpView**: Made fully scrollable with responsive elements, fixed button visibility on smaller screens
- **FeedView (Stories)**: Added collapsing app bar header with gradient design

### Android 15 Compatibility
- Created `GoogleSignInHelper.kt` utility for improved Credential Manager handling
- Added proper error handling for Android 15 specific issues
- Implemented credential state clearing for retry scenarios

### Unit Tests
- `AuthViewModelTest.kt` - Authentication flow tests
- `SignUpViewModelTest.kt` - Sign up validation tests  
- `HomeViewModelTest.kt` - Home data loading tests
- `LeaderboardViewModelTest.kt` - Leaderboard and follow functionality tests
- `FeedViewModelTest.kt` - Feed interactions (like, comment) tests

## Key Features
1. **Home Dashboard**: Activity tracking, streak management, XP system
2. **Leaderboard**: Weekly competitions with leagues
3. **Stories Feed**: Sustainability content with likes/comments
4. **Authentication**: Email/password and Google Sign-In via Credential Manager
5. **Maps**: Location-based eco-friendly route tracking

## Build Commands
```bash
# Check Gradle version
./gradlew --version

# Attempt build (requires Android SDK)
./gradlew assembleDebug

# Run unit tests (may work without full SDK)
./gradlew test
```

## Firebase Configuration
The app uses Firebase for authentication and data storage. The `google-services.json` file contains the Firebase project configuration.

## User Preferences
- None specified yet

## Notes for Development
- Collapsing headers use `rememberLazyListState()` to track scroll position
- Responsive breakpoints: < 360dp (compact), 360-600dp (normal), > 600dp (large)
- All screens support edge-to-edge display with proper padding
