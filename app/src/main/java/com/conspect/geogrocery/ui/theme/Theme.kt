package com.conspect.geogrocery.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Single dark scheme; the app is designed dark-first to match the mockup.
private val DarkColors = darkColorScheme(
    primary = AccentGreen,
    onPrimary = Color.White,
    secondary = AccentGreen,
    onSecondary = Color.White,
    tertiary = AccentGreen,
    background = DarkBackground,
    onBackground = OnDark,
    surface = DarkSurface,
    onSurface = OnDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun GeoGroceryTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
    )
}
