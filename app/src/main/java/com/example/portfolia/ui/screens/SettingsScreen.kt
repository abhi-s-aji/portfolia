package com.example.portfolia.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
                title = { Text("Customization", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // GLASSMORPHISM TOGGLE
            AppleGlassCard(isGlassmorphism = isGlassmorphism, intensity = intensity) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Translucent Surfaces", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text("Enable frosted specular surfaces with ambient illumination.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF989A9C))
                    }
                    Switch(
                        checked = isGlassmorphism,
                        onCheckedChange = onGlassmorphismToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accent.primary,
                            uncheckedTrackColor = Color(0xFF2B2E3A)
                        )
                    )
                }
            }

            // ACCENT PALETTE SELECTOR
            AppleGlassCard(isGlassmorphism = isGlassmorphism, intensity = intensity) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Accent Theme", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ThemeAccent.values().forEach { item ->
                            val isSelected = accent == item
                            val borderColor by animateColorAsState(
                                targetValue = if (isSelected) Color.White else Color.Transparent,
                                label = "border_anim"
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onAccentSelected(item) }
                                    .padding(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(item.primary)
                                        .border(2.dp, borderColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.displayName.split(" ")[0],
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White else Color(0xFF8E9094)
                                )
                            }
                        }
                    }
                }
            }

            // INTENSITY SEGMENT CONTROL
            AppleGlassCard(isGlassmorphism = isGlassmorphism, intensity = intensity) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Glass Intensity", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F1015))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        GlassIntensity.values().forEach { level ->
                            val isSelected = intensity == level
                            val bg by animateColorAsState(
                                targetValue = if (isSelected) accent.primary else Color.Transparent,
                                label = "intensity_bg"
                            )

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bg)
                                    .clickable { onIntensitySelected(level) }
                            ) {
                                Text(
                                    text = level.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) Color.White else Color(0xFF8E9094),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // DENSITY SEGMENT CONTROL
            AppleGlassCard(isGlassmorphism = isGlassmorphism, intensity = intensity) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Layout Density", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F1015))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LayoutDensity.values().forEach { dens ->
                            val isSelected = density == dens
                            val bg by animateColorAsState(
                                targetValue = if (isSelected) accent.primary else Color.Transparent,
                                label = "density_bg"
                            )

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bg)
                                    .clickable { onDensitySelected(dens) }
                            ) {
                                Text(
                                    text = dens.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) Color.White else Color(0xFF8E9094),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
