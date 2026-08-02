package com.example.portfolia.ui.screens

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
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
import com.example.portfolia.ui.theme.ThemeAccent
import com.example.portfolia.util.AiPromptExporter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as PortfoliaApp).database
    private val dao = db.profileDao()

    val userProfile: StateFlow<UserProfileEntity?> = dao.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val projects: StateFlow<List<ProjectEntity>> = db.projectDao().getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val references: StateFlow<List<ReferenceEntity>> = db.referenceDao().getAllReferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateProfile(
        name: String,
        title: String,
        bio: String,
        email: String,
        github: String,
        linkedin: String,
        experience: String,
        uptime: String,
        commits: String
    ) {
        viewModelScope.launch {
            dao.saveUserProfile(
                UserProfileEntity(
                    id = 1,
                    name = name,
                    title = title,
                    bio = bio,
                    email = email,
                    githubUrl = github,
                    linkedinUrl = linkedin,
                    experience = experience,
                    uptime = uptime,
                    commits = commits
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    isGlassmorphism: Boolean,
    accent: ThemeAccent,
    intensity: GlassIntensity,
    viewModel: ProfileViewModel = viewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val references by viewModel.references.collectAsState()
    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }

    var name by remember(profile) { mutableStateOf(profile?.name ?: "") }
    var title by remember(profile) { mutableStateOf(profile?.title ?: "") }
    var bio by remember(profile) { mutableStateOf(profile?.bio ?: "") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "") }
    var github by remember(profile) { mutableStateOf(profile?.githubUrl ?: "") }
    var linkedin by remember(profile) { mutableStateOf(profile?.linkedinUrl ?: "") }
    var experience by remember(profile) { mutableStateOf(profile?.experience ?: "8+ YRS") }
    var uptime by remember(profile) { mutableStateOf(profile?.uptime ?: "99.9%") }
    var commits by remember(profile) { mutableStateOf(profile?.commits ?: "12.4k") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Developer Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = {
                        if (isEditing) {
                            viewModel.updateProfile(name, title, bio, email, github, linkedin, experience, uptime, commits)
                        }
                        isEditing = !isEditing
                    }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Save Profile" else "Edit Profile",
                            tint = accent.primary
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
            Surface(
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = Color.White
                    )
                }
            }

            if (!isEditing) {
                Text(
                    text = profile?.name ?: "Your Name",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = profile?.title ?: "Developer Title",
                    style = MaterialTheme.typography.titleMedium,
                    color = accent.primary
                )

                AppleGlassCard(
                    isGlassmorphism = isGlassmorphism,
                    intensity = intensity,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "About", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = profile?.bio ?: "No bio provided.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }

                AppleGlassCard(
                    isGlassmorphism = isGlassmorphism,
                    intensity = intensity,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Contact & Links", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Text(text = "Email: ${profile?.email ?: ""}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.75f))
                        Text(text = "GitHub: ${profile?.githubUrl ?: ""}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.75f))
                        Text(text = "LinkedIn: ${profile?.linkedinUrl ?: ""}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.75f))
                    }
                }

                // Stats Bento Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val stats = listOf(
                        "Experience" to (profile?.experience ?: "8+ YRS"),
                        "Projects" to projects.size.toString(),
                        "Uptime" to (profile?.uptime ?: "99.9%"),
                        "Commits" to (profile?.commits ?: "12.4k")
                    )
                    stats.forEach { (label, value) ->
                        AppleGlassCard(
                            isGlassmorphism = isGlassmorphism,
                            intensity = intensity,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // AI Portfolio Exporter Button
                Button(
                    onClick = {
                        val prompt = AiPromptExporter.generateMasterPrompt(profile, projects, references)
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("AI Web Portfolio Prompt", prompt)
                        clipboard.setPrimaryClip(clip)
                        android.widget.Toast.makeText(context, "AI Website Prompt copied to clipboard!", android.widget.Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF161820)),
                    border = BoxButtonBorder(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Export Portfolio for AI Website Builder", color = accent.primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = accent.primary)
                    }
                }
            } else {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accent.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = accent.primary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Designation") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accent.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = accent.primary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accent.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = accent.primary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accent.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = accent.primary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = github,
                    onValueChange = { github = it },
                    label = { Text("GitHub Profile URL") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accent.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = accent.primary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = linkedin,
                    onValueChange = { linkedin = it },
                    label = { Text("LinkedIn Profile URL") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accent.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = accent.primary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("Experience Stat") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accent.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = accent.primary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = uptime,
                    onValueChange = { uptime = it },
                    label = { Text("Uptime Stat") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accent.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = accent.primary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                OutlinedTextField(
                    value = commits,
                    onValueChange = { commits = it },
                    label = { Text("Commits Stat") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accent.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = accent.primary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                Button(
                    onClick = {
                        viewModel.updateProfile(name, title, bio, email, github, linkedin, experience, uptime, commits)
                        isEditing = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Changes")
                }
            }
        }
    }
}

@Composable
private fun BoxButtonBorder(): BorderStroke {
    return BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
}
