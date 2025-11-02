package com.test.cashi

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import org.junit.After
import org.junit.Before
import java.net.URL
import java.time.Duration

/**
 * Base class for Appium tests
 * Sets up and tears down the Appium driver for Android testing
 */
abstract class BaseAppiumTest {

    protected lateinit var driver: AppiumDriver

    @Before
    fun setUp() {
        // Configure Appium options for Android
        val options = UiAutomator2Options().apply {
            // App package and activity (use full qualified name)
            setAppPackage("com.test.cashi")
            setAppActivity("com.test.cashi.MainActivity")

            // Don't wait for activity - let the app launch naturally
            setAppWaitActivity("*")

            // Platform configuration
            setPlatformName("Android")
            setAutomationName("UiAutomator2")

            // Device configuration (update these based on your test device/emulator)
            setDeviceName("Android Emulator")


            // Reset app state for consistent tests, but don't uninstall
            setNoReset(false)
            setFullReset(false)

            // Wait times
            setNewCommandTimeout(Duration.ofSeconds(300))
            setAppWaitDuration(Duration.ofSeconds(30))
        }

        // Connect to Appium server (default: http://127.0.0.1:4723)
        driver = AndroidDriver(URL("http://127.0.0.1:4723"), options)

        // Set implicit wait time
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))

        // Wait for app to fully load before proceeding with test
        // Compose apps may take longer to render the first screen
        Thread.sleep(5000)
    }

    @After
    fun tearDown() {
        if (::driver.isInitialized) {
            driver.quit()
        }
    }

    /**
     * Helper method to wait for a specified duration
     */
    protected fun waitFor(seconds: Long) {
        Thread.sleep(seconds * 1000)
    }
}