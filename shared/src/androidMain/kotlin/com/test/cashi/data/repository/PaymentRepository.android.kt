package com.test.cashi.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.test.cashi.data.api.PaymentApiClient
import com.test.cashi.domain.model.Currency
import com.test.cashi.domain.model.Payment
import com.test.cashi.domain.model.PaymentRequest
import com.test.cashi.domain.model.PaymentResponse
import com.test.cashi.domain.model.TransactionStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Android implementation of PaymentRepository
 * Uses Firebase Firestore for real-time transaction sync
 *
 * Architecture:
 * 1. Payments submitted via API → Server saves to Firebase
 * 2. Android listens to Firebase for real-time updates
 * 3. This creates a reactive, testable data layer
 */
actual class PaymentRepository(
    private val apiClient: PaymentApiClient = PaymentApiClient(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val paymentsCollection = firestore.collection(COLLECTION_PAYMENTS)

    companion object {
        const val COLLECTION_PAYMENTS = "payments"
    }

    /**
     * Submit payment via API
     * The server will save it to Firebase, which triggers our real-time listener
     */
    actual suspend fun submitPayment(request: PaymentRequest): Result<PaymentResponse> {
        return apiClient.submitPayment(request)
    }

    /**
     * Real-time Firebase listener for transactions
     * This Flow automatically updates whenever Firebase data changes
     * Perfect for testing real-time sync behavior
     */
    actual fun observeTransactions(): Flow<List<Payment>> = callbackFlow {
        val listener = paymentsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val payments = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Payment(
                            id = doc.id,
                            recipientEmail = doc.getString("recipientEmail") ?: "",
                            amount = doc.getDouble("amount") ?: 0.0,
                            currency = Currency.fromCode(
                                doc.getString("currency") ?: "USD"
                            ) ?: Currency.USD,
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            status = TransactionStatus.valueOf(
                                doc.getString("status") ?: "COMPLETED"
                            )
                        )
                    } catch (e: Exception) {
                        null // Skip malformed documents
                    }
                } ?: emptyList()

                trySend(payments)
            }

        awaitClose { listener.remove() }
    }

    /**
     * One-time fetch of all transactions
     * Useful for initial load or testing without real-time updates
     */
    actual suspend fun getTransactions(): Result<List<Payment>> {
        return try {
            val snapshot = paymentsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val payments = snapshot.documents.mapNotNull { doc ->
                try {
                    Payment(
                        id = doc.id,
                        recipientEmail = doc.getString("recipientEmail") ?: "",
                        amount = doc.getDouble("amount") ?: 0.0,
                        currency = Currency.fromCode(
                            doc.getString("currency") ?: "USD"
                        ) ?: Currency.USD,
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        status = TransactionStatus.valueOf(
                            doc.getString("status") ?: "COMPLETED"
                        )
                    )
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(payments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}