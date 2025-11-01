package com.test.cashi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.test.cashi.domain.model.Currency
import com.test.cashi.domain.model.Payment
import com.test.cashi.domain.model.TransactionStatus
import com.test.cashi.ui.theme.*
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun TransactionCard(
    payment: Payment,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Recipient and date
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = payment.recipientEmail,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = formatTimestamp(payment.timestamp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Status badge
                StatusBadge(status = payment.status)
            }

            // Right side - Amount
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = payment.formattedAmount(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = when (payment.status) {
                        TransactionStatus.COMPLETED -> CashiGreen
                        TransactionStatus.FAILED -> CashiRed
                        TransactionStatus.PENDING -> MaterialTheme.colorScheme.onSurface
                    }
                )

                Text(
                    text = payment.currency.code,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    status: TransactionStatus,
    modifier: Modifier = Modifier
) {
    val (icon, color, text) = when (status) {
        TransactionStatus.PENDING -> Triple(
            Icons.Default.HourglassEmpty,
            CashiOrange,
            "Pending"
        )
        TransactionStatus.COMPLETED -> Triple(
            Icons.Default.CheckCircle,
            CashiGreen,
            "Completed"
        )
        TransactionStatus.FAILED -> Triple(
            Icons.Default.Error,
            CashiRed,
            "Failed"
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val month = localDateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    val day = localDateTime.dayOfMonth
    val year = localDateTime.year
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')

    return "$month $day, $year at $hour:$minute"
}

// ==================== Previews ====================

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun TransactionCardCompletedPreview() {
    CashiTheme {
        Surface {
            TransactionCard(
                payment = Payment(
                    id = "1",
                    recipientEmail = "john.doe@example.com",
                    amount = 150.00,
                    currency = Currency.USD,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    status = TransactionStatus.COMPLETED
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun TransactionCardPendingPreview() {
    CashiTheme {
        Surface {
            TransactionCard(
                payment = Payment(
                    id = "2",
                    recipientEmail = "jane.smith@company.com",
                    amount = 250.50,
                    currency = Currency.EUR,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    status = TransactionStatus.PENDING
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun TransactionCardFailedPreview() {
    CashiTheme {
        Surface {
            TransactionCard(
                payment = Payment(
                    id = "3",
                    recipientEmail = "failed.payment@test.com",
                    amount = 99.99,
                    currency = Currency.USD,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    status = TransactionStatus.FAILED
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun TransactionCardDarkPreview() {
    CashiTheme(darkTheme = true) {
        Surface {
            TransactionCard(
                payment = Payment(
                    id = "4",
                    recipientEmail = "darkmode@example.com",
                    amount = 1250.00,
                    currency = Currency.EUR,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    status = TransactionStatus.COMPLETED
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}