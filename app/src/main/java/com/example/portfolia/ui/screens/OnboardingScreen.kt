package com.example.portfolia.ui.screens

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.portfolia.PortfoliaApp
import com.example.portfolia.data.SettingsDataStore
import com.example.portfolia.data.UserProfileEntity
import com.example.portfolia.ui.theme.ThemeAccent
import com.example.portfolia.ui.components.PresetAvatarImage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as PortfoliaApp).database
    private val profileDao = db.profileDao()
    private val settingsDataStore = SettingsDataStore(application)

    var name by mutableStateOf("")
    var title by mutableStateOf("")
    var bio by mutableStateOf("")
    var email by mutableStateOf("")
    var githubUrl by mutableStateOf("")
    var linkedinUrl by mutableStateOf("")
    var experienceYears by mutableStateOf("")
    var selectedAvatar by mutableStateOf<String?>("preset:💻")

    init {
        viewModelScope.launch {
            val existing = profileDao.getUserProfile().first()
            if (existing != null) {
                name = existing.name
                title = existing.title
                bio = existing.bio
                email = existing.email
                githubUrl = existing.githubUrl
                linkedinUrl = existing.linkedinUrl
                experienceYears = existing.experienceYears.toString()
                selectedAvatar = existing.avatarUri ?: "preset:💻"
            }
        }
    }

    fun saveProfile(onComplete: () -> Unit) {
        val exp = experienceYears.toIntOrNull() ?: 0
        val profile = UserProfileEntity(
            id = 1,
            name = name,
            title = title,
            bio = bio,
            email = email,
            githubUrl = githubUrl,
            linkedinUrl = linkedinUrl,
            experienceYears = exp,
            avatarUri = selectedAvatar
        )
        viewModelScope.launch {
            profileDao.saveUserProfile(profile)
            settingsDataStore.setOnboardingCompleted(true)
            onComplete()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    accent: ThemeAccent,
    isEditMode: Boolean = false,
    onBack: (() -> Unit)? = null,
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)
            } catch (e: Exception) {}
            viewModel.selectedAvatar = uri.toString()
        }
    }

    val presets = listOf("preset:architect", "preset:security", "preset:creator", "preset:mobile", "preset:systems")

    val isDark = MaterialTheme.colorScheme.background == Color(0xFF141415)
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Profile Details" else "PORTFOLIA_V1 // INITIAL SETUP",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    if (isEditMode && onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = textColor
                            )
                        }
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isEditMode) {
                Text(
                    text = "Welcome to Portfolia. Let's configure your digital credentials to establish your professional profile.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = subTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Sleek Avatar Selector Group
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Profile Avatar",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF))
                        .border(1.dp, if (isDark) Color(0xFF2A2A2D) else Color(0xFFE5E5EA), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f))
                            .border(1.dp, if (isDark) Color(0xFF2A2A2D) else Color(0xFFE5E5EA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val avatar = viewModel.selectedAvatar
                        if (avatar != null && avatar.startsWith("preset:")) {
                            PresetAvatarImage(presetId = avatar, modifier = Modifier.fillMaxSize())
                        } else if (avatar != null) {
                            AsyncImage(
                                model = avatar,
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(text = "👤", style = MaterialTheme.typography.headlineLarge)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            presets.forEach { preset ->
                                val isSelected = viewModel.selectedAvatar == preset
                                val ringColor = if (isDark) Color.White else Color(0xFF1C1C1E)
                                val scale by animateFloatAsState(
                                    targetValue = if (isSelected) 1.1f else 1.0f,
                                    label = "PresetScaleAnimation"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                        }
                                        .clip(CircleShape)
                                        .border(
                                            width = 1.5.dp,
                                            color = if (isSelected) ringColor else if (isDark) Color(0xFF2A2A2D) else Color(0xFFE5E5EA),
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.selectedAvatar = preset }
                                ) {
                                    PresetAvatarImage(presetId = preset, modifier = Modifier.fillMaxSize(), iconSize = 20.dp)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f),
                                contentColor = textColor
                            ),
                            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp), tint = textColor)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Custom Image", style = MaterialTheme.typography.labelSmall, color = textColor)
                        }
                    }
                }
            }

            // Sleek Identity Fields Group (Monee specifications)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Professional Identity",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                val inputColors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF),
                    unfocusedContainerColor = if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF),
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedLabelColor = textColor,
                    unfocusedLabelColor = subTextColor,
                    focusedBorderColor = textColor,
                    unfocusedBorderColor = if (isDark) Color(0xFF2A2A2D) else Color(0xFFE5E5EA)
                )

                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = inputColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.title,
                    onValueChange = { viewModel.title = it },
                    label = { Text("Designation / Professional Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = inputColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.experienceYears,
                    onValueChange = { viewModel.experienceYears = it.filter { c -> c.isDigit() } },
                    label = { Text("Years of Experience") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = inputColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.bio,
                    onValueChange = { viewModel.bio = it },
                    label = { Text("Bio / Professional Summary") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    minLines = 3,
                    colors = inputColors
                )
            }

            // Sleek Social Fields Group
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Contact & Social Channels",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                val inputColors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF),
                    unfocusedContainerColor = if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF),
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedLabelColor = textColor,
                    unfocusedLabelColor = subTextColor,
                    focusedBorderColor = textColor,
                    unfocusedBorderColor = if (isDark) Color(0xFF2A2A2D) else Color(0xFFE5E5EA)
                )

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = inputColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.githubUrl,
                    onValueChange = { viewModel.githubUrl = it },
                    label = { Text("GitHub Profile URL") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = inputColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.linkedinUrl,
                    onValueChange = { viewModel.linkedinUrl = it },
                    label = { Text("LinkedIn Profile URL") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = inputColors,
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action save button (Monee stark CTA button style: White background / black text)
            Button(
                onClick = {
                    if (viewModel.name.isNotBlank() && viewModel.title.isNotBlank()) {
                        viewModel.saveProfile(onComplete)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color.White else Color.Black,
                    contentColor = if (isDark) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                enabled = viewModel.name.isNotBlank() && viewModel.title.isNotBlank()
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = if (isDark) Color.Black else Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditMode) "Save Profile Details" else "Complete Wizard Setup",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
