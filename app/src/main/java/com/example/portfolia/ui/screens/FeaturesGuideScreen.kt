package com.example.portfolia.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturesGuideScreen(
    onBackClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF141415)
    val canvasBg = if (isDark) Color(0xFF141415) else Color(0xFFF6F6F8)
    val cardBg = if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF)
    val borderColor = if (isDark) Color(0xFF2A2A2D) else Color(0xFFE5E5EA)
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        containerColor = canvasBg,
        topBar = {
            TopAppBar(
                title = { Text("Features Guide", fontWeight = FontWeight.Bold, color = textColor) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Learn how to get the most out of your offline workspace.",
                style = MaterialTheme.typography.bodyMedium,
                color = subTextColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val features = listOf(
                FeatureGuideData(
                    title = "Projects Hub",
                    overview = "Central portfolio manager for showcasing software projects, tech stacks, and live links.",
                    usage = "Tap the add button on the Projects screen to register a new build. Add tags, GitHub links, and deployment URLs."
                ),
                FeatureGuideData(
                    title = "Saved Links and Code Snippets",
                    overview = "Dual-purpose reference engine for storing web bookmarks and code snippets.",
                    usage = "Switch between Links and Snippets tabs. Tap any snippet card to copy plain code directly to your clipboard with haptic confirmation."
                ),
                FeatureGuideData(
                    title = "Dev Secret Vault",
                    overview = "Biometric-protected local vault for storing API keys, environment variables, and configuration links.",
                    usage = "Access the Vault tab, complete biometric authentication, and store credentials. Tap Copy Key for a 30-second temporary clipboard copy, or tap Export Vault to download a .env configuration file."
                ),
                FeatureGuideData(
                    title = "Profile and AI Exporter",
                    overview = "Personal developer card generator and multi-format portfolio export builder.",
                    usage = "Edit your personal bio, developer title, and social links. Use the Export button to generate raw Markdown summaries or 5-step AI web builder prompts."
                )
            )

            features.forEach { feature ->
                ExpandableFeatureCard(
                    feature = feature,
                    cardBg = cardBg,
                    borderColor = borderColor,
                    textColor = textColor,
                    subTextColor = subTextColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class FeatureGuideData(
    val title: String,
    val overview: String,
    val usage: String
)

@Composable
fun ExpandableFeatureCard(
    feature: FeatureGuideData,
    cardBg: Color,
    borderColor: Color,
    textColor: Color,
    subTextColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = subTextColor
                )
            }

            Text(
                text = feature.overview,
                style = MaterialTheme.typography.bodyMedium,
                color = subTextColor
            )

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HorizontalDivider(color = borderColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "How to Use",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = feature.usage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = subTextColor
                    )
                }
            }
        }
    }
}
