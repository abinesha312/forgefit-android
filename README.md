# ForgeFit (Android)

**ForgeFit** is an open-source gym workout tracker for Android, inspired by Lyfta-style fitness apps. Track your workouts, monitor progress, and achieve your fitness goals with personalized workout plans.

**Not affiliated with Lyfta or any commercial fitness app.**

## Features

### ✅ Core Features
- **Personalized Onboarding**: Multi-step setup collecting display name, age, height, weight, fitness goal, experience level, available equipment, and training preferences
- **Smart Workout Plans**: Rule-based plan engine that generates personalized weekly workout programs based on your profile
  - Full Body, Upper/Lower, Push/Pull/Legs, and 4-Day splits
  - Automatic exercise selection based on available equipment
  - Sets, reps, and rest periods tailored to your goal (strength, hypertrophy, fat loss, etc.)
- **Exercise Library**: 50+ exercises with detailed instructions
  - Searchable and filterable
  - Covers barbell, dumbbell, machine, cable, and bodyweight movements
  - Add custom exercises
  - **Note**: Exercise data compiled from public domain sources (CC BY 4.0 license)
- **Workout Logging**: Active workout tracking with:
  - Set-by-set logging (weight, reps, RPE)
  - Rest timer
  - Historical performance data
  - Personal record (PR) detection
  - Volume tracking
- **Progress Tracking**:
  - Personal records for each exercise (1RM estimates, max reps, best set, volume)
  - Body weight log with history
  - Workout streak counter
  - Recent workout history
- **Profile Management**: View and edit user profile, manage routines and settings
- **Android Health Connect Integration**:
  - Write ExerciseSession records on workout completion
  - Read weight and height data
  - Graceful handling when permissions denied or Health Connect unavailable

### 🎨 UI/UX
- Modern Material 3 design
- Dark theme support (dynamic theming on Android 12+)
- Bottom navigation with 5 tabs: Home | Workout | Exercises | Progress | Profile
- Responsive and intuitive interface

### 🚧 Future Enhancements (Stubbed)
- Wear OS companion app
- Social features (sharing workouts, comparing progress)

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (local persistence)
- **Navigation**: Navigation Compose
- **Async**: Kotlin Coroutines + Flow
- **Health Integration**: Android Health Connect SDK
- **Charts**: Vico (for progress visualization)
- **Build**: Gradle Kotlin DSL

**Minimum SDK**: 26 (Android 8.0)  
**Target SDK**: 34 (Android 14)

## Project Structure

```
app/
├── src/main/
│   ├── assets/
│   │   └── exercises.json          # Exercise library seed data
│   ├── java/com/forgefit/android/
│   │   ├── data/
│   │   │   ├── db/                 # Room database, DAOs, converters
│   │   │   ├── model/              # Data classes (entities)
│   │   │   ├── repository/         # Repository layer
│   │   │   └── healthconnect/      # Health Connect integration
│   │   ├── domain/                 # Business logic
│   │   │   ├── WorkoutPlanEngine.kt
│   │   │   └── PersonalRecordDetector.kt
│   │   ├── ui/                     # Compose UI
│   │   │   ├── onboarding/
│   │   │   ├── home/
│   │   │   ├── workout/
│   │   │   ├── exercises/
│   │   │   ├── progress/
│   │   │   ├── profile/
│   │   │   ├── navigation/
│   │   │   └── theme/
│   │   ├── ForgeFitApplication.kt
│   │   └── MainActivity.kt
│   └── res/                        # Resources (strings, themes, icons)
├── build.gradle.kts
└── proguard-rules.pro
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- Android SDK with API 34

### Setup
1. **Clone the repository**:
   ```bash
   git clone https://github.com/abinesha312/forgefit-android.git
   cd forgefit-android
   ```

2. **Open in Android Studio**:
   - `File > Open` → select the `forgefit-android` folder
   - Wait for Gradle sync to complete

3. **Build the project**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on device/emulator**:
   - Connect an Android device (API 26+) or start an emulator
   - Click "Run" in Android Studio or:
     ```bash
     ./gradlew installDebug
     ```

### Health Connect Setup (Optional)
1. Install **Health Connect** from the Play Store (Android 14+) or sideload for testing
2. Grant ForgeFit the following permissions in Health Connect settings:
   - Read/Write Exercise Sessions
   - Read/Write Weight
   - Read Height
3. ForgeFit will automatically sync completed workouts to Health Connect

## Workout Planning

The plan engine generates workouts based on:
- **Training Days**: 2-6 days per week
  - 2 days → Full Body
  - 3 days → Full Body or Push/Pull/Legs
  - 4 days → Upper/Lower
  - 5-6 days → Push/Pull/Legs
- **Goal**:
  - Strength → Lower reps (3-5), longer rest (3-4 min)
  - Hypertrophy → Medium reps (8-12), moderate rest (60-90s)
  - Fat Loss → Higher reps (12-15), shorter rest (45-60s)
- **Equipment**: Filters exercises to match what you have available
- **Experience**: Adjusts volume (sets per exercise)

## Exercise Library

- **Current count**: 50 exercises (honest count)
- **Categories**: Barbell, dumbbell, machine, cable, bodyweight
- **Muscle groups**: All major and minor muscle groups covered
- **Attribution**: Data sourced from public domain fitness resources
- **License**: CC BY 4.0

To expand the library, edit `app/src/main/assets/exercises.json` and rebuild.

## Testing

Run unit tests:
```bash
./gradlew test
```

Key test coverage:
- `WorkoutPlanEngineTest`: Validates workout plan generation logic
- `PersonalRecordDetectorTest`: Ensures PR detection accuracy

## Contributing

Contributions welcome! Please:
1. Fork the repo
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## Disclaimer

ForgeFit is an independent open-source project. It is **not affiliated with, endorsed by, or connected to Lyfta** or any other commercial fitness application.

## Acknowledgments

- Exercise data compiled from public fitness databases and resources
- Inspired by modern workout tracking apps but built from scratch
- Thanks to the open-source Android and Kotlin communities

---

**Made with ❤️ for fitness enthusiasts who value open source**
