package com.example.portfolia.ui.screens

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.portfolia.PortfoliaApp
import com.example.portfolia.data.ProjectEntity
import com.example.portfolia.data.UserProfileEntity
import com.example.portfolia.ui.components.AppleGlassCard
import com.example.portfolia.ui.theme.ThemeAccent
import com.example.portfolia.util.AiPromptExporter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as PortfoliaApp).database

    val userProfile: StateFlow<UserProfileEntity?> = db.profileDao().getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val projects: StateFlow<List<ProjectEntity>> = db.projectDao().getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    accent: ThemeAccent,
    onEditProfileClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val context = LocalContext.current

    var showQrDialog by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }
    var showAiBuilderDialog by remember { mutableStateOf(false) }

    // File saver launcher for Choice A
    val markdownText = remember(profile, projects) {
        AiPromptExporter.generateMarkdown(profile, projects)
    }
    val fileSaveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/markdown")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    os.write(markdownText.toByteArray())
                }
                android.widget.Toast.makeText(context, "Saved portfolio.md successfully!", android.widget.Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Failed to save file: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Developer Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = onEditProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color(0xFF2A2A2D), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val avatar = profile?.avatarUri
                if (avatar != null && avatar.startsWith("preset:")) {
                    val emoji = avatar.removePrefix("preset:")
                    Text(text = emoji, style = MaterialTheme.typography.displaySmall)
                } else if (avatar != null) {
                    AsyncImage(
                        model = avatar,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = "👤", style = MaterialTheme.typography.displaySmall)
                }
            }

            Text(
                text = profile?.name?.ifBlank { "Unconfigured Profile" } ?: "Your Name",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = profile?.title?.ifBlank { "Configure in settings wizard" } ?: "Developer Title",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF8E8E93),
                fontWeight = FontWeight.SemiBold
            )

            // Bio Card
            AppleGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Bio",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = profile?.bio?.ifBlank { "No bio provided." } ?: "No bio provided.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            // Clean Bento Stats Grid (Exactly 2-Column: Experience & Projects count)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Experience
                AppleGlassCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "EXPERIENCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF8E8E93),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${profile?.experienceYears ?: 0} YRS",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                // Card 2: Projects count
                AppleGlassCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "PROJECTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF8E8E93),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = projects.size.toString(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            // Clean social contact buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (profile?.email?.isNotBlank() == true) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${profile?.email}"))
                                context.startActivity(intent)
                            } catch (e: Exception) {}
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E20)),
                        border = BorderStroke(1.dp, Color(0xFF2A2A2D)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Email", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (profile?.githubUrl?.isNotBlank() == true) {
                    Button(
                        onClick = {
                            try {
                                val validUrl = if (!profile!!.githubUrl.startsWith("http://") && !profile!!.githubUrl.startsWith("https://")) {
                                    "https://${profile!!.githubUrl}"
                                } else profile!!.githubUrl
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {}
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E20)),
                        border = BorderStroke(1.dp, Color(0xFF2A2A2D)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("GitHub", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (profile?.linkedinUrl?.isNotBlank() == true) {
                    Button(
                        onClick = {
                            try {
                                val validUrl = if (!profile!!.linkedinUrl.startsWith("http://") && !profile!!.linkedinUrl.startsWith("https://")) {
                                    "https://${profile!!.linkedinUrl}"
                                } else profile!!.linkedinUrl
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {}
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E20)),
                        border = BorderStroke(1.dp, Color(0xFF2A2A2D)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("LinkedIn", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Actions: Share QR & Export Portfolio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showQrDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E20)),
                    border = BorderStroke(1.dp, Color(0xFF2A2A2D)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share QR", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showExportSheet = true },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Portfolio", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Modal Bottom Sheet for Dual Export Engine (Rebranded, copy-to-clipboard fallbacks)
    if (showExportSheet) {
        ModalBottomSheet(
            onDismissRequest = { showExportSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color(0xFF1E1E20), // Monee surface obsidian
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Export Portfolio",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Choice A: Markdown Document Group
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Choice A: Markdown Portfolio Document",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = "Generate portfolio.md detailing your bio credentials, contact profile URLs, and projects list.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8E8E93)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                showExportSheet = false
                                fileSaveLauncher.launch("portfolio.md")
                            },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save .md File", style = MaterialTheme.typography.labelMedium)
                        }
                        
                        Button(
                            onClick = {
                                showExportSheet = false
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Markdown Portfolio", markdownText)
                                clipboard.setPrimaryClip(clip)
                                android.widget.Toast.makeText(context, "Markdown portfolio copied to clipboard!", android.widget.Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy text", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                // Choice B: AI Web Prompts Builder Group (No Gemini icons, standard line-art terminal icon)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Choice B: 5-Step AI Prompts Builder",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = "Select a color palette vibe and generate 5 modular sequential prompts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8E8E93)
                    )
                    
                    Button(
                        onClick = {
                            showExportSheet = false
                            showAiBuilderDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Build AI Prompts", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal Dialog for Choice B (5-Step AI Builder - incorporates Palette Selector ONLY here)
    if (showAiBuilderDialog) {
        var currentWizardStep by remember { mutableStateOf(0) } // 0 = Palette Selector, 1 to 5 = Prompt Steps
        var selectedPaletteVibe by remember { mutableStateOf<ThemeAccent>(ThemeAccent.OBSIDIAN_DARK) }

        AlertDialog(
            onDismissRequest = { showAiBuilderDialog = false },
            containerColor = Color(0xFF1E1E20),
            tonalElevation = 0.dp,
            title = {
                Text(
                    text = if (currentWizardStep == 0) "Choose Website Color Vibe" else "AI Builder Prompt ${currentWizardStep}/5",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (currentWizardStep == 0) {
                        Text(
                            text = "Select the target palette theme for your portfolio website's prompt:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF8E8E93)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeAccent.values().forEach { palette ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedPaletteVibe == palette) Color.White.copy(alpha = 0.08f) else Color.Transparent)
                                        .clickable { selectedPaletteVibe = palette }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(palette.primary)
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = palette.displayName,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (selectedPaletteVibe == palette) Color.White else Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    } else {
                        // Display prompt step text
                        val promptTitle = when (currentWizardStep) {
                            1 -> "Architecture & Theme Engine"
                            2 -> "Hero & Profile Header"
                            3 -> "Featured Projects Showcase"
                            4 -> "Interactive Tech Stack & Skills"
                            else -> "Layout Assembly & Deployment"
                        }
                        val promptContent = remember(currentWizardStep, selectedPaletteVibe, profile, projects) {
                            when (currentWizardStep) {
                                1 -> AiPromptExporter.generatePrompt1(selectedPaletteVibe)
                                2 -> AiPromptExporter.generatePrompt2(profile)
                                3 -> AiPromptExporter.generatePrompt3(projects)
                                4 -> AiPromptExporter.generatePrompt4(projects)
                                else -> AiPromptExporter.generatePrompt5()
                            }
                        }

                        Text(
                            text = promptTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = promptContent,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = Color.White)
                            )
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("AI Web Prompt Step $currentWizardStep", promptContent)
                                clipboard.setPrimaryClip(clip)
                                android.widget.Toast.makeText(context, "Step $currentWizardStep Prompt copied!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Copy Prompt to Clipboard", color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                if (currentWizardStep == 0) {
                    Button(
                        onClick = { currentWizardStep = 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                    ) {
                        Text("Generate Prompts", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (currentWizardStep > 1) {
                            TextButton(
                                onClick = { currentWizardStep-- }
                            ) {
                                Text("Previous", color = Color.White)
                            }
                        }
                        Button(
                            onClick = {
                                if (currentWizardStep == 5) {
                                    showAiBuilderDialog = false
                                } else {
                                    currentWizardStep++
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                        ) {
                            Text(
                                text = if (currentWizardStep == 5) "Finish" else "Next",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            dismissButton = {
                if (currentWizardStep == 0) {
                    TextButton(onClick = { showAiBuilderDialog = false }) {
                        Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        )
    }

    // Modal Dialog for Share QR Code (Fixed selectedPlatform state to default to GitHub)
    if (showQrDialog) {
        var selectedPlatform by remember { mutableStateOf("GitHub") }
        val qrText = if (selectedPlatform == "LinkedIn") {
            profile?.linkedinUrl ?: ""
        } else {
            profile?.githubUrl ?: ""
        }

        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            containerColor = Color(0xFF1E1E20),
            tonalElevation = 0.dp,
            title = {
                Text(
                    text = "Share Digital Business Card",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val sources = listOf("GitHub", "LinkedIn")
                        sources.forEach { src ->
                            val isSelected = selectedPlatform == src
                            val isEnabled = if (src == "LinkedIn") profile?.linkedinUrl?.isNotBlank() == true else profile?.githubUrl?.isNotBlank() == true

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable(enabled = isEnabled) { selectedPlatform = src }
                            ) {
                                Text(
                                    text = src,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) Color.White else if (isEnabled) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.2f),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    if (qrText.isNotBlank()) {
                        val qrBitmap = remember(qrText) { generateQrCode(qrText) }
                        if (qrBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "QR Code",
                                    modifier = Modifier.size(200.dp)
                                )
                            }
                        }
                        Text(
                            text = qrText,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8E8E93),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "No profile URL configured for $selectedPlatform.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF8E8E93),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showQrDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

private fun generateQrCode(text: String, size: Int = 512): Bitmap? {
    return try {
        val writer = com.google.zxing.qrcode.QRCodeWriter()
        val bitMatrix = writer.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(
                    x, y,
                    if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        bmp
    } catch (e: Exception) {
        null
    }
}
