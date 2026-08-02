package com.example.portfolia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.portfolia.ui.components.AppleGlassCard
import com.example.portfolia.ui.theme.ThemeAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isGlassmorphism: Boolean,
    accent: ThemeAccent,
    onGlassmorphismToggle: (Boolean) -> Unit,
    onAccentSelected: (ThemeAccent) -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("App Customization", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // GLASSMORPHISM SWITCH CARD
            AppleGlassCard(isGlassmorphism = isGlassmorphism) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Glassmorphism UI Effect", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Translucent frosted-glass cards with ambient gradient glows.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = isGlassmorphism,
                        onCheckedChange = onGlassmorphismToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accent.primary,
                            uncheckedThumbColor = Color(0xFF8E8E93),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            }

            // COLOR PALETTE ACCENT SELECTOR CARD
            AppleGlassCard(isGlassmorphism = isGlassmorphism) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Accent Color Theme", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text(
                        text = "Customize the background gradient mesh and accent highlights.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ThemeAccent.values().forEach { item ->
                            val isSelected = accent == item
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { onAccentSelected(item) }
                                    .padding(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(item.primary)
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = if (isSelected) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.displayName.split(" ")[0],
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
