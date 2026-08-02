package com.example.portfolia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.portfolia.ui.components.AppleGlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isGlassmorphism: Boolean,
    onGlassmorphismToggle: (Boolean) -> Unit
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
            AppleGlassCard(isGlassmorphism = isGlassmorphism) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Glassmorphism UI Effect", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enable translucent frosted-glass styling across project cards and menus.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = isGlassmorphism,
                        onCheckedChange = onGlassmorphismToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFFA2D55),
                            uncheckedThumbColor = Color(0xFF8E8E93),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            }

            AppleGlassCard(isGlassmorphism = isGlassmorphism) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Theme System", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text(
                        text = "Apple Music liquid mesh gradient background with rich colors (Warm Apple Red, Soft Indigo, Deep Violet) extracts a premium mobile developer portfolio experience.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
