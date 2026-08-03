package com.example.portfolia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AvatarPreset(
    val id: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val contentDescription: String
)

val AVATAR_PRESETS = listOf(
    AvatarPreset("preset:architect", Icons.Default.Code, listOf(Color(0xFF2C2D35), Color(0xFF1E1E24)), "Architect"),
    AvatarPreset("preset:security", Icons.Default.Shield, listOf(Color(0xFF142C29), Color(0xFF0F1E20)), "Security"),
    AvatarPreset("preset:creator", Icons.Default.RocketLaunch, listOf(Color(0xFF221F3B), Color(0xFF161424)), "Creator"),
    AvatarPreset("preset:mobile", Icons.Default.DeveloperMode, listOf(Color(0xFF1B2E3C), Color(0xFF111E26)), "Mobile"),
    AvatarPreset("preset:systems", Icons.Default.Memory, listOf(Color(0xFF24252A), Color(0xFF17181C)), "Systems")
)

@Composable
fun PresetAvatarImage(
    presetId: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 32.dp
) {
    val preset = AVATAR_PRESETS.firstOrNull { it.id == presetId } ?: AVATAR_PRESETS[0]

    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(preset.gradientColors)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = preset.icon,
            contentDescription = preset.contentDescription,
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}
