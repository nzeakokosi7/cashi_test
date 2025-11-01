package com.test.cashi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.test.cashi.domain.model.Currency
import com.test.cashi.domain.model.Payment
import com.test.cashi.domain.model.TransactionStatus
import com.test.cashi.ui.components.EmptyStateView
import com.test.cashi.ui.components.TransactionCard
import com.test.cashi.ui.theme.CashiTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    transactions: List<Payment>,
    onAddPaymentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPaymentClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Payment"
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        if (transactions.isEmpty()) {
            EmptyStateView(
                onAddPaymentClick = onAddPaymentClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(transactions, key = { it.id }) { payment ->
                    TransactionCard(payment = payment)
                }
            }
        }
    }
}

// ==================== Previews ====================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Preview
@Composable
fun TransactionListScreenEmptyPreview() {
    CashiTheme {
        TransactionListScreen(
            transactions = emptyList(),
            onAddPaymentClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Preview
@Composable
fun TransactionListScreenWithTransactionsPreview() {
    CashiTheme {
        TransactionListScreen(
            transactions = listOf(
                Payment(
                    id = "1",
                    recipientEmail = "john.doe@example.com",
                    amount = 150.00,
                    currency = Currency.USD,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    status = TransactionStatus.COMPLETED
                ),
                Payment(
                    id = "2",
                    recipientEmail = "jane.smith@company.com",
                    amount = 250.50,
                    currency = Currency.EUR,
                    timestamp = Clock.System.now().toEpochMilliseconds() - 3600000,
                    status = TransactionStatus.PENDING
                ),
                Payment(
                    id = "3",
                    recipientEmail = "failed.payment@test.com",
                    amount = 99.99,
                    currency = Currency.USD,
                    timestamp = Clock.System.now().toEpochMilliseconds() - 7200000,
                    status = TransactionStatus.FAILED
                )
            ),
            onAddPaymentClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Preview
@Composable
fun TransactionListScreenManyTransactionsPreview() {
    CashiTheme {
        TransactionListScreen(
            transactions = List(10) { index ->
                Payment(
                    id = index.toString(),
                    recipientEmail = "user$index@example.com",
                    amount = (50 + index * 25).toDouble(),
                    currency = if (index % 2 == 0) Currency.USD else Currency.EUR,
                    timestamp = Clock.System.now().toEpochMilliseconds() - (index * 3600000L),
                    status = when (index % 3) {
                        0 -> TransactionStatus.COMPLETED
                        1 -> TransactionStatus.PENDING
                        else -> TransactionStatus.FAILED
                    }
                )
            },
            onAddPaymentClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Preview
@Composable
fun TransactionListScreenDarkPreview() {
    CashiTheme(darkTheme = true) {
        TransactionListScreen(
            transactions = listOf(
                Payment(
                    id = "1",
                    recipientEmail = "darkmode@example.com",
                    amount = 1250.00,
                    currency = Currency.EUR,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    status = TransactionStatus.COMPLETED
                ),
                Payment(
                    id = "2",
                    recipientEmail = "pending@test.com",
                    amount = 75.50,
                    currency = Currency.USD,
                    timestamp = Clock.System.now().toEpochMilliseconds() - 3600000,
                    status = TransactionStatus.PENDING
                )
            ),
            onAddPaymentClick = {}
        )
    }
}