# Cashi - Kotlin Multiplatform Payment Application

A production-ready Kotlin Multiplatform payment application demonstrating clean architecture, real-time data synchronization, and comprehensive testing strategies.

## Overview

Cashi is a fintech payment application built with Kotlin Multiplatform, featuring:

- **Android UI** with Jetpack Compose Material 3
- **Real-time transaction updates** via Firebase Firestore
- **Ktor backend server** with Firebase Admin SDK integration
- **Cross-platform business logic** shared between Android, iOS, and Server
- **Comprehensive testing** including unit tests, BDD scenarios, and load testing

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
└── loadTests/           # JMeter load testing scripts
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
# Run all tests
./gradlew :shared:allTests

# Run only common tests
./gradlew :shared:cleanAllTests
```

**Test Coverage:**
- `PaymentValidatorTest` - Email and amount validation logic
- `PaymentTest` - Domain model validation
- `SubmitPaymentUseCaseTest` - Payment submission flow with mocks
- `ObserveTransactionsUseCaseTest` - Real-time transaction observation

### BDD Tests (Cucumber)

Run Cucumber BDD tests for payment scenarios:

```bash
# Run all BDD tests
./gradlew :server:test --tests "com.test.cashi.RunCucumberTest"
```

**Feature Files** (`server/src/test/resources/features/`):
- `payment.feature` - Complete payment submission scenarios
  - Successful payments
  - Invalid email validation
  - Invalid amount validation
  - Network error handling

**Generated Reports**: Available in `server/build/reports/tests/test/index.html`

### Load Testing (JMeter)

Test the server's performance under load:

**Prerequisites:**
1. Download [Apache JMeter](https://jmeter.apache.org/download_jmeter.cgi)
2. Extract and add `bin/` to your PATH

**Running Load Tests:**

```bash
# Start the server first
./gradlew :server:run

# In a new terminal, run JMeter test
cd loadTests
jmeter -n -t PaymentAPI_LoadTest.jmx -l results.jtl -e -o reports/

# View reports
open reports/index.html
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
cd loadTests && jmeter -n -t PaymentAPI_LoadTest.jmx -l results.jtl

# 5. Stop the server
pkill -f "server:run"
```

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