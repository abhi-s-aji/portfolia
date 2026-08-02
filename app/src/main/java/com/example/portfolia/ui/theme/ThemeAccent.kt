package com.example.portfolia.ui.theme

import androidx.compose.ui.graphics.Color

enum class ThemeAccent(
    val displayName: String,
    val primary: Color,
    val mesh1: Color,
    val mesh2: Color,
    val mesh3: Color
) {
    SAPPHIRE(
        displayName = "Sapphire",
        primary = Color(0xFF0984E3),
        mesh1 = Color(0xFF0984E3).copy(alpha = 0.38f),
        mesh2 = Color(0xFF6C5CE7).copy(alpha = 0.32f),
        mesh3 = Color(0xFF00CEC9).copy(alpha = 0.25f)
    ),
    ROSE_GOLD(
        displayName = "Rose Gold",
        primary = Color(0xFFFF6B6B),
        mesh1 = Color(0xFFFF6B6B).copy(alpha = 0.38f),
        mesh2 = Color(0xFFFECA57).copy(alpha = 0.30f),
        mesh3 = Color(0xFFFD79A8).copy(alpha = 0.25f)
    ),
    FOREST(
        displayName = "Forest",
        primary = Color(0xFF00B894),
        mesh1 = Color(0xFF00B894).copy(alpha = 0.35f),
        mesh2 = Color(0xFF00CEC9).copy(alpha = 0.30f),
        mesh3 = Color(0xFF0984E3).copy(alpha = 0.25f)
    ),
    AMETHYST(
        displayName = "Amethyst",
        primary = Color(0xFFA29BFE),
        mesh1 = Color(0xFFA29BFE).copy(alpha = 0.38f),
        mesh2 = Color(0xFF6C5CE7).copy(alpha = 0.35f),
        mesh3 = Color(0xFFFD79A8).copy(alpha = 0.25f)
    ),
    SUNSET(
        displayName = "Sunset",
        primary = Color(0xFFFD79A8),
        mesh1 = Color(0xFFFD79A8).copy(alpha = 0.38f),
        mesh2 = Color(0xFFFDCB6E).copy(alpha = 0.32f),
        mesh3 = Color(0xFFFF6B6B).copy(alpha = 0.28f)
    )
}

enum class GlassIntensity(val displayName: String, val alpha: Float, val blurDp: Int) {
    SUBTLE("Subtle", 0.05f, 40),
    MEDIUM("Medium", 0.10f, 75),
    BOLD("Bold", 0.18f, 110)
}

enum class LayoutDensity(val displayName: String, val paddingDp: Int) {
    COZY("Cozy", 20),
    COMFORTABLE("Comfortable", 14),
    COMPACT("Compact", 8)
}
