package com.test.cashi.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the current status of a payment transaction
 */
@Serializable
enum class TransactionStatus {
    /**
     * Payment has been initiated but not yet processed
     */
    PENDING,

    /**
     * Payment has been successfully processed
     */
    COMPLETED,

    /**
     * Payment processing failed
     */
    FAILED
}