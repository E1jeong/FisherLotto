package com.queentech.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ColorScheme = lightColorScheme(
//    primary = primaryBlue3,
//    onPrimary = Color.White,
//    primaryContainer = primaryBlue1,
//    onPrimaryContainer = primaryBlue5,
//    secondary = secondaryBlue3,
//    onSecondary = Color.White,
//    secondaryContainer = secondaryBlue1,
//    onSecondaryContainer = secondaryBlue5,
//    tertiary = secondaryGreen3,
//    onTertiary = Color.White,
//    tertiaryContainer = secondaryGreen1,
//    onTertiaryContainer = secondaryGreen5,
//    background = Color.White,
//    onBackground = Color.Black,
//    surface = Color.White,
//    onSurface = Color.Black,
//    error = Color.Red,
//    onError = Color.White
)

@Composable
fun FisherLottoTheme(
    content: @Composable () -> Unit
) {
    val darkTheme: Boolean = isSystemInDarkTheme()
    val colorScheme = ColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
