package com.test.cashi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.cashi.domain.model.Currency
import com.test.cashi.domain.model.Payment
import com.test.cashi.domain.usecase.ObserveTransactionsUseCase
import com.test.cashi.domain.usecase.SubmitPaymentUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for transaction list and payment submission
 *
 * Clean Architecture:
 * - ViewModel depends only on use cases (not repositories)
 * - Business logic lives in use cases
 * - ViewModel handles UI state and coordination
 *
 * This makes testing easier:
 * - Use cases can be mocked for UI tests
 * - Clear separation between business logic and UI logic
 * - Easy to test state transitions
 */
class TransactionViewModel(
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val submitPaymentUseCase: SubmitPaymentUseCase
) : ViewModel() {

    // Transaction list state
    private val _transactions = MutableStateFlow<List<Payment>>(emptyList())
    val transactions: StateFlow<List<Payment>> = _transactions.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Payment submission state
    private val _paymentState = MutableStateFlow<PaymentSubmissionState>(PaymentSubmissionState.Idle)
    val paymentState: StateFlow<PaymentSubmissionState> = _paymentState.asStateFlow()

    // Validation errors
    private val _validationErrors = MutableStateFlow<List<String>>(emptyList())
    val validationErrors: StateFlow<List<String>> = _validationErrors.asStateFlow()

    init {
        observeTransactions()
    }

    /**
     * Start observing transactions from Firebase in real-time
     */
    private fun observeTransactions() {
        viewModelScope.launch {
            observeTransactionsUseCase()
                .catch { e ->
                    // Handle errors from Firebase listener
                    _paymentState.value = PaymentSubmissionState.Error(
                        "Failed to load transactions: ${e.message}"
                    )
                }
                .collect { payments ->
                    _transactions.value = payments
                }
        }
    }

    /**
     * Submit a new payment
     */
    fun submitPayment(
        recipientEmail: String,
        amount: String,
        currency: Currency
    ) {
        // Clear previous state
        _validationErrors.value = emptyList()
        _paymentState.value = PaymentSubmissionState.Idle

        // Parse amount
        val amountDouble = amount.toDoubleOrNull()
        if (amountDouble == null) {
            _validationErrors.value = listOf("Invalid amount format")
            return
        }

        // Get validation errors
        val errors = submitPaymentUseCase.getValidationErrors(
            recipientEmail = recipientEmail.trim(),
            amount = amountDouble,
            currency = currency
        )

        if (errors.isNotEmpty()) {
            _validationErrors.value = errors
            return
        }

        // Submit via use case
        viewModelScope.launch {
            _isLoading.value = true
            _paymentState.value = PaymentSubmissionState.Loading

            submitPaymentUseCase(
                recipientEmail = recipientEmail.trim(),
                amount = amountDouble,
                currency = currency
            )
                .onSuccess { response ->
                    if (response.success) {
                        _paymentState.value = PaymentSubmissionState.Success
                    } else {
                        _paymentState.value = PaymentSubmissionState.Error(
                            response.error ?: "Unknown error"
                        )
                    }
                }
                .onFailure { e ->
                    _paymentState.value = PaymentSubmissionState.Error(
                        e.message ?: "Network error"
                    )
                }

            _isLoading.value = false
        }
    }

    /**
     * Reset payment submission state
     */
    fun resetPaymentState() {
        _paymentState.value = PaymentSubmissionState.Idle
        _validationErrors.value = emptyList()
    }
}

/**
 * Represents the state of payment submission
 * Sealed class makes it easy to handle all states in UI
 */
sealed class PaymentSubmissionState {
    data object Idle : PaymentSubmissionState()
    data object Loading : PaymentSubmissionState()
    data object Success : PaymentSubmissionState()
    data class Error(val message: String) : PaymentSubmissionState()
}