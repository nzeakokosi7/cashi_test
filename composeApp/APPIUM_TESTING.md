# Appium Testing Guide for Cashi Payment App

This guide explains how to set up and run Appium tests for the Cashi payment application.

## Overview

The Appium tests automate the payment flow, including:
- Opening the payment form
- Filling in recipient email and amount
- Selecting currency (USD/EUR)
- Submitting the payment
- Verifying the payment appears in transaction history

## Prerequisites

### 1. Install Node.js and npm
Appium requires Node.js to run.

```bash
# macOS (using Homebrew)
brew install node

# Verify installation
node --version
npm --version
```

### 2. Install Appium Server

```bash
# Install Appium globally
npm install -g appium@next

# Verify installation
appium --version

# Install UiAutomator2 driver for Android
appium driver install uiautomator2
```

### 3. Install Android SDK and Tools

Ensure you have the following installed:
- Android SDK
- Android SDK Platform-Tools
- Android SDK Build-Tools
- Android Emulator (or a physical Android device)

Set up environment variables:
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
```

### 4. Java Development Kit (JDK)

Appium tests require Java 11 or later.

```bash
# Verify Java installation
java -version
```

## Setup

### 1. Prepare Test Device

**Option A: Android Emulator**
1. Open Android Studio
2. Go to Tools > Device Manager
3. Create or start an Android Virtual Device (AVD)
4. Verify the emulator is running:
   ```bash
   adb devices
   ```

**Option B: Physical Device**
1. Enable Developer Options on your Android device
2. Enable USB Debugging
3. Connect device via USB
4. Verify connection:
   ```bash
   adb devices
   ```

### 2. Install the Cashi App on Test Device

Build and install the app:
```bash
# From project root
./gradlew :composeApp:installDebug

# Or install manually
adb install -r composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

### 3. Start Appium Server

Open a terminal and start the Appium server:

```bash
appium

# Or specify host and port
appium --address 127.0.0.1 --port 4723
```

You should see output like:
```
[Appium] Welcome to Appium v2.x.x
[Appium] Appium REST http interface listener started on 127.0.0.1:4723
```

Keep this terminal running during test execution.

## Running Tests

### Run All Tests

```bash
# From project root
./gradlew :appium-tests:test
```

### Run Specific Test Class

```bash
./gradlew :appium-tests:test --tests "com.test.cashi.PaymentFlowTest"
```

### Run Specific Test Method

```bash
./gradlew :appium-tests:test --tests "com.test.cashi.PaymentFlowTest.testSendPaymentAndVerifyInHistory"
```

## Test Structure

Tests are located in `appium-tests/src/test/kotlin/com/test/cashi/`

**Important**: These are desktop JVM tests that run on your development machine and remotely control the Android app via the Appium server. They are NOT Android instrumented tests that run on the device.

### BaseAppiumTest.kt
Base class that sets up and tears down the Appium driver. It configures:
- App package and activity
- Platform settings (Android)
- Automation framework (UiAutomator2)
- Wait timeouts

### PaymentFlowTest.kt
Contains three main test cases:

#### 1. testSendPaymentAndVerifyInHistory()
**Full payment flow test**
- Opens payment bottom sheet
- Fills in recipient email: `test.recipient@example.com`
- Enters amount: `100.50`
- Submits payment
- Verifies payment appears in transaction history
- Validates amount and status are displayed

#### 2. testPaymentFormValidation()
**Form validation test**
- Verifies send button is disabled when fields are empty
- Verifies send button remains disabled with only email filled
- Tests cancel button functionality

#### 3. testCurrencySelection()
**Currency selection test**
- Opens currency dropdown
- Verifies available currencies (USD, EUR)
- Selects EUR
- Validates EUR is selected and € symbol appears

## Troubleshooting

### Common Issues

#### 1. Appium Server Not Running
```
Error: connect ECONNREFUSED 127.0.0.1:4723
```
**Solution:** Start the Appium server: `appium`

#### 2. App Not Installed
```
Error: Activity not found
```
**Solution:** Install the app: `./gradlew :composeApp:installDebug`

#### 3. Device Not Connected
```
Error: Could not find a connected device
```
**Solution:**
- Check device connection: `adb devices`
- Start emulator or connect physical device

#### 4. Element Not Found
```
org.openqa.selenium.NoSuchElementException
```
**Solution:**
- Verify the app is in the correct state
- Increase wait timeout in BaseAppiumTest
- Check if UI element text/IDs have changed

#### 5. Test Timeout
```
org.openqa.selenium.TimeoutException
```
**Solution:**
- Ensure Appium server is responsive
- Check if app is loading slowly (increase timeouts)
- Verify network connectivity for API calls

### Debugging Tips

1. **Enable Appium Logs:**
   ```bash
   appium --log-level debug
   ```

2. **Check App Logs:**
   ```bash
   adb logcat | grep cashi
   ```

3. **Take Screenshots:**
   Add to test code:
   ```kotlin
   val screenshot = driver.getScreenshotAs(OutputType.FILE)
   FileUtils.copyFile(screenshot, File("screenshot.png"))
   ```

4. **Use Appium Inspector:**
   Install Appium Inspector to visually explore UI elements:
   - Download from: https://github.com/appium/appium-inspector/releases
   - Configure with same capabilities as tests
   - Inspect elements to find correct selectors

## Configuration

### Updating Device Settings

To run on a different device, update `BaseAppiumTest.kt`:

```kotlin
val options = UiAutomator2Options().apply {
    // For physical device, set UDID
    setUdid("your-device-udid")  // Get from: adb devices

    // For specific emulator
    setDeviceName("Pixel_6_API_34")

    // For different Android version
    setPlatformVersion("14.0")
}
```

### Changing Appium Server URL

If Appium is running on a different host/port:

```kotlin
driver = AndroidDriver(URL("http://192.168.1.100:4723"), options)
```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Appium Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'

      - name: Install Appium
        run: |
          npm install -g appium
          appium driver install uiautomator2

      - name: Start Appium Server
        run: appium &

      - name: Run Tests
        run: ./gradlew :appium-tests:test

      - name: Upload Test Results
        uses: actions/upload-artifact@v3
        with:
          name: test-results
          path: appium-tests/build/reports/tests/test/
```

## Best Practices

1. **Use Explicit Waits:** Always use WebDriverWait for dynamic elements
2. **Stable Locators:** Prefer accessibility IDs and stable text over XPath
3. **Isolated Tests:** Each test should be independent and not rely on previous test state
4. **Clean State:** Use `setNoReset(false)` if you need app to start fresh each time
5. **Meaningful Assertions:** Use descriptive assertion messages
6. **Page Object Pattern:** For larger test suites, consider implementing page objects

## Next Steps

- Add more test scenarios (invalid inputs, network errors, etc.)
- Implement Page Object Model for better maintainability
- Add parallel test execution for faster CI/CD
- Integrate with test reporting tools (Allure, TestNG)
- Add visual regression testing

## Resources

- [Appium Documentation](https://appium.io/docs/en/latest/)
- [UiAutomator2 Driver](https://github.com/appium/appium-uiautomator2-driver)
- [Selenium WebDriver](https://www.selenium.dev/documentation/webdriver/)