package com.test.cashi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.test.cashi.domain.model.Currency
import com.test.cashi.ui.theme.CashiTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSubmitPayment: (recipientEmail: String, amount: String, currency: Currency) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        PaymentFormContent(
            onSubmitPayment = onSubmitPayment,
            onCancel = onDismiss,
            isLoading = isLoading,
            errorMessage = errorMessage
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFormContent(
    onSubmitPayment: (recipientEmail: String, amount: String, currency: Currency) -> Unit,
    onCancel: () -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var recipientEmail by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(Currency.USD) }
    var showCurrencyDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text = "Send Payment",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Recipient Email Field
        OutlinedTextField(
            value = recipientEmail,
            onValueChange = { recipientEmail = it },
            label = { Text("Recipient Email") },
            placeholder = { Text("example@email.com") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Amount Field
        OutlinedTextField(
            value = amount,
            onValueChange = {
                // Only allow numbers and single decimal point
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                    amount = it
                }
            },
            label = { Text("Amount") },
            placeholder = { Text("0.00") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Currency Selection
        ExposedDropdownMenuBox(
            expanded = showCurrencyDropdown,
            onExpandedChange = { showCurrencyDropdown = !showCurrencyDropdown }
        ) {
            OutlinedTextField(
                value = "${selectedCurrency.code} (${selectedCurrency.symbol})",
                onValueChange = {},
                readOnly = true,
                label = { Text("Currency") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCurrencyDropdown)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            ExposedDropdownMenu(
                expanded = showCurrencyDropdown,
                onDismissRequest = { showCurrencyDropdown = false }
            ) {
                Currency.entries.forEach { currency ->
                    DropdownMenuItem(
                        text = {
                            Text("${currency.code} (${currency.symbol})")
                        },
                        onClick = {
                            selectedCurrency = currency
                            showCurrencyDropdown = false
                        }
                    )
                }
            }
        }

        // Error Message
        if (errorMessage != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Submit Button
        Button(
            onClick = {
                onSubmitPayment(recipientEmail.trim(), amount.trim(), selectedCurrency)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading && recipientEmail.isNotBlank() && amount.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Send Payment",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Cancel Button
        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==================== Previews ====================

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PaymentFormContentPreview() {
    CashiTheme {
        Surface {
            PaymentFormContent(
                onSubmitPayment = { _, _, _ -> },
                onCancel = {},
                isLoading = false,
                errorMessage = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PaymentFormContentLoadingPreview() {
    CashiTheme {
        Surface {
            PaymentFormContent(
                onSubmitPayment = { _, _, _ -> },
                onCancel = {},
                isLoading = true,
                errorMessage = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PaymentFormContentErrorPreview() {
    CashiTheme {
        Surface {
            PaymentFormContent(
                onSubmitPayment = { _, _, _ -> },
                onCancel = {},
                isLoading = false,
                errorMessage = "Invalid email address. Please enter a valid email."
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PaymentFormContentDarkPreview() {
    CashiTheme(darkTheme = true) {
        Surface {
            PaymentFormContent(
                onSubmitPayment = { _, _, _ -> },
                onCancel = {},
                isLoading = false,
                errorMessage = null
            )
        }
    }
}