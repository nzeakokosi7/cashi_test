package com.test.cashi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.cashi.ui.theme.CashiTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Loading state component for transaction list
 *
 * Displays a centered circular progress indicator with a loading message.
 * Used while initial transaction data is being fetched from Firebase.
 */
@Composable
fun LoadingStateView(
    modifier: Modifier = Modifier,
    message: String = "Loading transactions..."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==================== Previews ====================

@Preview
@Composable
fun LoadingStatePreview() {
    CashiTheme {
        Surface {
            LoadingStateView()
        }
    }
}

@Preview
@Composable
fun LoadingStateDarkPreview() {
    CashiTheme(darkTheme = true) {
        Surface {
            LoadingStateView()
        }
    }
}

@Preview
@Composable
fun LoadingStateCustomMessagePreview() {
    CashiTheme {
        Surface {
            LoadingStateView(message = "Syncing with Firebase...")
        }
    }
}