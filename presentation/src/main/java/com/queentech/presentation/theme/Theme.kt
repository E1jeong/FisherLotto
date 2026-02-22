package com.queentech.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FisherLottoDarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = CardBg,
    onPrimaryContainer = TextPrimary,
    secondary = AccentGold,
    onSecondary = BgDark,
    secondaryContainer = SectionBg,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentGreen,
    onTertiary = Color.White,
    tertiaryContainer = SectionBg,
    onTertiaryContainer = AccentGreen,
    background = BgDark,
    onBackground = TextPrimary,
    surface = SectionBg,
    onSurface = TextPrimary,
    surfaceVariant = CardBg,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    outlineVariant = DividerColor,
    error = AccentRed,
    onError = Color.White,
    errorContainer = AccentRed.copy(alpha = 0.2f),
    onErrorContainer = AccentRed,
)

@Composable
fun FisherLottoTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = FisherLottoDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BgDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
