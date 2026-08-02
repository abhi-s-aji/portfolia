package com.example.portfolia.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.portfolia.ui.theme.ThemeAccent

@Composable
fun AmbientGlassBackground(
    enabled: Boolean = true,
    accent: ThemeAccent = ThemeAccent.SAPPHIRE,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh_drift")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 90f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mesh_drift"
    )

    // Smooth color morphing when switching theme accents
    val mesh1Color by animateColorAsState(targetValue = accent.mesh1, animationSpec = tween(600), label = "m1")
    val mesh2Color by animateColorAsState(targetValue = accent.mesh2, animationSpec = tween(600), label = "m2")
    val mesh3Color by animateColorAsState(targetValue = accent.mesh3, animationSpec = tween(600), label = "m3")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0C10)) // Deep Midnight Base
    ) {
        if (enabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(95.dp)
            ) {
                // Top-Left Primary Glow
                Box(
                    modifier = Modifier
                        .size(360.dp)
                        .offset(x = (-50 + animOffset / 3).dp, y = (-40).dp)
                        .background(Brush.radialGradient(listOf(mesh1Color, Color.Transparent)))
                )
                // Center-Right Secondary Glow
                Box(
                    modifier = Modifier
                        .size(380.dp)
                        .offset(x = (150 - animOffset / 2).dp, y = (240 + animOffset).dp)
                        .background(Brush.radialGradient(listOf(mesh2Color, Color.Transparent)))
                )
                // Bottom Tertiary Glow
                Box(
                    modifier = Modifier
                        .size(340.dp)
                        .offset(x = (20).dp, y = (540 - animOffset / 2).dp)
                        .background(Brush.radialGradient(listOf(mesh3Color, Color.Transparent)))
                )
            }
        }
        content()
    }
}
