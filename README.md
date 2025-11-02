# Cashi - Kotlin Multiplatform Payment Application

A semi production-ready Kotlin Multiplatform payment application demonstrating clean architecture, real-time data synchronization, and comprehensive testing strategies.

## Overview

Cashi is a fintech payment application built with Kotlin Multiplatform, featuring:

- **Android UI** with Jetpack Compose Material 3
- **Real-time transaction updates** via Firebase Firestore
- **Ktor backend server** with Firebase Admin SDK integration
- **Cross-platform business logic** shared between Android, iOS, and Server
- **Comprehensive testing** including unit tests, BDD scenarios, and load testing

## 🎥 Demo Videos

Watch comprehensive demos of the project:

1. **[Cashi Challenge Demo Part 1 | Payments & Project Structure Demo](https://www.loom.com/share/3f87027f9988489ea3a137d8d8220168)**
   - Application walkthrough and payment flow
   - Project architecture and structure
   - Firebase integration demonstration

2. **[Cashi Challenge Demo Part 2 | Unit & BDD Testing Demo](https://www.loom.com/share/e4473fd512a24e59b76f28f65dd4016b)**
   - Unit testing with MockK
   - BDD scenarios with Cucumber
   - Test coverage and reporting

3. **[Cashi Challenge Demo Final Part | Appium & JMeter Load Tests](https://www.loom.com/share/5deb794f89694697893174b720629452)**
   - UI automation with Appium
   - Load testing with JMeter
   - Performance analysis

## ⚠️ Security Warning - For Demonstration Only

**IMPORTANT**: This application is configured for local development and demonstration purposes only.

### Network Security Configuration

The Android app is configured to allow **HTTP clear text traffic** to communicate with the local development server. This is **UNSAFE for production** and should **NEVER** be used in a real application.

**Configuration details**:
- Clear text traffic is enabled in `network_security_config.xml`
- Server runs on `http://10.0.2.2:8080` (Android emulator's localhost)
- This is configured for this coding challenge demonstration only

**For a production application, you MUST**:
- Use HTTPS for ALL network communication
- Implement certificate pinning
- Never allow clear text traffic
- Use proper API authentication (OAuth2, JWT, etc.)
- Store sensitive data encrypted
- Follow Android security best practices

This configuration exists solely to simplify the demonstration of the app's architecture and functionality for evaluation purposes.

## Technology Stack

### Frontend (Android)
- Kotlin Multiplatform
- Jetpack Compose Multiplatform
- Material 3 Design System
- Koin Dependency Injection
- Coroutines & Flow

### Backend (Server)
- Ktor 3.x
- Firebase Admin SDK
- Content Negotiation (JSON)
- Kotlinx Serialization

### Shared Layer
- Kotlin Multiplatform Common
- Kotlinx DateTime
- Domain Models & Business Logic
- Payment Validation

### Testing
- JUnit 5 for unit tests
- Cucumber (Kotlin) for BDD tests
- MockK for mocking
- JMeter for load testing
- Appium for UI automation testing

## Project Structure

```
cashi/
├── composeApp/          # Android app with Compose UI
│   ├── commonMain/      # Cross-platform UI and ViewModels
│   ├── androidMain/     # Android-specific implementations
│   └── iosMain/         # iOS-specific implementations (future)
├── shared/              # Shared business logic
│   ├── commonMain/      # Domain models, validators, use cases
│   └── commonTest/      # Shared unit tests
├── server/              # Ktor backend server
│   ├── main/kotlin/     # Server implementation
│   └── test/kotlin/     # BDD tests and server tests
├── jmeter-tests/        # JMeter load testing scripts
└── appium-tests/        # Appium UI automation tests
```

## Architecture

### Clean Architecture Layers

1. **Presentation Layer** (`composeApp/`)
   - `BaseViewModel<UIAction, UIState>` pattern
   - Compose UI screens and components
   - Material 3 theming

2. **Domain Layer** (`shared/commonMain/domain/`)
   - Use Cases (SubmitPaymentUseCase, ObserveTransactionsUseCase)
   - Validators (PaymentValidator)
   - Domain Models (Payment, TransactionStatus)

3. **Data Layer** (`shared/commonMain/data/`)
   - Repository pattern with expect/actual
   - API Client (Ktor)
   - Firebase Firestore integration

4. **Server Layer** (`server/`)
   - REST API with Ktor
   - Firebase Admin SDK for Firestore operations
   - Payment processing endpoints

### Key Patterns

- **BaseViewModel Pattern**: Type-safe state management with UIState/UIAction
- **Repository Pattern**: Abstract data sources with platform-specific implementations
- **Use Case Pattern**: Encapsulate business logic operations
- **Dependency Injection**: Koin modules split by platform (commonModule + androidModule)

## Prerequisites

Before running the project, ensure you have:

1. **JDK 11 or higher**
2. **Android Studio Ladybug or later** with Kotlin Multiplatform plugin
3. **Firebase Project** with Firestore enabled
4. **Gradle 8.x** (included via wrapper)

### Firebase Setup

1. **Create a Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use an existing one
   - Enable Firestore Database in test mode (or production with rules)

2. **Download Firebase Configuration Files**:

   **For Android:**
   - In Firebase Console, add an Android app
   - Package name: `com.test.cashi`
   - Download `google-services.json`
   - Place it in: `composeApp/google-services.json`

   **For Server:**
   - In Firebase Console, go to Project Settings > Service Accounts
   - Click "Generate new private key"
   - Download the JSON file
   - Rename it to `serviceAccountKey.json`
   - Place it in: `server/src/main/resources/serviceAccountKey.json`

3. **Firestore Database Structure**:
   ```
   payments/
   ├── {paymentId}/
   │   ├── id: String
   │   ├── recipientEmail: String
   │   ├── amount: Double
   │   ├── currency: String
   │   ├── timestamp: Long
   │   └── status: String
   ```

### Environment Configuration

The app uses `local.properties` for environment-specific configuration. This file is git-ignored for security.

**1. Copy the example file**:
```bash
cp local.properties.example local.properties
```

**2. Configure the API base URL** based on your platform and device:

```properties
# For Android Emulator (default)
api.base.url=http://10.0.2.2:8080

# For physical Android device (use your machine's local IP)
api.base.url=http://192.168.1.100:8080

# For iOS Simulator
api.base.url=http://localhost:8080

# For physical iOS device (use your machine's local IP)
api.base.url=http://192.168.1.100:8080
```

**Configuration Priority**:
1. `local.properties` file (highest priority)
2. `API_BASE_URL` environment variable
3. Default value: `http://10.0.2.2:8080`

**How it works across platforms**:
- Uses **BuildKonfig** plugin to generate compile-time constants
- At **build time**, Gradle reads `local.properties` and generates a `BuildKonfig.kt` file
- The generated constant is compiled into the platform binary (Android APK, iOS Framework, JVM JAR)
- Works across **all platforms** (Android, iOS, JVM) via Kotlin Multiplatform

**Important Notes**:
- **iOS and Android need different URLs**: iOS Simulator uses `localhost:8080`, Android Emulator uses `10.0.2.2:8080`
- You need to **rebuild** the project after changing `local.properties` (it's compile-time, not runtime)
- For **cross-platform development**, use environment variables to set different URLs per platform:
  ```bash
  # Build for Android Emulator
  API_BASE_URL=http://10.0.2.2:8080 ./gradlew :composeApp:assembleDebug

  # Build iOS framework for Simulator
  API_BASE_URL=http://localhost:8080 ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
  ```

## Building and Running

### 1. Run the Server

The server must be running before using the Android app, as it handles payment submissions.

```bash
# macOS/Linux
./gradlew :server:run

# Windows
.\gradlew.bat :server:run
```

The server will start on `http://localhost:8080`

**Available Endpoints:**
- `GET /` - Health check
- `POST /payments` - Submit a new payment
  ```json
  {
    "recipientEmail": "user@example.com",
    "amount": 100.50,
    "currency": "USD"
  }
  ```

### 2. Run the Android App

**Option A: Android Studio**
1. Open the project in Android Studio
2. Select "composeApp" run configuration
3. Choose an emulator or connected device
4. Click Run

**Option B: Command Line**
```bash
# Build the debug APK
./gradlew :composeApp:assembleDebug

# Install and run on connected device/emulator
./gradlew :composeApp:installDebug
```

**Note**: The app is configured to connect to `http://10.0.2.2:8080` for Android emulators (localhost proxy). For physical devices, update the `baseUrl` in `PaymentApiClient.kt`.

### 3. iOS Setup (Future)

The shared business logic is ready for iOS, but the UI integration is not yet implemented. To add iOS support:

1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Initialize Koin with `commonModule` in Swift
3. Create SwiftUI views that call the shared ViewModels

## Testing

### Unit Tests

Run all unit tests in the shared module:

```bash
# Run Android unit tests and view HTML report (recommended)
./gradlew :shared:testDebugUnitTest && open shared/build/reports/tests/testDebugUnitTest/index.html

# Run all platform tests (Android, JVM, iOS)
./gradlew :shared:allTests

# If tests are cached and you need to force re-run, clean first
./gradlew :shared:clean :shared:testDebugUnitTest
```

**Note**: Gradle caches test results. Test output only appears in console when tests fail. Always check the HTML report for detailed results including passed tests, timing, and coverage.

**Test Coverage:**
- `PaymentValidatorTest` - Email and amount validation logic
- `PaymentTest` - Domain model validation
- `SubmitPaymentUseCaseTest` - Payment submission flow with mocks
- `ObserveTransactionsUseCaseTest` - Real-time transaction observation

**Generated Reports**: `shared/build/reports/tests/testDebugUnitTest/index.html`

### Server Tests

Run server integration tests:

```bash
# Run server tests and view HTML report (recommended)
./gradlew :server:test && open server/build/reports/tests/test/index.html

# Or run tests only
./gradlew :server:test

# View the HTML report separately (macOS/Linux)
open server/build/reports/tests/test/index.html

# View the HTML report separately (Windows)
start server/build/reports/tests/test/index.html
```

**Test Coverage:**
- `ApplicationTest` - Server endpoint tests
  - Health check endpoint
  - Payment submission endpoint
  - Request/response validation

**Generated Reports**: Available in `server/build/reports/tests/test/index.html`

### BDD Tests (Cucumber)

Run Cucumber BDD tests for payment submission scenarios:

```bash
# Run Cucumber tests in shared module
./gradlew :shared:testDebugUnitTest --tests "CucumberTestRunner"

# View the HTML report
open shared/build/reports/cucumber/cucumber-report.html
```

**Feature Files** (`shared/src/androidUnitTest/resources/features/`):
- `payment_submission.feature` - Payment submission scenarios (8 scenarios)
  - Successful payments (USD and EUR)
  - Invalid email validation
  - Empty email validation
  - Zero/negative/excessive amount validation
  - Network error handling

**Step Definitions**: Located in `shared/src/androidUnitTest/kotlin/com/test/cashi/bdd/steps/`

**Generated Reports**:
- Cucumber HTML: `shared/build/reports/cucumber/cucumber-report.html`
- Cucumber JSON: `shared/build/reports/cucumber/cucumber-report.json`

**Note**: Cucumber test results don't appear in console output - check the HTML report to view test results.

### Load Testing (JMeter)

Test the server's performance under load:

**Prerequisites:**
1. Download [Apache JMeter](https://jmeter.apache.org/download_jmeter.cgi)
2. Extract and add `bin/` to your PATH

**Running Load Tests:**

```bash
# Start the server first (in a separate terminal)
./gradlew :server:run

# In a new terminal, run JMeter test from project root
jmeter -n -t jmeter-tests/PaymentAPI_LoadTest.jmx -l jmeter-tests/results.jtl -e -o jmeter-tests/reports/

# View reports (macOS/Linux)
open jmeter-tests/reports/index.html

# View reports (Windows)
start jmeter-tests/reports/index.html
```

**Test Plan Details:**
- **Thread Group**: 100 concurrent users
- **Ramp-Up Period**: 10 seconds
- **Loop Count**: 10 iterations per user
- **Total Requests**: 1,000 payment submissions
- **Assertions**: Response time < 2s, HTTP 200/201 status codes

**Expected Results:**
- Throughput: ~50-100 requests/second
- Average Response Time: < 500ms
- Error Rate: < 1%

### UI Automation Testing (Appium)

Test the complete payment flow with automated UI tests:

**Prerequisites:**
1. Install Appium Server:
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```
2. Start an Android emulator or connect a physical device
3. Ensure the app is installed on the device

**Running Appium Tests:**

**Important**: Ensure the backend server is running before starting Appium tests, as the app needs to connect to the API.

**Option A: Using the Helper Script (Recommended)**
```bash
# 1. Start the backend server in a separate terminal
./gradlew :server:run

# 2. Set ANDROID_HOME environment variable
export ANDROID_HOME=$HOME/Library/Android/sdk

# 3. Run all Appium tests (automatically handles Appium server, app installation, etc.)
./run-appium-tests.sh

# Run a specific test class
./run-appium-tests.sh "com.test.cashi.PaymentFlowTest"

# Run a specific test method
./run-appium-tests.sh "com.test.cashi.PaymentFlowTest.testSendPaymentAndVerifyInHistory"
```

**Option B: Manual Setup**
```bash
# 1. Start the backend server in a separate terminal
./gradlew :server:run

# 2. Set ANDROID_HOME environment variable
export ANDROID_HOME=$HOME/Library/Android/sdk

# 3. Start Appium server in another terminal
appium

# 4. Install the app
./gradlew :composeApp:installDebug

# 5. Run the tests
./gradlew :appium-tests:test
```

**Test Coverage:**
- `testSendPaymentAndVerifyInHistory()` - Complete payment flow:
  - Opens payment form
  - Fills in recipient email and amount
  - Submits payment
  - Verifies transaction appears in history
- `testPaymentFormValidation()` - Form validation:
  - Tests required field validation
  - Tests cancel button functionality
- `testCurrencySelection()` - Currency dropdown:
  - Tests currency selection (USD/EUR)
  - Validates currency symbol updates

**Detailed Documentation:**
See [APPIUM_TESTING.md](composeApp/APPIUM_TESTING.md) for comprehensive setup and troubleshooting guide.

## Running All Tests

Run the complete test suite:

```bash
# 1. Unit tests
./gradlew :shared:allTests

# 2. BDD tests (requires server to be stopped)
./gradlew :server:test

# 3. Start server for load tests
./gradlew :server:run &

# 4. Wait for server startup, then run load tests
sleep 5
jmeter -n -t jmeter-tests/PaymentAPI_LoadTest.jmx -l jmeter-tests/results.jtl -e -o jmeter-tests/reports/

# 5. Stop the server
pkill -f "server:run"
```

## Localization

Cashi includes built-in internationalization with support for Sudanese users:

### Supported Languages

- **English** (default) - Full UI coverage
- **Arabic** (العربية) - Basic translation for Sudanese/Arabic-speaking users

### Features

- **Seamless Language Switching**: Tap the language icon (🌐) in the top app bar to switch between English and Arabic
- **Real-time Updates**: All UI elements update instantly when switching languages
- **KMP-Compatible**: Localization is implemented in the shared Compose layer, making it fully cross-platform
- **RTL Support Ready**: Arabic strings are provided, with RTL layout support ready to be enabled

### Implementation

The localization system is built on Kotlin Multiplatform and Compose Multiplatform:

1. **Strings Interface** (`composeApp/commonMain/ui/localization/Strings.kt`)
    - Defines all localizable strings
    - Implemented for both English and Arabic

2. **Locale Manager** (`composeApp/commonMain/ui/localization/LocaleManager.kt`)
    - Manages current locale state
    - Provides language switching functionality
    - Integrates with Compose via CompositionLocal

3. **Usage in UI**
   ```kotlin
   val strings = LocalStrings.current
   Text(text = strings.sendPayment) // Automatically displays in current language
   ```

### Adding More Languages

To add a new language:

1. Create a new object implementing the `Strings` interface
2. Add the language to the `SupportedLocale` enum
3. Provide translations for all string properties

This architecture makes it easy to expand support to additional languages in the future.

## Development Workflow

### Adding a New Feature

1. **Domain Layer**: Define models and validators in `shared/commonMain/domain/`
2. **Use Case**: Create use case in `shared/commonMain/domain/usecase/`
3. **Repository**: Add repository method (expect/actual if platform-specific)
4. **ViewModel**: Update or create ViewModel in `composeApp/commonMain/ui/`
5. **UI**: Build Compose screens in `composeApp/commonMain/ui/screens/`
6. **Tests**: Write unit tests in `shared/commonTest/` and BDD tests in `server/src/test/`

### Dependency Injection

**Common Module** (`composeApp/commonMain/di/AppModule.kt`):
```kotlin
val commonModule = module {
    singleOf(::PaymentValidator)
    factoryOf(::SubmitPaymentUseCase)
    single { PaymentApiClient() }
    viewModelOf(::TransactionViewModel)
}
```

**Platform Module** (`composeApp/androidMain/di/PlatformModule.kt`):
```kotlin
val androidModule = module {
    singleOf(::PaymentRepository) // expect/actual
}
```

## Troubleshooting

### Server Won't Start
- Ensure Firebase `serviceAccountKey.json` is in `server/src/main/resources/`
- Check that port 8080 is not already in use: `lsof -i :8080`

### Android App Can't Connect to Server
- Emulator: Use `http://10.0.2.2:8080` (already configured)
- Physical device: Update `baseUrl` in `PaymentApiClient.kt` to your machine's IP
- Check server logs for incoming requests

### Firebase Errors
- Verify `google-services.json` is in `composeApp/` directory
- Ensure Firestore is enabled in Firebase Console
- Check Firebase rules allow read/write access

### Koin Dependency Errors
- Ensure both `commonModule` and `androidModule` are loaded in `CashiApplication.kt`
- Check that expect/actual implementations match

## License

This project is for demonstration purposes as part of a fintech coding challenge.

## Contact

For questions or issues, please open an issue in the repository.