package com.example.portfolia.ui.theme

import androidx.compose.ui.graphics.Color

enum class ThemeAccent(
    val displayName: String,
    val primary: Color,
    val mesh1: Color,
    val mesh2: Color,
    val mesh3: Color
) {
    RED(
        displayName = "Apple Crimson",
        primary = Color(0xFFFA2D55),
        mesh1 = Color(0xFFFA2D55).copy(alpha = 0.38f),
        mesh2 = Color(0xFF5E5CE6).copy(alpha = 0.32f),
        mesh3 = Color(0xFF8E44AD).copy(alpha = 0.28f)
    ),
    BLUE(
        displayName = "Sapphire Blue",
        primary = Color(0xFF0A84FF),
        mesh1 = Color(0xFF0A84FF).copy(alpha = 0.38f),
        mesh2 = Color(0xFF5E5CE6).copy(alpha = 0.35f),
        mesh3 = Color(0xFF64D2FF).copy(alpha = 0.28f)
    ),
    GREEN(
        displayName = "Emerald Forest",
        primary = Color(0xFF30D158),
        mesh1 = Color(0xFF30D158).copy(alpha = 0.35f),
        mesh2 = Color(0xFF0A84FF).copy(alpha = 0.30f),
        mesh3 = Color(0xFF5E5CE6).copy(alpha = 0.25f)
    ),
    PURPLE(
        displayName = "Amethyst Velvet",
        primary = Color(0xFFBF5AF2),
        mesh1 = Color(0xFFBF5AF2).copy(alpha = 0.38f),
        mesh2 = Color(0xFFFA2D55).copy(alpha = 0.30f),
        mesh3 = Color(0xFF5E5CE6).copy(alpha = 0.30f)
    )
}
