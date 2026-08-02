package com.example.portfolia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.portfolia.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isGlassmorphism: Boolean,
    onGlassmorphismToggle: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Customization") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
            GlassCard(isGlassmorphism = isGlassmorphism) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Glassmorphism UI Effect", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Enable translucent frosted-glass styling across project cards and menus.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isGlassmorphism,
                        onCheckedChange = onGlassmorphismToggle
                    )
                }
            }

            GlassCard(isGlassmorphism = isGlassmorphism) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Theme System", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Material You dynamic theme automatically extracts wallpaper palette colors on Android 12+.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
