package com.example.portfolia.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.portfolia.ui.theme.ThemeAccent

@Composable
fun AmbientGlassBackground(
    enabled: Boolean = true,
    accent: ThemeAccent = ThemeAccent.SAPPHIRE,
    content: @Composable BoxScope.() -> Unit
) {
    val primaryColor by animateColorAsState(
        targetValue = accent.primary.copy(alpha = 0.18f),
        animationSpec = tween(500),
        label = "bg_primary"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12)) // Deep Professional Slate Base
    ) {
        if (enabled) {
            // Hardware-accelerated radial ambient spotlight (Zero GPU blur overhead)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.95f }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(primaryColor, Color.Transparent),
                            radius = 1200f
                        )
                    )
            )
        }
        content()
    }
}
