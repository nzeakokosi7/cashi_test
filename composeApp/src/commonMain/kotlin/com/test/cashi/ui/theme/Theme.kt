package com.test.cashi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CashiPurpleLight,
    onPrimary = CashiWhite,
    primaryContainer = CashiPurpleDark,
    onPrimaryContainer = CashiWhite,

    secondary = CashiBlueLight,
    onSecondary = CashiWhite,

    tertiary = CashiGreenLight,
    onTertiary = CashiBlack,

    error = CashiRed,
    onError = CashiWhite,

    background = CashiDarkBackground,
    onBackground = CashiWhite,

    surface = CashiDarkSurface,
    onSurface = CashiWhite,
    surfaceVariant = CashiDarkSurfaceVariant,
    onSurfaceVariant = CashiGreyLight
)

private val LightColorScheme = lightColorScheme(
    primary = CashiPurple,
    onPrimary = CashiWhite,
    primaryContainer = CashiPurpleLight,
    onPrimaryContainer = CashiBlack,

    secondary = CashiBlue,
    onSecondary = CashiWhite,

    tertiary = CashiGreen,
    onTertiary = CashiWhite,

    error = CashiRed,
    onError = CashiWhite,

    background = CashiBackground,
    onBackground = CashiBlack,

    surface = CashiSurface,
    onSurface = CashiBlack,
    surfaceVariant = CashiSurfaceVariant,
    onSurfaceVariant = CashiGrey
)

@Composable
fun CashiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}