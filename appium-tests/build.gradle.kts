plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    // Appium and Selenium dependencies
    testImplementation("io.appium:java-client:9.3.0")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.25.0")

    // JUnit for test execution
    testImplementation("junit:junit:4.13.2")

    // Kotlin test
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnit()

    // Pass ANDROID_HOME environment variable to test process
    // This must be set in your shell before running tests
    val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    if (androidHome != null) {
        environment("ANDROID_HOME", androidHome)
        environment("ANDROID_SDK_ROOT", androidHome)
    }

    // Add test output logging
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}