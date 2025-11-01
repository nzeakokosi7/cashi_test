package com.test.cashi.domain.usecase

import com.test.cashi.data.repository.PaymentRepository
import com.test.cashi.domain.model.Currency
import com.test.cashi.domain.model.PaymentRequest
import com.test.cashi.domain.model.PaymentResponse
import com.test.cashi.domain.validator.PaymentValidator

/**
 * Use case for submitting a payment
 *
 * Benefits for testing:
 * - Encapsulates validation + submission logic
 * - Easy to mock for BDD tests
 * - Single responsibility
 * - Clear input/output contracts
 */
class SubmitPaymentUseCase(
    private val repository: PaymentRepository,
    private val validator: PaymentValidator
) {
    /**
     * Submit a payment with validation
     *
     * @return Result with PaymentResponse or SubmitPaymentError
     */
    suspend operator fun invoke(
        recipientEmail: String,
        amount: Double,
        currency: Currency
    ): Result<PaymentResponse> {
        // Validate first
        val validationResult = validator.validatePayment(
            recipientEmail = recipientEmail.trim(),
            amount = amount,
            currency = currency
        )

        if (!validationResult.isValid) {
            return Result.failure(
                SubmitPaymentError.ValidationError(
                    validationResult.errorMessage() ?: "Validation failed"
                )
            )
        }

        // Submit to repository
        val request = PaymentRequest(
            recipientEmail = recipientEmail.trim(),
            amount = amount,
            currency = currency
        )

        return repository.submitPayment(request)
            .fold(
                onSuccess = { Result.success(it) },
                onFailure = { Result.failure(SubmitPaymentError.NetworkError(it.message ?: "Network error")) }
            )
    }

    /**
     * Get all validation errors for a payment (useful for showing multiple errors)
     */
    fun getValidationErrors(
        recipientEmail: String,
        amount: Double,
        currency: Currency
    ): List<String> {
        return validator.validatePaymentWithAllErrors(recipientEmail, amount, currency)
    }
}

/**
 * Domain-specific errors for payment submission
 * Makes error handling type-safe and testable
 */
sealed class SubmitPaymentError : Exception() {
    data class ValidationError(override val message: String) : SubmitPaymentError()
    data class NetworkError(override val message: String) : SubmitPaymentError()
}