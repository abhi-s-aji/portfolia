package com.example.portfolia.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.portfolia.PortfoliaApp
import com.example.portfolia.data.UserProfileEntity
import com.example.portfolia.ui.components.AppleGlassCard
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as PortfoliaApp).database.profileDao()

    val userProfile: StateFlow<UserProfileEntity?> = dao.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateProfile(name: String, title: String, bio: String, email: String, github: String, linkedin: String) {
        viewModelScope.launch {
            dao.saveUserProfile(
                UserProfileEntity(
                    id = 1,
                    name = name,
                    title = title,
                    bio = bio,
                    email = email,
                    githubUrl = github,
                    linkedinUrl = linkedin
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    isGlassmorphism: Boolean,
    viewModel: ProfileViewModel = viewModel()
) {
    val profile by viewModel.userProfile.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    var name by remember(profile) { mutableStateOf(profile?.name ?: "") }
    var title by remember(profile) { mutableStateOf(profile?.title ?: "") }
    var bio by remember(profile) { mutableStateOf(profile?.bio ?: "") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "") }
    var github by remember(profile) { mutableStateOf(profile?.githubUrl ?: "") }
    var linkedin by remember(profile) { mutableStateOf(profile?.linkedinUrl ?: "") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Developer Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = {
                        if (isEditing) {
                            viewModel.updateProfile(name, title, bio, email, github, linkedin)
                        }
                        isEditing = !isEditing
                    }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Save Profile" else "Edit Profile",
                            tint = Color(0xFFFA2D55)
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
                    color = Color(0xFFFA2D55)
                )

                AppleGlassCard(isGlassmorphism = isGlassmorphism, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "About", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = profile?.bio ?: "No bio provided.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }

                AppleGlassCard(isGlassmorphism = isGlassmorphism, modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Contact & Links", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Text(text = "Email: ${profile?.email ?: ""}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.75f))
                        Text(text = "GitHub: ${profile?.githubUrl ?: ""}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.75f))
                        Text(text = "LinkedIn: ${profile?.linkedinUrl ?: ""}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.75f))
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
                        focusedBorderColor = Color(0xFFFA2D55),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFFA2D55),
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
                        focusedBorderColor = Color(0xFFFA2D55),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFFA2D55),
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
                        focusedBorderColor = Color(0xFFFA2D55),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFFA2D55),
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
                        focusedBorderColor = Color(0xFFFA2D55),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFFA2D55),
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
                        focusedBorderColor = Color(0xFFFA2D55),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFFA2D55),
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
                        focusedBorderColor = Color(0xFFFA2D55),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFFA2D55),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                Button(
                    onClick = {
                        viewModel.updateProfile(name, title, bio, email, github, linkedin)
                        isEditing = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFA2D55))
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Changes")
                }
            }
        }
    }
}
