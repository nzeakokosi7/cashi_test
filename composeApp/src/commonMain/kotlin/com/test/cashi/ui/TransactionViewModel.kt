package com.test.cashi.ui

import androidx.lifecycle.viewModelScope
import com.test.cashi.domain.model.Currency
import com.test.cashi.domain.model.Payment
import com.test.cashi.domain.usecase.ObserveTransactionsUseCase
import com.test.cashi.domain.usecase.SubmitPaymentUseCase
import com.test.cashi.ui.base.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel for transaction list and payment submission
 *
 * Follows BaseViewModel pattern with:
 * - TransactionUIState: All UI state in one place
 * - TransactionUIAction: One-time events (navigation, success messages)
 * - Clean separation between state and events
 */
class TransactionViewModel(
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val submitPaymentUseCase: SubmitPaymentUseCase
) : BaseViewModel<TransactionUIAction, TransactionUIState>(
    defaultState = TransactionUIState()
) {

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
                    state = state.copy(
                        isLoadingTransactions = false,
                        error = "Failed to load transactions: ${e.message}"
                    )
                }
                .collect { payments ->
                    state = state.copy(
                        transactions = payments,
                        isLoadingTransactions = false,
                        error = null
                    )
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
        state = state.copy(
            isSubmittingPayment = false,
            error = null
        )

        // Parse amount
        val amountDouble = amount.toDoubleOrNull()
        if (amountDouble == null) {
            state = state.copy(
                error = "Invalid amount format"
            )
            return
        }

        // Submit via use case
        viewModelScope.launch {
            state = state.copy(isSubmittingPayment = true)

            submitPaymentUseCase(
                recipientEmail = recipientEmail.trim(),
                amount = amountDouble,
                currency = currency
            )
                .onSuccess { response ->
                    state = state.copy(isSubmittingPayment = false)

                    if (response.success) {
                        // Dispatch one-time success action
                        dispatchAction(TransactionUIAction.PaymentSuccess)
                    } else {
                        state = state.copy(
                            error = response.error ?: "Unknown error"
                        )
                    }
                }
                .onFailure { e ->
                    state = state.copy(
                        isSubmittingPayment = false,
                        error = e.message ?: "Network error"
                    )
                }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        state = state.copy(error = null)
    }
}

/**
 * Represents the complete UI state for the transaction screen
 */
data class TransactionUIState(
    val transactions: List<Payment> = emptyList(),
    val isLoadingTransactions: Boolean = true,
    val isSubmittingPayment: Boolean = false,
    val error: String? = null
)

/**
 * One-time UI actions (events that should only be handled once)
 */
sealed class TransactionUIAction {
    /**
     * Payment submitted successfully - dismiss bottom sheet
     */
    data object PaymentSuccess : TransactionUIAction()
}