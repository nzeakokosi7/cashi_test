package com.test.cashi

import android.app.Application
import com.google.firebase.FirebaseApp
import com.test.cashi.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Custom Application class for Cashi
 *
 * Initializes:
 * - Firebase Android SDK
 * - Koin dependency injection
 *
 * Register in AndroidManifest.xml
 */
class CashiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Koin
        startKoin {
            androidLogger()
            androidContext(this@CashiApplication)
            modules(appModule)
        }
    }
}