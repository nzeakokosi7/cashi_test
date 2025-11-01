package com.test.cashi.data.repository

import com.test.cashi.domain.model.Payment
import com.test.cashi.domain.model.PaymentRequest
import com.test.cashi.domain.model.PaymentResponse
import kotlinx.coroutines.flow.Flow

/**
 * Platform-specific payment repository
 *
 * Android: Uses Firebase Firestore for real-time transaction sync
 * JVM/Server: Can use different implementation if needed
 *
 * This expect/actual pattern makes testing easier:
 * - Clear platform boundaries
 * - Easy to mock for BDD tests
 * - Type-safe platform implementations
 */
expect class PaymentRepository {
    /**
     * Submit a new payment to the backend API
     * @param request Payment request data
     * @return Result containing PaymentResponse or error
     */
    suspend fun submitPayment(request: PaymentRequest): Result<PaymentResponse>

    /**
     * Observe real-time payment transactions from Firebase
     * @return Flow emitting list of payments whenever data changes
     */
    fun observeTransactions(): Flow<List<Payment>>

    /**
     * Get all payments (one-time fetch, not real-time)
     * @return Result containing list of payments or error
     */
    suspend fun getTransactions(): Result<List<Payment>>
}