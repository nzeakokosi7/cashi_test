import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildkonfig)
}

// Load local.properties if it exists
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    jvm() // Server needs this for domain models (Payment, Currency, etc.)
           // Note: Server won't use PaymentRepository, only domain models

    sourceSets {
        commonMain.dependencies {
            // Kotlinx
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            // Ktor Client
            implementation(libs.ktor.clientCore)
            implementation(libs.ktor.clientContentNegotiation)
            implementation(libs.ktor.clientSerialization)
            implementation(libs.ktor.clientLogging)

            // Koin
            implementation(libs.koin.core)
        }

        androidMain.dependencies {
            implementation(libs.ktor.clientAndroid)
            implementation(libs.kotlinx.coroutines.android)

            // Firebase for Android actual implementation
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.firestore)
        }

        iosMain.dependencies {
            // iOS-specific Ktor client engine will be added here when needed
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotest.assertions)
            implementation(libs.koin.test)
        }

        androidUnitTest.dependencies {
            // MockK is Android/JVM only, not available for iOS
            implementation(libs.mockk)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.junit)

            // Cucumber for BDD tests
            implementation(libs.cucumber.java)
            implementation(libs.cucumber.junit)
        }
    }
}

android {
    namespace = "com.test.cashi.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

buildkonfig {
    packageName = "com.test.cashi"

    // Default configuration (Development)
    // Priority: local.properties > environment variable > default value
    defaultConfigs {
        val apiBaseUrl = localProperties.getProperty("api.base.url")
            ?: System.getenv("API_BASE_URL")
            ?: "http://10.0.2.2:8080"

        buildConfigField(STRING, "API_BASE_URL", apiBaseUrl)
    }
}
