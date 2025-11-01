package com.test.cashi.domain.usecase

import com.test.cashi.data.repository.PaymentRepository
import com.test.cashi.domain.model.Payment
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing real-time transactions
 *
 * Benefits for testing:
 * - Separates Firebase real-time logic from UI
 * - Easy to mock for UI tests
 * - Can add business logic (filtering, sorting) without touching repository
 * - Clear contract for what the UI layer needs
 */
class ObserveTransactionsUseCase(
    private val repository: PaymentRepository
) {
    /**
     * Observe transactions in real-time
     *
     * @return Flow of transaction lists, updated automatically by Firebase
     */
    operator fun invoke(): Flow<List<Payment>> {
        return repository.observeTransactions()
    }

    /**
     * Get transactions once (no real-time updates)
     * Useful for initial load or testing
     */
    suspend fun getOnce(): Result<List<Payment>> {
        return repository.getTransactions()
    }
}