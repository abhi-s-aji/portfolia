package com.example.portfolia.ui.screens

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.portfolia.PortfoliaApp
import com.example.portfolia.data.ProjectEntity
import com.example.portfolia.data.ReferenceEntity
import com.example.portfolia.data.UserProfileEntity
import com.example.portfolia.ui.components.AppleGlassCard
import com.example.portfolia.ui.theme.GlassIntensity
import com.example.portfolia.ui.theme.LayoutDensity
import com.example.portfolia.ui.theme.ThemeAccent
import com.example.portfolia.util.AiPromptExporter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as PortfoliaApp).database
    val projects: StateFlow<List<ProjectEntity>> = db.projectDao().getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = db.profileDao().getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val references: StateFlow<List<ReferenceEntity>> = db.referenceDao().getAllReferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch { db.projectDao().deleteProject(project) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isGlassmorphism: Boolean,
    accent: ThemeAccent,
    intensity: GlassIntensity,
    density: LayoutDensity,
    onAddProjectClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val projects by viewModel.projects.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val references by viewModel.references.collectAsState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val filteredProjects = remember(projects, searchQuery) {
        if (searchQuery.isBlank()) projects
        else projects.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.techStack.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Welcome back,",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = profile?.name ?: "Developer",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = {
                        val prompt = AiPromptExporter.generateMasterPrompt(profile, projects, references)
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("AI Web Portfolio Prompt", prompt)
                        clipboard.setPrimaryClip(clip)
                        android.widget.Toast.makeText(context, "AI Website Prompt copied to clipboard!", android.widget.Toast.LENGTH_LONG).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Export AI Prompt",
                            tint = accent.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProjectClick,
                containerColor = accent.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = density.paddingDp.dp) // Dynamic Density Padding
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search projects or tech stack...", color = Color.White.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.6f)) },
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = accent.primary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.02f)
                )
            )

            Spacer(modifier = Modifier.height(density.paddingDp.dp)) // Dynamic Density Spacing

            AnimatedVisibility(
                visible = filteredProjects.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF8E8E93)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No projects found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF8E8E93)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(density.paddingDp.dp) // Dynamic Density Spacing
            ) {
                items(filteredProjects, key = { it.id }) { project ->
                    AppleGlassCard(
                        isGlassmorphism = isGlassmorphism,
                        intensity = intensity, // Dynamic Glass Intensity
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = project.title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = project.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = project.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.70f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tech: ${project.techStack}",
                            style = MaterialTheme.typography.labelMedium,
                            color = accent.primary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (project.githubUrl.isNotBlank()) {
                                Button(
                                    onClick = {
                                        val validUrl = if (!project.githubUrl.startsWith("http://") && !project.githubUrl.startsWith("https://")) {
                                            "https://${project.githubUrl}"
                                        } else project.githubUrl
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.15f),
                                        contentColor = Color.White
                                    ),
                                    shape = CircleShape
                                ) {
                                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Repository")
                                }
                            }
                            IconButton(onClick = { viewModel.deleteProject(project) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFFF453A)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
