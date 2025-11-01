package com.test.cashi.data.repository

import com.test.cashi.domain.model.Payment
import com.test.cashi.domain.model.PaymentRequest
import com.test.cashi.domain.model.PaymentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * JVM/Server stub implementation of PaymentRepository
 *
 * The server doesn't use this - it writes directly to Firebase via Admin SDK
 * This exists only to satisfy the expect/actual contract for KMP compilation
 */
actual class PaymentRepository {
    actual suspend fun submitPayment(request: PaymentRequest): Result<PaymentResponse> {
        return Result.failure(UnsupportedOperationException("Server uses Firebase Admin SDK directly"))
    }

    actual fun observeTransactions(): Flow<List<Payment>> {
        return flowOf(emptyList())
    }

    actual suspend fun getTransactions(): Result<List<Payment>> {
        return Result.success(emptyList())
    }
}