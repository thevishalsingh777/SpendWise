SpendWise
=========

SpendWise is an Android application for tracking personal finances. It is built with Kotlin and Jetpack Compose, with a layered data, domain, and presentation structure.

Features
--------
- Record and review financial transactions.
- Organize transactions by category and filter transaction history.
- Create and manage budgets.
- View financial summaries, analytics, and spending insights.
- Export transaction data to CSV.
- Store app data locally using Room.
- Use biometric authentication where supported.
- Configure app settings and preferences.

Technology
----------
- Kotlin
- Android SDK 35; minimum Android version: Android 7.0 (API 24)
- Jetpack Compose and Material 3
- Navigation Compose and AndroidX Lifecycle/ViewModel
- Room database with KSP
- AndroidX Biometric
- Gradle Kotlin DSL

Requirements
------------
- Android Studio with support for this project's Android Gradle Plugin and Kotlin versions.
- JDK 11 or newer.
- Android SDK 35.
- An emulator or device running Android 7.0 (API 24) or newer.

Getting Started
---------------
1. Clone the repository:

   git clone https://github.com/thevishalsingh777/SpendWise.git
   cd SpendWise

2. Open the project in Android Studio and allow Gradle to sync.
3. Run the app from Android Studio on an emulator or connected device.

To build a debug APK from the command line:

   ./gradlew assembleDebug

On Windows, use:

   gradlew.bat assembleDebug

Testing
-------
Run local unit tests:

   ./gradlew test

Run instrumented tests on a connected device or emulator:

   ./gradlew connectedAndroidTest

Project Layout
--------------
app/src/main/java/com/example/test/
  data/          Local storage, preferences, and repository implementations
  domain/        Business models, repository interfaces, and use cases
  presentation/  Navigation, screens, and shared UI components
  ui/theme/      Compose theme resources

License
-------
No license is currently specified in this repository. Unless a license is added, standard copyright restrictions apply.
