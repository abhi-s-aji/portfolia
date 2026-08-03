package com.example.portfolia.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun PortfoliaTheme(
    content: @Composable () -> Unit
) {
    val colors = darkColorScheme(
        primary = MoneeTextPrimary,
        background = MoneeCanvas,
        surface = MoneeCardSurface,
        surfaceTint = Color.Transparent, // DISABLE ELEVATION TINTING
        onBackground = Color.White,
        onSurface = Color.White,
        onSurfaceVariant = MoneeTextSecondary
    )

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}
