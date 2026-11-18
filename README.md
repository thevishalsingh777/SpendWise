SpendWise
SpendWise is a modern Android expense-tracking application built with Kotlin and Jetpack Compose. It helps users record transactions, organize spending by category, manage budgets, and analyze financial activity.

Features
Track income and expenses
Categorize transactions
Create and manage budgets
View financial summaries and spending insights
Filter and review transaction history
Analyze spending through charts and analytics
Export expense data to CSV
Secure app access with biometric authentication
Store data locally using Room
Customize preferences and app settings
Support for light and dark themes
Tech Stack
Kotlin
Jetpack Compose
Material 3
MVVM architecture
Room database
Kotlin Coroutines and Flow
Navigation Compose
AndroidX Lifecycle and ViewModel
KSP
Biometric authentication
JUnit, Espresso, and Compose UI testing
Requirements
Android Studio with an Android Gradle Plugin-compatible SDK
JDK 11 or newer
Android SDK 35
Android device or emulator running Android 7.0 (API 24) or newer
Getting Started
Clone the repository:
bash
git clone https://github.com/thevishalsingh777/SpendWise.git
cd SpendWise
Open the project in Android Studio.

Allow Gradle to synchronize the project and download dependencies.

Build and run the app:

bash
./gradlew assembleDebug
Alternatively, select the app configuration and run it on an emulator or connected Android device.

Project Structure
Text
app/src/main/java/com/example/test/
├── data/
│   ├── local/          # Room entities, DAOs, database, and mappers
│   ├── preferences/    # Application preferences
│   └── repository/     # Repository implementations
├── domain/
│   ├── model/          # Business models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Financial calculations and CSV export
├── presentation/
│   ├── components/     # Shared Compose components
│   ├── navigation/     # App navigation
│   ├── screens/        # Application screens
│   └── theme/          # Presentation-level theming
└── ui/theme/           # Jetpack Compose theme definitions
Main Screens
Home: Overview of balances and recent activity
Transactions: Transaction history and filtering
Add/Edit: Create or update transactions
Budgets: Set and monitor category budgets
Analytics: Visualize spending patterns
Settings: Configure app preferences and security
Testing
Run unit tests:

bash
./gradlew test
Run instrumented tests:

bash
./gradlew connectedAndroidTest
License
This project currently does not specify a license. Add a license file if you intend to make the repository available for public reuse.
