package com.test.cashi.di

import com.google.firebase.firestore.FirebaseFirestore
import com.test.cashi.data.repository.PaymentRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Android-specific Koin module
 *
 * Provides platform-specific dependencies:
 * - FirebaseFirestore instance
 * - PaymentRepository (uses Firebase Firestore on Android)
 */
val androidModule = module {
    // Firebase Firestore instance
    single { FirebaseFirestore.getInstance() }

    // Data layer - Platform-specific repository
    singleOf(::PaymentRepository)
}