#!/bin/bash

# Appium Test Runner Script for Cashi App
# This script helps automate the process of running Appium tests

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Cashi Appium Test Runner ===${NC}\n"

# Check if Appium is installed
if ! command -v appium &> /dev/null; then
    echo -e "${RED}Error: Appium is not installed${NC}"
    echo "Install it with: npm install -g appium"
    exit 1
fi

echo -e "${GREEN}✓ Appium is installed${NC}"

# Check if UiAutomator2 driver is installed
if ! appium driver list --installed | grep -q "uiautomator2"; then
    echo -e "${YELLOW}Warning: UiAutomator2 driver not found. Installing...${NC}"
    appium driver install uiautomator2
fi

echo -e "${GREEN}✓ UiAutomator2 driver is installed${NC}"

# Check if ADB is available
if ! command -v adb &> /dev/null; then
    echo -e "${RED}Error: ADB is not found in PATH${NC}"
    echo "Make sure Android SDK is installed and ANDROID_HOME is set"
    exit 1
fi

echo -e "${GREEN}✓ ADB is available${NC}"

# Check for connected devices
DEVICES=$(adb devices | grep -v "List" | grep "device" | wc -l)
if [ "$DEVICES" -eq 0 ]; then
    echo -e "${RED}Error: No Android devices/emulators connected${NC}"
    echo "Start an emulator or connect a device"
    exit 1
fi

echo -e "${GREEN}✓ Device connected${NC}"
adb devices

# Check if app is installed
APP_INSTALLED=$(adb shell pm list packages | grep "com.test.cashi" || true)
if [ -z "$APP_INSTALLED" ]; then
    echo -e "${YELLOW}Warning: Cashi app not installed. Installing...${NC}"
    ./gradlew :composeApp:installDebug
    echo -e "${GREEN}✓ App installed${NC}"
else
    echo -e "${GREEN}✓ Cashi app is installed${NC}"
fi

# Check if Appium server is running
if ! curl -s http://127.0.0.1:4723/status > /dev/null 2>&1; then
    echo -e "${YELLOW}Starting Appium server...${NC}"
    appium > appium.log 2>&1 &
    APPIUM_PID=$!
    echo "Appium PID: $APPIUM_PID"

    # Wait for Appium to start
    echo "Waiting for Appium server to start..."
    for i in {1..30}; do
        if curl -s http://127.0.0.1:4723/status > /dev/null 2>&1; then
            echo -e "${GREEN}✓ Appium server is running${NC}"
            break
        fi
        sleep 1
    done

    if ! curl -s http://127.0.0.1:4723/status > /dev/null 2>&1; then
        echo -e "${RED}Error: Failed to start Appium server${NC}"
        cat appium.log
        exit 1
    fi
else
    echo -e "${GREEN}✓ Appium server is already running${NC}"
    APPIUM_PID=""
fi

# Run tests
echo -e "\n${GREEN}Running Appium tests...${NC}\n"

TEST_COMMAND="./gradlew :appium-tests:test"

# Check if specific test is requested
if [ ! -z "$1" ]; then
    echo "Running specific test: $1"
    TEST_COMMAND="$TEST_COMMAND --tests $1"
fi

# Add additional gradle options
TEST_COMMAND="$TEST_COMMAND --info"

# Run the tests
if $TEST_COMMAND; then
    echo -e "\n${GREEN}✓ Tests completed successfully!${NC}"
    TEST_EXIT_CODE=0
else
    echo -e "\n${RED}✗ Tests failed${NC}"
    TEST_EXIT_CODE=1
fi

# Cleanup
if [ ! -z "$APPIUM_PID" ]; then
    echo -e "\n${YELLOW}Stopping Appium server (PID: $APPIUM_PID)...${NC}"
    kill $APPIUM_PID 2>/dev/null || true
fi

# Show test report location
echo -e "\n${GREEN}Test reports available at:${NC}"
echo "  HTML: appium-tests/build/reports/tests/test/index.html"
echo -e "\n${GREEN}Test results available at:${NC}"
echo "  XML: appium-tests/build/test-results/test/"

exit $TEST_EXIT_CODE