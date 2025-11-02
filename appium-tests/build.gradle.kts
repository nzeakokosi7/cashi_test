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

    // Add test output logging
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}