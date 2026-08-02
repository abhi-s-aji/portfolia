package com.example.portfolia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.portfolia.ui.components.AppleGlassCard
import com.example.portfolia.ui.theme.GlassIntensity
import com.example.portfolia.ui.theme.LayoutDensity
import com.example.portfolia.ui.theme.ThemeAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isGlassmorphism: Boolean,
    accent: ThemeAccent,
    intensity: GlassIntensity,
    density: LayoutDensity,
    onGlassmorphismToggle: (Boolean) -> Unit,
    onAccentSelected: (ThemeAccent) -> Unit,
    onIntensitySelected: (GlassIntensity) -> Unit,
    onDensitySelected: (LayoutDensity) -> Unit
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // GLASSMORPHISM SWITCH CARD
            AppleGlassCard(isGlassmorphism = isGlassmorphism, intensity = intensity) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Glassmorphism 2.0 Effect", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
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

            // ACCENT COLOR PICKER CARD
            AppleGlassCard(isGlassmorphism = isGlassmorphism, intensity = intensity) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Accent Color Palette", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
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
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
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

            // GLASS INTENSITY CARD
            AppleGlassCard(isGlassmorphism = isGlassmorphism, intensity = intensity) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Glass Intensity", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GlassIntensity.values().forEach { level ->
                            val isSelected = intensity == level
                            FilterChip(
                                selected = isSelected,
                                onClick = { onIntensitySelected(level) },
                                label = { Text(level.displayName) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accent.primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White.copy(alpha = 0.05f),
                                    labelColor = Color.White.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }
            }

            // LAYOUT DENSITY CARD
            AppleGlassCard(isGlassmorphism = isGlassmorphism, intensity = intensity) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Layout Density", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LayoutDensity.values().forEach { dens ->
                            val isSelected = density == dens
                            FilterChip(
                                selected = isSelected,
                                onClick = { onDensitySelected(dens) },
                                label = { Text(dens.displayName) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accent.primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White.copy(alpha = 0.05f),
                                    labelColor = Color.White.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
