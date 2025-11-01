package com.test.cashi.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.FileInputStream

/**
 * Firebase Admin SDK initialization
 *
 * This singleton manages Firebase Admin SDK for server-side operations
 * Designed for testability:
 * - Can be mocked for unit tests
 * - Environment-based configuration
 * - Lazy initialization
 */
object FirebaseAdmin {
    private var firebaseApp: FirebaseApp? = null
    private var firestore: Firestore? = null

    /**
     * Initialize Firebase Admin SDK
     *
     * @param serviceAccountPath Path to Firebase service account JSON file
     *                           Default: "serviceAccountKey.json" in project root
     *
     * For testing: Set FIREBASE_CONFIG_PATH environment variable
     */
    fun initialize(serviceAccountPath: String = "serviceAccountKey.json") {
        if (firebaseApp != null) {
            println("Firebase Admin SDK already initialized")
            return
        }

        try {
            val configPath = System.getenv("FIREBASE_CONFIG_PATH") ?: serviceAccountPath

            // Try multiple paths to find the service account key
            val possiblePaths = listOf(
                configPath,                                    // Direct path (environment variable or parameter)
                "serviceAccountKey.json",                      // Working directory
                "../serviceAccountKey.json",                   // Parent directory (for server module)
                "../../serviceAccountKey.json",                // Project root from build directory
                System.getProperty("user.dir") + "/serviceAccountKey.json"  // Absolute from current dir
            )

            var foundPath: String? = null
            for (path in possiblePaths) {
                val file = java.io.File(path)
                if (file.exists() && file.canRead()) {
                    foundPath = path
                    println("Found Firebase config at: ${file.absolutePath}")
                    break
                }
            }

            if (foundPath == null) {
                throw IllegalStateException(
                    "Could not find serviceAccountKey.json. Searched in:\n" +
                    possiblePaths.joinToString("\n") { "  - $it" } +
                    "\n\nCurrent working directory: ${System.getProperty("user.dir")}"
                )
            }

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(FileInputStream(foundPath)))
                .build()

            firebaseApp = FirebaseApp.initializeApp(options)
            firestore = FirestoreClient.getFirestore()

            println("✅ Firebase Admin SDK initialized successfully")
        } catch (e: Exception) {
            System.err.println("❌ Failed to initialize Firebase Admin SDK: ${e.message}")
            System.err.println("Make sure serviceAccountKey.json exists in project root")
            System.err.println("Download from: Firebase Console > Project Settings > Service Accounts")
            throw e
        }
    }

    /**
     * Get Firestore instance
     * @throws IllegalStateException if Firebase not initialized
     */
    fun getFirestore(): Firestore {
        return firestore ?: throw IllegalStateException(
            "Firebase Admin SDK not initialized. Call initialize() first"
        )
    }

    /**
     * Check if Firebase is initialized (useful for tests)
     */
    fun isInitialized(): Boolean = firebaseApp != null
}