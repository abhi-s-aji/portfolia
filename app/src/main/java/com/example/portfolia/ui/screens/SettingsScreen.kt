package com.example.portfolia.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.portfolia.PortfoliaApp
import com.example.portfolia.data.ProjectEntity
import com.example.portfolia.data.ReferenceEntity
import com.example.portfolia.data.SettingsDataStore
import com.example.portfolia.data.UserProfileEntity
import com.example.portfolia.ui.components.AppleGlassCard
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsDataStore: SettingsDataStore,
    onEditProfileClick: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var showResetDialog by remember { mutableStateOf(false) }

    // JSON export launcher
    val jsonExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val db = (context.applicationContext as PortfoliaApp).database
                    val profile = db.profileDao().getUserProfile().first()
                    val projects = db.projectDao().getAllProjects().first()
                    val references = db.referenceDao().getAllReferences().first()

                    val backupMap = mapOf(
                        "profile" to profile,
                        "projects" to projects,
                        "references" to references
                    )
                    val jsonStr = Gson().toJson(backupMap)

                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        os.write(jsonStr.toByteArray())
                    }
                    scope.launch(Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "Backup JSON exported successfully!", android.widget.Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    scope.launch(Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "Export failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    // JSON import launcher
    val jsonImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                try {
                    val jsonStr = context.contentResolver.openInputStream(uri)?.use { isStream ->
                        isStream.bufferedReader().readText()
                    }
                    if (jsonStr != null) {
                        val parser = com.google.gson.JsonParser.parseString(jsonStr).asJsonObject
                        
                        // Parse values
                        val profileObj = parser.getAsJsonObject("profile")
                        val profileEntity = if (profileObj != null) {
                            Gson().fromJson(profileObj, UserProfileEntity::class.java)
                        } else null

                        val projectsArr = parser.getAsJsonArray("projects")
                        val projectsList = if (projectsArr != null) {
                            val type = object : com.google.gson.reflect.TypeToken<List<ProjectEntity>>() {}.type
                            Gson().fromJson<List<ProjectEntity>>(projectsArr, type)
                        } else emptyList()

                        val referencesArr = parser.getAsJsonArray("references")
                        val referencesList = if (referencesArr != null) {
                            val type = object : com.google.gson.reflect.TypeToken<List<ReferenceEntity>>() {}.type
                            Gson().fromJson<List<ReferenceEntity>>(referencesArr, type)
                        } else emptyList()

                        val db = (context.applicationContext as PortfoliaApp).database
                        db.clearAllTables()
                        if (profileEntity != null) {
                            db.profileDao().saveUserProfile(profileEntity)
                        }
                        projectsList.forEach { db.projectDao().insertProject(it.copy(id = 0)) }
                        referencesList.forEach { db.referenceDao().insertReference(it.copy(id = 0)) }
                        settingsDataStore.setOnboardingCompleted(true)

                        scope.launch(Dispatchers.Main) {
                            android.widget.Toast.makeText(context, "Backup JSON imported successfully!", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    scope.launch(Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "Import failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. PROFILE MANAGEMENT CARD
            AppleGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Profile Configuration", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Modify credentials, bio contacts, or profile avatar.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF989A9C))
                    
                    Button(
                        onClick = onEditProfileClick,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Profile Details", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 2. DATA & STORAGE MANAGEMENT CARD
            AppleGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Data & Storage", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Export or restore your profile configurations, links, and projects list.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF989A9C))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                jsonExportLauncher.launch("portfolia_backup.json")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Backup", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }

                        Button(
                            onClick = {
                                jsonImportLauncher.launch(arrayOf("application/json"))
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import Backup", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Button(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x20FF453A)),
                        border = BorderStroke(1.dp, Color(0xFFFF453A).copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFF453A))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset App Data", color = Color(0xFFFF453A), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 3. DEVELOPER CREDITS CARD
            AppleGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "App Developer Contact",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    Text(
                        text = "Get in touch, report issues, or view social profiles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8E8E93)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val actions = listOf(
                            "GitHub" to "https://github.com/abhi-s-aji",
                            "LinkedIn" to "https://www.linkedin.com/in/abhi-s-aji-eden",
                            "Email" to "mailto:abhisaji.dev@gmail.com"
                        )
                        actions.forEach { (label, link) ->
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {}
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF252528)),
                                border = BorderStroke(1.dp, Color(0xFF2A2A2D)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Text(label, color = Color.White, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }

    // Reset confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = Color(0xFF1E1E20),
            tonalElevation = 0.dp,
            title = { Text("Reset Application Data?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "This will permanently delete all your profiles, projects, reference links, and reset app customization preferences. This action cannot be undone.",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val db = (context.applicationContext as PortfoliaApp).database
                            db.clearAllTables()
                            settingsDataStore.clearAll()
                            scope.launch(Dispatchers.Main) {
                                showResetDialog = false
                                android.widget.Toast.makeText(context, "Application successfully reset!", android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF453A))
                ) {
                    Text("Yes, Reset Data", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }
}
