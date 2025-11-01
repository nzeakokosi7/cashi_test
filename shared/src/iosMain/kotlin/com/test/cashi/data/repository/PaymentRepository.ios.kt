package com.test.cashi.data.repository

import com.test.cashi.data.api.PaymentApiClient
import com.test.cashi.domain.model.Payment
import com.test.cashi.domain.model.PaymentRequest
import com.test.cashi.domain.model.PaymentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * iOS implementation of PaymentRepository
 *
 * TODO: Integrate Firebase iOS SDK for real-time sync
 * This stub allows the shared module to compile for iOS.
 * Actual Firebase integration requires:
 * 1. cocoapods dependency for Firebase
 * 2. Firebase iOS SDK initialization in Swift
 * 3. Kotlin/Native interop with Firebase iOS APIs
 *
 * For now, API calls work but Firebase sync is not implemented.
 */
actual class PaymentRepository(
    private val apiClient: PaymentApiClient = PaymentApiClient(
        baseUrl = "http://localhost:8080" // iOS simulator localhost
    )
) {
    actual suspend fun submitPayment(request: PaymentRequest): Result<PaymentResponse> {
        return apiClient.submitPayment(request)
    }

    actual fun observeTransactions(): Flow<List<Payment>> {
        // TODO: Implement Firebase Firestore listener for iOS
        return flowOf(emptyList())
    }

    actual suspend fun getTransactions(): Result<List<Payment>> {
        // TODO: Implement Firebase Firestore read for iOS
        return Result.success(emptyList())
    }
}