package com.example.portfolia.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AmbientGlassBackground(
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "apple_music_mesh")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mesh_drift"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0C10)) // Deep Apple Music Midnight Base
    ) {
        if (enabled) {
            // Warm Apple Music ambient color mesh (Crimson, Soft Indigo, Deep Violet, Rose)
            val warmRed = Color(0xFFFA2D55).copy(alpha = 0.38f)
            val softIndigo = Color(0xFF5E5CE6).copy(alpha = 0.35f)
            val deepViolet = Color(0xFF8E44AD).copy(alpha = 0.30f)
            val softRose = Color(0xFFFF3B30).copy(alpha = 0.25f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(85.dp)
            ) {
                // Top-Left Warm Red Glow
                Box(
                    modifier = Modifier
                        .size(340.dp)
                        .offset(x = (-40 + animOffset / 3).dp, y = (-30).dp)
                        .background(Brush.radialGradient(listOf(warmRed, Color.Transparent)))
                )
                // Center-Right Indigo Glow
                Box(
                    modifier = Modifier
                        .size(360.dp)
                        .offset(x = (140 - animOffset / 2).dp, y = (220 + animOffset).dp)
                        .background(Brush.radialGradient(listOf(softIndigo, Color.Transparent)))
                )
                // Bottom Violet/Rose Glow
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .offset(x = (20).dp, y = (520 - animOffset / 2).dp)
                        .background(Brush.radialGradient(listOf(deepViolet, softRose, Color.Transparent)))
                )
            }
        }
        content()
    }
}
