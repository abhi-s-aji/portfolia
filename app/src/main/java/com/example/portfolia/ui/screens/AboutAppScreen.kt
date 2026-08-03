package com.example.portfolia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
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
                title = { Text("About Portfolia", fontWeight = FontWeight.Bold, color = textColor) },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // App Identity Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Portfolia",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = "v1.2.0 (Build 6)",
                        style = MaterialTheme.typography.bodySmall,
                        color = subTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Offline-First Developer Portfolio Workspace",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // System Architecture Specs Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "System Architecture Specs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    HorizontalDivider(color = borderColor)

                    SpecRow(label = "UI Architecture", value = "Jetpack Compose with Material 3 Design", textColor = textColor, subTextColor = subTextColor)
                    SpecRow(label = "Local Database", value = "Encrypted Room Database (Schema v6)", textColor = textColor, subTextColor = subTextColor)
                    SpecRow(label = "Security Engine", value = "Android KeyStore and BiometricPrompt API", textColor = textColor, subTextColor = subTextColor)
                }
            }

            // Developer Identity Card
            // Developer Identity Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Developer Identity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = borderColor)
                    
                    // Developer Name
                    Text(text = "Developer", style = MaterialTheme.typography.labelMedium, color = subTextColor)
                    Text(text = "Abhi S Aji", style = MaterialTheme.typography.bodyLarge, color = textColor)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // GitHub Handle
                    Text(text = "GitHub", style = MaterialTheme.typography.labelMedium, color = subTextColor)
                    Text(text = "abhi-s-aji", style = MaterialTheme.typography.bodyLarge, color = textColor)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Contact Email
                    Text(text = "Contact", style = MaterialTheme.typography.labelMedium, color = subTextColor)
                    Text(text = "abhisaji.dev@gmail.com", style = MaterialTheme.typography.bodyLarge, color = textColor)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SpecRow(
    label: String,
    value: String,
    textColor: Color,
    subTextColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = subTextColor
        )
    }
}
