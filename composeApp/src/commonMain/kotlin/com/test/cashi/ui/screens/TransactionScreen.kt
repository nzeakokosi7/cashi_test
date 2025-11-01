package com.test.cashi.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.test.cashi.ui.TransactionUIAction
import com.test.cashi.ui.TransactionViewModel
import com.test.cashi.ui.components.PaymentBottomSheet
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main transaction screen that demonstrates BaseViewModel usage
 *
 * Shows how to:
 * - Collect UI state from viewModel.state
 * - Collect one-time actions from viewModel.actions
 * - Handle state-driven UI rendering
 * - Handle one-time events (like dismissing bottom sheet)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel = koinViewModel()
) {
    // Collect UI state (automatically recomposes when state changes)
    val state = viewModel.state

    // State for bottom sheet
    var showPaymentSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Collect one-time UI actions
    LaunchedEffect(Unit) {
        viewModel.actions.collectLatest { action ->
            when (action) {
                TransactionUIAction.PaymentSuccess -> {
                    // Dismiss bottom sheet on success
                    sheetState.hide()
                    showPaymentSheet = false
                }
            }
        }
    }

    // Render UI based on state
    TransactionListScreen(
        transactions = state.transactions,
        isLoading = state.isLoadingTransactions,
        onAddPaymentClick = { showPaymentSheet = true }
    )

    // Show payment bottom sheet
    if (showPaymentSheet) {
        PaymentBottomSheet(
            sheetState = sheetState,
            onDismiss = {
                showPaymentSheet = false
                viewModel.clearError()
            },
            onSubmitPayment = { email, amount, currency ->
                viewModel.submitPayment(email, amount, currency)
            },
            isLoading = state.isSubmittingPayment,
            errorMessage = state.error
        )
    }
}