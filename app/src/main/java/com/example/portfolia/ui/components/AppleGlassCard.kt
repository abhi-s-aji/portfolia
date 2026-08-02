package com.example.portfolia.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.portfolia.ui.theme.GlassIntensity

@Composable
fun AppleGlassCard(
    modifier: Modifier = Modifier,
    isGlassmorphism: Boolean = true,
    intensity: GlassIntensity = GlassIntensity.MEDIUM,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)

    // Ultra-sharp 1px specular edge border
    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = if (isGlassmorphism) 0.16f else 0.08f),
            Color.White.copy(alpha = 0.03f)
        )
    )

    // Crisp dark slate surface hierarchy (No muddy gray fills)
    val cardBg = if (isGlassmorphism) {
        Color(0xFF161820).copy(alpha = 0.75f)
    } else {
        Color(0xFF161820)
    }

    Card(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderBrush),
        modifier = modifier.graphicsLayer {
            // Offload rendering layers directly to GPU
            clip = true
            shadowElevation = 0f
        }
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
                .padding(18.dp),
            content = content
        )
    }
}
