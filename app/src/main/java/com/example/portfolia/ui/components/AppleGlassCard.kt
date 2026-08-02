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
    val shape = RoundedCornerShape(22.dp)

    if (isGlassmorphism) {
        val glassBorder = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.05f)
            )
        )

        Card(
            onClick = { onClick?.invoke() },
            enabled = onClick != null,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = intensity.alpha)
            ),
            border = BorderStroke(1.dp, glassBorder),
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = intensity.alpha + 0.04f),
                                Color.White.copy(alpha = 0.01f)
                            )
                        )
                    )
                    .padding(18.dp),
                content = content
            )
        }
    } else {
        Card(
            onClick = { onClick?.invoke() },
            enabled = onClick != null,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            modifier = modifier
        ) {
            Column(modifier = Modifier.padding(18.dp), content = content)
        }
    }
}
