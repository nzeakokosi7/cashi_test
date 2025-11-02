package com.test.cashi

import androidx.compose.runtime.*
import com.test.cashi.ui.localization.LocalizationProvider
import com.test.cashi.ui.screens.TransactionScreen
import com.test.cashi.ui.theme.CashiTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    LocalizationProvider {
        CashiTheme {
            TransactionScreen()
        }
    }
}