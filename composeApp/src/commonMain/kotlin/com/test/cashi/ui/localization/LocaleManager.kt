package com.test.cashi.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Composition Local for accessing current strings
 */
val LocalStrings = compositionLocalOf<Strings> { EnglishStrings }

/**
 * Composition Local for accessing locale switching function
 */
val LocalLocaleManager = compositionLocalOf<LocaleManager> {
    error("LocaleManager not provided")
}

/**
 * Manages the current locale and provides string resources
 */
class LocaleManager {
    var currentLocale by mutableStateOf(SupportedLocale.ENGLISH)
        private set

    val strings: Strings
        get() = currentLocale.strings

    fun setLocale(locale: SupportedLocale) {
        currentLocale = locale
    }

    fun toggleLocale() {
        currentLocale = when (currentLocale) {
            SupportedLocale.ENGLISH -> SupportedLocale.ARABIC
            SupportedLocale.ARABIC -> SupportedLocale.ENGLISH
        }
    }
}

/**
 * Provides localization context to the app
 */
@Composable
fun LocalizationProvider(
    localeManager: LocaleManager = remember { LocaleManager() },
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalStrings provides localeManager.strings,
        LocalLocaleManager provides localeManager,
        content = content
    )
}