# LiveSustainably 🌱

A modern Android application designed to promote sustainable living through gamified activities, community engagement, and environmental awareness. Built with cutting-edge Android technologies and best practices.

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Setup & Installation](#setup--installation)
- [Building Release APK](#building-release-apk)
- [Testing](#testing)
- [Known Limitations](#known-limitations)
- [Future Enhancements](#future-enhancements)

---

## 🎯 Project Overview

**LiveSustainably** is an eco-friendly gamified mobile application that encourages users to adopt sustainable habits through:

- **Daily Activities & Quests**: Engage in environmental challenges (Stories, Quizzes, Mobility, Wellness)
- **Streak System**: Build consecutive day streaks to maintain motivation
- **Leaderboard**: Compete with other users and earn recognition
- **User Profiles**: Track personal progress and achievements
- **Community Feed**: Share and discover sustainability tips
- **AI Chatbot**: Get personalized sustainability advice
- **Location-based Features**: Discover nearby sustainable locations (Maps integration)

---

## 🏗️ Architecture

### MVVM (Model-View-ViewModel) Architecture

The application follows the **MVVM architectural pattern**, ensuring clean separation of concerns and testability:

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer (Compose)                   │
│              (Views, Screens, Composables)              │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│                  ViewModel Layer                        │
│  (State Management, Business Logic, User Interactions)  │
│  - AuthViewModel, HomeViewModel, FeedViewModel, etc.    │
└────────────────────┬────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────┐
│                  Repository Layer                       │
│  (Data Abstraction, API Calls, Database Operations)     │
│  - AuthRepository, HomeRepository, FeedRepository, etc. │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        ↓                         ↓
┌──────────────────┐    ┌──────────────────┐
│  Firebase Auth   │    │  Firestore DB    │
│  (Authentication)│    │  (Data Storage)  │
└──────────────────┘    └──────────────────┘
```

### Key MVVM Components

#### **1. ViewModels** (State Management)
- **AuthViewModel**: Handles login, signup, and Google Sign-In
- **SignUpViewModel**: Manages user registration flow
- **HomeViewModel**: Manages home screen state, activities, and streaks
- **FeedViewModel**: Handles community feed data
- **LeaderboardViewModel**: Manages leaderboard rankings
- **ProfileViewModel**: Manages user profile data
- **ChatbotViewModel**: Handles AI chatbot interactions
- **MapViewModel**: Manages location-based features
- **ReportViewModel**: Manages sustainability reports

#### **2. Repositories** (Data Layer)
- **AuthRepository**: Abstract interface for authentication
  - `FirebaseAuthRepository`: Firebase implementation
  - `FakeAuthRepository`: Mock for testing
- **HomeRepository**: Manages home screen data operations
  - `FakeHomeRepository`: Mock for testing
- Additional repositories for Feed, Leaderboard, Profile, etc.

#### **3. State Classes** (UI State)
- `AuthState`: Email, password, loading state, errors
- `SignUpState`: Registration form state
- `HomeState`: Streaks, activities, user stats
- Each state is immutable and managed via `StateFlow`

---

## ✨ Features

### Authentication
- ✅ Email/Password Login & Signup
- ✅ Google Sign-In Integration
- ✅ Firebase Authentication
- ✅ Secure credential storage

### Home Screen
- ✅ Daily activity tracking
- ✅ Weekly streak visualization
- ✅ User statistics (stars, roses, XP)
- ✅ Activity completion tracking
- ✅ Real-time data from Firestore

### Community & Social
- ✅ Leaderboard system
- ✅ User profiles with stats
- ✅ Community feed
- ✅ Sustainability tips sharing

### Additional Features
- ✅ AI Chatbot for sustainability advice
- ✅ Location-based maps (structure ready for API integration)
- ✅ Sustainability reports
- ✅ User logout functionality

---

## 🛠️ Tech Stack

### Core Android & Kotlin
- **Kotlin**: 2.2.0 - Modern, concise language for Android
- **Android Gradle Plugin**: 8.13.1
- **Minimum SDK**: 29 | **Target SDK**: 36

### UI Framework
- **Jetpack Compose**: 2025.07.00 - Modern declarative UI toolkit
  - Material Design 3 components
  - Compose Navigation for routing
  - Constraint Layout for complex layouts

### Architecture & DI
- **Hilt**: 2.56.1 - Dependency injection framework
  - `@HiltViewModel` for ViewModel injection
  - `@AndroidEntryPoint` for Activity/Fragment injection
  - Automatic scope management

### State Management
- **Kotlin Flow**: StateFlow for reactive state management
- **Coroutines**: 1.10.2 - Asynchronous programming
  - `viewModelScope` for lifecycle-aware coroutines
  - `suspend` functions for async operations

### Backend & Database
- **Firebase Authentication**: 23.2.1
  - Email/password authentication
  - Google Sign-In integration
  - User session management
- **Firebase Firestore**: Real-time NoSQL database
  - User profiles and statistics
  - Activity tracking
  - Streak management
  - Community feed data

### Networking
- **Ktor Client**: 3.2.1 - HTTP client for API calls
  - Android engine
  - Content negotiation
  - Logging interceptor
  - Authentication support
- **Kotlin Serialization**: JSON serialization/deserialization
- **GSON**: 2.10.1 - JSON parsing fallback

### Data Storage
- **Jetpack DataStore**: 1.1.7 - Encrypted key-value storage
- **Security Crypto**: 1.1.0-beta01 - Encrypted SharedPreferences

### Location Services
- **Google Play Services Maps**: 18.2.0
- **Google Play Services Location**: 21.1.0
- **Maps Compose**: 4.3.3 - Compose integration for Maps

### Image Loading
- **Coil**: 2.5.0 - Efficient image loading and caching

### Testing
- **JUnit**: 4.13.2 - Unit testing framework
- **Kotlin Coroutines Test**: 1.10.2 - Coroutine testing utilities
  - `StandardTestDispatcher` for controlled test execution
  - `runTest` for suspending test functions
  - `advanceUntilIdle` for test synchronization
- **AndroidX Test**: JUnit, Espresso for instrumented tests
- **Compose UI Test**: UI testing for Compose components

### Code Quality
- **KtLint**: 13.0.0 - Kotlin linter and formatter

---

## 📁 Project Structure

```
LiveSustainably/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/superhuman/livesustainably/
│   │   │   │   ├── MainActivity.kt                 # Entry point
│   │   │   │   ├── MainViewModel.kt                # Auth state management
│   │   │   │   ├── AppNavigator.kt                 # Navigation setup
│   │   │   │   ├── AppModule.kt                    # Global DI module
│   │   │   │   ├── LiveSustainablyApp.kt           # Application class
│   │   │   │   │
│   │   │   │   ├── auth/                           # Authentication module
│   │   │   │   │   ├── AuthView.kt                 # Login screen
│   │   │   │   │   ├── AuthViewModel.kt            # Login logic
│   │   │   │   │   ├── SignUpView.kt               # Registration screen
│   │   │   │   │   ├── SignUpViewModel.kt          # Registration logic
│   │   │   │   │   ├── GoogleSignInHelper.kt       # Google Sign-In
│   │   │   │   │   ├── AuthModule.kt               # Auth DI module
│   │   │   │   │   └── data/
│   │   │   │   │       ├── AuthRepository.kt       # Auth interface
│   │   │   │   │       └── FirebaseAuthRepository.kt # Firebase impl
│   │   │   │   │
│   │   │   │   ├── home/                           # Home screen module
│   │   │   │   │   ├── HomeView.kt                 # Home screen UI
│   │   │   │   │   ├── HomeViewModel.kt            # Home logic
│   │   │   │   │   └── data/
│   │   │   │   │       ├── HomeRepository.kt       # Home data ops
│   │   │   │   │       └── HomeModule.kt           # Home DI module
│   │   │   │   │
│   │   │   │   ├── feed/                           # Feed module
│   │   │   │   │   ├── FeedView.kt                 # Feed screen
│   │   │   │   │   └── FeedViewModel.kt            # Feed logic
│   │   │   │   │
│   │   │   │   ├── leaderboard/                    # Leaderboard module
│   │   │   │   │   ├── LeaderboardView.kt          # Leaderboard UI
│   │   │   │   │   └── LeaderboardViewModel.kt     # Leaderboard logic
│   │   │   │   │
│   │   │   │   ├── profile/                        # Profile module
│   │   │   │   │   ├── ProfileView.kt              # Profile screen
│   │   │   │   │   └── ProfileViewModel.kt         # Profile logic
│   │   │   │   │
│   │   │   │   ├── chatbot/                        # Chatbot module
│   │   │   │   │   ├── ChatbotView.kt              # Chatbot UI
│   │   │   │   │   └── ChatbotViewModel.kt         # Chatbot logic
│   │   │   │   │
│   │   │   │   ├── map/                            # Maps module
│   │   │   │   │   ├── MapView.kt                  # Map screen
│   │   │   │   │   └── MapViewModel.kt             # Map logic
│   │   │   │   │
│   │   │   │   ├── report/                         # Reports module
│   │   │   │   │   ├── ReportView.kt               # Report screen
│   │   │   │   │   └── ReportViewModel.kt          # Report logic
│   │   │   │   │
│   │   │   │   ├── navigation/                     # Navigation
│   │   │   │   │   └── UnifiedNavigationBar.kt     # Bottom nav
│   │   │   │   │
│   │   │   │   ├── ui/theme/                       # UI Theming
│   │   │   │   │   ├── Theme.kt                    # Material theme
│   │   │   │   │   ├── Color.kt                    # Color palette
│   │   │   │   │   ├── Type.kt                     # Typography
│   │   │   │   │   └── LiveSustainablyPreview.kt   # Preview helpers
│   │   │   │   │
│   │   │   │   └── utils/                          # Utilities
│   │   │   │       └── OneTimeEvent.kt             # Event wrapper
│   │   │   │
│   │   │   └── res/                                # Resources
│   │   │       ├── drawable/                       # Icons & images
│   │   │       ├── values/                         # Strings, colors
│   │   │       └── AndroidManifest.xml             # App manifest
│   │   │
│   │   └── test/                                   # Unit tests
│   │       └── java/com/superhuman/livesustainably/
│   │           ├── auth/
│   │           │   ├── AuthViewModelTest.kt        # Login tests
│   │           │   ├── SignUpViewModelTest.kt      # Signup tests
│   │           │   ├── FakeAuthRepository.kt       # Mock auth repo
│   │           │   └── FakeFirebaseUser.kt         # Mock user
│   │           └── home/
│   │               ├── HomeViewModelTest.kt        # Home tests
│   │               └── FakeHomeRepository.kt       # Mock home repo
│   │
│   └── build.gradle.kts                            # App-level config
│
├── gradle/
│   └── libs.versions.toml                          # Dependency versions
│
├── build.gradle.kts                                # Project-level config
├── settings.gradle.kts                             # Project settings
├── gradle.properties                               # Gradle properties
├── local.properties                                # Local SDK path
└── README.md                                       # This file
```

---

## 🚀 Setup & Installation

### Prerequisites
- **Android Studio**: Latest version (Koala or newer)
- **JDK**: 11 or higher
- **Android SDK**: API 29+ (minimum), API 36 (target)
- **Google Play Services**: For Google Sign-In
- **Firebase Project**: For authentication and Firestore

### Step 1: Clone the Repository
```bash
git clone <repository-url>
cd LiveSustainably
```

### Step 2: Firebase Setup
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Add Android app to your Firebase project
3. Download `google-services.json` and place it in `app/` directory
4. Enable Firebase Authentication (Email/Password & Google Sign-In)
5. Create Firestore database in test mode (or configure security rules)

### Step 3: Google Sign-In Setup
1. Get SHA-1 fingerprint:
   ```bash
   ./gradlew signingReport
   ```
2. Add fingerprint to Firebase Console (Project Settings → Your apps)
3. Create OAuth 2.0 Client ID (Android type) in Google Cloud Console
4. Add Web Client ID to `strings.xml` or configuration

### Step 4: Build & Run
```bash
# Debug build
./gradlew installDebug

# Or use Android Studio: Run → Run 'app'
```

---

## 📦 Building Release APK

### ⚠️ Important: Test Dependencies Configuration

**The Issue**: Test dependencies like `kotlinx.coroutines.test` were causing build failures in release APK builds because they were not properly scoped.

**The Solution**: We've explicitly added test dependencies with `testImplementation` scope in `app/build.gradle.kts`:

```kotlin
// Testing
testImplementation(libs.junit)
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
androidTestImplementation(libs.androidx.junit)
// ... other test dependencies
```

This ensures test libraries are **only included in debug builds and unit tests**, not in the release APK.

### Building Release APK

#### Option 1: Using Gradle (Recommended)
```bash
# Build release APK
./gradlew assembleRelease

# Build and sign release APK
./gradlew assembleRelease -Pandroid.injected.signing.store.file=<keystore-path> \
  -Pandroid.injected.signing.store.password=<store-password> \
  -Pandroid.injected.signing.key.alias=<key-alias> \
  -Pandroid.injected.signing.key.password=<key-password>
```

#### Option 2: Using Android Studio
1. **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Select **Release** build variant
3. APK will be generated in `app/build/outputs/apk/release/`

#### Option 3: Build App Bundle (For Play Store)
```bash
./gradlew bundleRelease
```

### Signing Configuration

Create or update `keystore.properties` in project root:
```properties
storeFile=<path-to-keystore>
storePassword=<store-password>
keyAlias=<key-alias>
keyPassword=<key-password>
```

Then in `app/build.gradle.kts`:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### Verification
After building, verify the APK:
```bash
# Check APK contents
unzip -l app/build/outputs/apk/release/app-release.apk | grep "kotlinx/coroutines/test"
# Should return NO results - test dependencies should not be present
```

---

## 🧪 Testing

### Unit Tests

The project includes comprehensive unit tests using **JUnit** and **Kotlin Coroutines Test**:

#### Test Files
- `AuthViewModelTest.kt`: Tests login/signup logic
- `SignUpViewModelTest.kt`: Tests registration flow
- `HomeViewModelTest.kt`: Tests home screen state
- `FakeAuthRepository.kt`: Mock authentication
- `FakeHomeRepository.kt`: Mock home data

#### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests AuthViewModelTest

# Run specific test method
./gradlew test --tests AuthViewModelTest.onEmailLoginClicked*

# Run with detailed output
./gradlew test --info
```

#### Test Coverage

```bash
# Generate coverage report
./gradlew testDebugUnitTestCoverage

# View report
open app/build/reports/coverage/index.html
```

### Test Architecture

Tests use **fake repositories** for isolation:

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private lateinit var viewModel: AuthViewModel
    private lateinit var mockAuthRepository: FakeAuthRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)  // Replace main dispatcher
        mockAuthRepository = FakeAuthRepository()
        viewModel = AuthViewModel(mockAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()  // Restore main dispatcher
    }

    @Test
    fun `onEmailLoginClicked with valid credentials starts loading`() = runTest {
        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.onEmailLoginClicked()
        
        assertTrue(viewModel.state.value.isLoading)
    }
}
```

### Instrumented Tests

```bash
# Run instrumented tests on device/emulator
./gradlew connectedAndroidTest
```

---

## ⚠️ Known Limitations

### 🗺️ Google Maps Integration
**Status**: Structure defined, API integration pending

**Current State**:
- Map screen UI is fully implemented (`MapView.kt`, `MapViewModel.kt`)
- Google Play Services Maps dependencies are included
- Location services are configured

**Limitation**: 
- Maps display is simulated/mocked due to lack of active Google Maps API key
- Real location data is not fetched

**How to Enable**:
1. **Get Google Maps API Key**:
   - Go to [Google Cloud Console](https://console.cloud.google.com)
   - Create a new project
   - Enable Maps SDK for Android
   - Create an API key (Android type)
   - Add your app's SHA-1 fingerprint

2. **Add to AndroidManifest.xml**:
   ```xml
   <application>
       <meta-data
           android:name="com.google.android.geo.API_KEY"
           android:value="YOUR_API_KEY_HERE" />
   </application>
   ```

3. **Update MapViewModel.kt**:
   ```kotlin
   // Replace mock location with real API calls
   private fun fetchNearbyLocations() {
       // Implement actual location fetching
   }
   ```

4. **Request Permissions** in AndroidManifest.xml:
   ```xml
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
   ```

### 🔐 Firebase Configuration
**Status**: Requires user setup

**Required Setup**:
- Firebase project creation
- Firestore database initialization
- Authentication method configuration
- Security rules configuration

### 📡 Mock API Data
**Status**: Using mock JSON API

**Current Implementation**:
- Activities data is mocked in `HomeRepository`
- Feed data is simulated
- Leaderboard data is placeholder

**To Connect Real API**:
- Replace mock data calls with actual Ktor HTTP requests
- Implement proper error handling
- Add request/response interceptors

---

## 🔮 Future Enhancements

### Short Term
- [ ] Implement real Google Maps API integration
- [ ] Add push notifications for daily reminders
- [ ] Implement offline-first data sync
- [ ] Add user avatar upload functionality

### Medium Term
- [ ] Implement real-time chat for community
- [ ] Add achievement/badge system
- [ ] Implement social sharing features
- [ ] Add dark mode support

### Long Term
- [ ] Machine learning for personalized recommendations
- [ ] Integration with wearable devices
- [ ] Augmented Reality (AR) features
- [ ] Multi-language support (i18n)
- [ ] Web dashboard for analytics

---

## 📊 API/Libraries Summary

| Category | Library | Version | Purpose |
|----------|---------|---------|---------|
| **UI** | Jetpack Compose | 2025.07.00 | Modern declarative UI |
| **DI** | Hilt | 2.56.1 | Dependency injection |
| **State** | Kotlin Flow | 1.10.2 | Reactive state management |
| **Async** | Coroutines | 1.10.2 | Asynchronous operations |
| **Auth** | Firebase Auth | 23.2.1 | User authentication |
| **Database** | Firestore | Latest | Real-time database |
| **Networking** | Ktor | 3.2.1 | HTTP client |
| **Storage** | DataStore | 1.1.7 | Encrypted preferences |
| **Maps** | Google Maps | 18.2.0 | Location features |
| **Images** | Coil | 2.5.0 | Image loading |
| **Testing** | JUnit | 4.13.2 | Unit testing |
| **Testing** | Coroutines Test | 1.10.2 | Coroutine testing |

---

## 📄 License

This project is licensed under the MIT License - see LICENSE file for details.

---