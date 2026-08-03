package com.example.portfolia.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    background = MoneeLightCanvas,
    surface = MoneeLightCardSurface,
    surfaceTint = Color.Transparent,
    onBackground = MoneeLightTextPrimary,
    onSurface = MoneeLightTextPrimary,
    onSurfaceVariant = MoneeLightTextSecondary
)

val DarkColorScheme = darkColorScheme(
    primary = MoneeTextPrimary,
    background = MoneeCanvas,
    surface = MoneeCardSurface,
    surfaceTint = Color.Transparent, // DISABLE ELEVATION TINTING
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = MoneeTextSecondary
)

@Composable
fun PortfoliaTheme(
    themeMode: String = "SYSTEM",
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemInDarkTheme()
    }

    val targetColors = if (useDarkTheme) DarkColorScheme else LightColorScheme

    val animatedBackground by animateColorAsState(
        targetValue = targetColors.background,
        animationSpec = tween(durationMillis = 300),
        label = "BackgroundColorAnimation"
    )
    val animatedSurface by animateColorAsState(
        targetValue = targetColors.surface,
        animationSpec = tween(durationMillis = 300),
        label = "SurfaceColorAnimation"
    )
    val animatedOnBackground by animateColorAsState(
        targetValue = targetColors.onBackground,
        animationSpec = tween(durationMillis = 300),
        label = "OnBackgroundColorAnimation"
    )
    val animatedOnSurface by animateColorAsState(
        targetValue = targetColors.onSurface,
        animationSpec = tween(durationMillis = 300),
        label = "OnSurfaceColorAnimation"
    )
    val animatedOnSurfaceVariant by animateColorAsState(
        targetValue = targetColors.onSurfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "OnSurfaceVariantColorAnimation"
    )

    val colors = targetColors.copy(
        background = animatedBackground,
        surface = animatedSurface,
        onBackground = animatedOnBackground,
        onSurface = animatedOnSurface,
        onSurfaceVariant = animatedOnSurfaceVariant
    )

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}
