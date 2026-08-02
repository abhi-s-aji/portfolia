package com.example.portfolia.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DeepCharcoal,
    primaryContainer = GoldDark,
    onPrimaryContainer = OffWhite,
    secondary = Emerald,
    onSecondary = DeepCharcoal,
    secondaryContainer = EmeraldSubtle,
    onSecondaryContainer = Emerald,
    tertiary = CategoryDesign,
    onTertiary = OffWhite,
    background = DeepCharcoal,
    onBackground = OffWhite,
    surface = DarkSlate,
    onSurface = OffWhite,
    surfaceVariant = SlateHighlight,
    onSurfaceVariant = MutedPewter,
    outline = SlateBorder,
    outlineVariant = SlateHighlight,
    error = Crimson,
    onError = OffWhite,
    errorContainer = CrimsonSubtle,
    onErrorContainer = Crimson,
    inverseSurface = OffWhite,
    inverseOnSurface = DeepCharcoal,
    inversePrimary = GoldDark,
    surfaceTint = Gold
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = LightBackground,
    primaryContainer = GoldSubtle,
    onPrimaryContainer = GoldDark,
    secondary = Emerald,
    onSecondary = LightBackground,
    secondaryContainer = EmeraldSubtle,
    onSecondaryContainer = Emerald,
    tertiary = CategoryDesign,
    onTertiary = LightBackground,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceHighlight,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = LightSurfaceHighlight,
    error = Crimson,
    onError = LightBackground,
    errorContainer = CrimsonSubtle,
    onErrorContainer = Crimson,
    inverseSurface = DeepCharcoal,
    inverseOnSurface = OffWhite,
    inversePrimary = Gold,
    surfaceTint = GoldDark
)

@Composable
fun PortfoliaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PortfoliaTypography,
        shapes = PortfoliaShapes,
        content = content
    )
}
