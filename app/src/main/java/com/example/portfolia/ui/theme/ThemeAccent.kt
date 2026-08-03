package com.example.portfolia.ui.theme

import androidx.compose.ui.graphics.Color

enum class ThemeAccent(
    val displayName: String,
    val background: Color,
    val surface: Color,
    val primary: Color,
    val textPrimary: Color,
    val textMuted: Color
) {
    OBSIDIAN_DARK(
        displayName = "Obsidian Dark",
        background = Color(0xFF141415),
        surface = Color(0xFF1E1E20),
        primary = Color(0xFFFFFFFF),
        textPrimary = Color(0xFFFFFFFF),
        textMuted = Color(0xFF8E8E93)
    ),
    SLATE_STEEL(
        displayName = "Slate Steel",
        background = Color(0xFF08090C),
        surface = Color(0xFF101216),
        primary = Color(0xFF94A3B8),
        textPrimary = Color(0xFFF8FAFC),
        textMuted = Color(0xFF64748B)
    ),
    ROYAL_SAPPHIRE(
        displayName = "Royal Sapphire",
        background = Color(0xFF05070A),
        surface = Color(0xFF11161D),
        primary = Color(0xFF3D7EFF),
        textPrimary = Color(0xFFF7FAFF),
        textMuted = Color(0xFF9BBEFF)
    ),
    EMERALD_PRO(
        displayName = "Emerald Pro",
        background = Color(0xFF050505),
        surface = Color(0xFF101317),
        primary = Color(0xFF00C853),
        textPrimary = Color(0xFFF5FFF7),
        textMuted = Color(0xFF69F0AE)
    ),
    TITANIUM_GOLD(
        displayName = "Titanium Gold",
        background = Color(0xFF000000),
        surface = Color(0xFF111111),
        primary = Color(0xFFD4AF37),
        textPrimary = Color(0xFFF8F8F8),
        textMuted = Color(0xFFA78B4A)
    ),
    WARM_CORAL(
        displayName = "Warm Coral",
        background = Color(0xFF0D0E12),
        surface = Color(0xFF15171C),
        primary = Color(0xFFF26D5B),
        textPrimary = Color(0xFFFAFAFA),
        textMuted = Color(0xFFFFB4A2)
    ),
    MINIMAL_STARK(
        displayName = "Minimal Stark",
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFF5F5F5),
        primary = Color(0xFF000000),
        textPrimary = Color(0xFF111111),
        textMuted = Color(0xFF6B7280)
    )
}

enum class LayoutDensity(val displayName: String, val paddingDp: Int) {
    COZY("Cozy", 20),
    COMFORTABLE("Comfortable", 14),
    COMPACT("Compact", 8)
}
