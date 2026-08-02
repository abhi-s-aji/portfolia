package com.example.portfolia.ui.screens.settings

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.portfolia.data.local.AppDatabase
import com.example.portfolia.data.repository.ProfileRepository
import com.example.portfolia.data.repository.ProjectRepository
import com.example.portfolia.ui.theme.CardShape
import com.example.portfolia.ui.theme.Crimson
import com.example.portfolia.ui.theme.Gold
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    application: Application,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showClearDialog by remember { mutableStateOf(false) }
    var clearConfirmStep by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Appearance Section
            SectionHeader("Appearance")

            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Dark Theme",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                if (isDarkTheme) "Currently using dark mode"
                                else "Currently using light mode",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onToggleTheme,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = Gold,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Data Management Section
            SectionHeader("Data Management")

            SettingsCard {
                SettingsActionItem(
                    icon = Icons.Default.Upload,
                    title = "Export Data",
                    subtitle = "Save all projects and profile as JSON",
                    onClick = {
                        scope.launch {
                            exportData(application, context)
                        }
                    }
                )
            }

            SettingsCard {
                SettingsActionItem(
                    icon = Icons.Default.Download,
                    title = "Import Data",
                    subtitle = "Restore from a previously exported JSON file",
                    onClick = {
                        scope.launch {
                            importData(application, context)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Danger Zone
            SectionHeader("Danger Zone")

            Surface(
                shape = CardShape,
                color = Crimson.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, Crimson.copy(alpha = 0.3f))
            ) {
                SettingsActionItem(
                    icon = Icons.Default.DeleteForever,
                    title = "Clear All Data",
                    subtitle = "Permanently delete all projects and profile data",
                    iconTint = Crimson,
                    titleColor = Crimson,
                    onClick = { showClearDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App info
            Text(
                text = "Portfolia v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Clear Data Dialog - Double confirmation
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = {
                showClearDialog = false
                clearConfirmStep = 0
            },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Crimson,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    if (clearConfirmStep == 0) "Clear All Data?"
                    else "Are you absolutely sure?",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    if (clearConfirmStep == 0)
                        "This will permanently delete all your projects and profile data. This action cannot be undone."
                    else
                        "This is your final confirmation. All projects, profile data, and settings will be erased permanently.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (clearConfirmStep == 0) {
                            clearConfirmStep = 1
                        } else {
                            scope.launch {
                                clearAllData(application, context)
                                showClearDialog = false
                                clearConfirmStep = 0
                            }
                        }
                    }
                ) {
                    Text(
                        if (clearConfirmStep == 0) "Continue" else "Delete Everything",
                        color = Crimson,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        clearConfirmStep = 0
                    }
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = Gold,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        shape = CardShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.animateContentSize()
    ) {
        content()
    }
}

@Composable
private fun SettingsActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: androidx.compose.ui.graphics.Color = Gold,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent,
        shape = CardShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    color = titleColor
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private suspend fun exportData(application: Application, context: Context) {
    withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getInstance(application)
            val projectRepo = ProjectRepository(db.projectDao())
            val profileRepo = ProfileRepository(db.profileDao())

            val projects = projectRepo.getAllProjectsSnapshot()
            val profile = profileRepo.getProfileSnapshot()

            val exportData = mapOf(
                "projects" to projects,
                "profile" to profile,
                "exportedAt" to System.currentTimeMillis()
            )

            val gson = GsonBuilder().setPrettyPrinting().create()
            val json = gson.toJson(exportData)

            val exportDir = File(context.getExternalFilesDir(null), "exports")
            exportDir.mkdirs()
            val file = File(exportDir, "portfolia_backup_${System.currentTimeMillis()}.json")
            file.writeText(json)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Data exported to: ${file.name}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Export failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

private suspend fun importData(application: Application, context: Context) {
    withContext(Dispatchers.IO) {
        try {
            val exportDir = File(context.getExternalFilesDir(null), "exports")
            val files = exportDir.listFiles()?.filter { it.extension == "json" }
                ?.sortedByDescending { it.lastModified() }

            if (files.isNullOrEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "No backup files found in exports folder",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@withContext
            }

            val latestFile = files.first()
            val json = latestFile.readText()
            val gson = Gson()

            val importMap = gson.fromJson(json, Map::class.java)
            val db = AppDatabase.getInstance(application)

            // Import projects
            val projectsJson = gson.toJson(importMap["projects"])
            val projectType = object : com.google.gson.reflect.TypeToken<List<com.example.portfolia.data.model.ProjectEntity>>() {}.type
            val projects: List<com.example.portfolia.data.model.ProjectEntity> = gson.fromJson(projectsJson, projectType)

            projects.forEach { project ->
                db.projectDao().insertProject(project)
            }

            // Import profile
            val profileJson = gson.toJson(importMap["profile"])
            if (profileJson != "null") {
                val profile = gson.fromJson(profileJson, com.example.portfolia.data.model.UserProfileEntity::class.java)
                db.profileDao().insertOrUpdateProfile(profile)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Imported ${projects.size} projects from ${latestFile.name}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Import failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

private suspend fun clearAllData(application: Application, context: Context) {
    withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getInstance(application)
            db.projectDao().deleteAllProjects()
            db.profileDao().deleteProfile()

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "All data has been cleared",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Clear failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
