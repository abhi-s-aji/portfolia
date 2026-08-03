package com.example.portfolia.ui.screens

import android.app.Application
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.portfolia.PortfoliaApp
import com.example.portfolia.data.ProjectEntity
import com.example.portfolia.ui.theme.ThemeAccent
import kotlinx.coroutines.launch

class FormViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as PortfoliaApp).database

    fun saveProject(
        title: String,
        desc: String,
        category: String,
        tech: List<String>,
        github: String,
        demo: String,
        linkedin: String,
        onSaved: () -> Unit
    ) {
        val proj = ProjectEntity(
            title = title,
            description = desc,
            category = category,
            techStack = tech,
            githubUrl = github,
            demoUrl = demo,
            linkedinPostUrl = linkedin,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            db.projectDao().insertProject(proj)
            onSaved()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProjectFormScreen(
    accent: ThemeAccent,
    onBackClick: () -> Unit,
    onProjectSaved: () -> Unit,
    viewModel: FormViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val view = LocalView.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var techInput by remember { mutableStateOf("") }
    val techList = remember { mutableStateListOf<String>() }
    var githubUrl by remember { mutableStateOf("") }
    var demoUrl by remember { mutableStateOf("") }
    var linkedinPostUrl by remember { mutableStateOf("") }

    val isDark = MaterialTheme.colorScheme.background == Color(0xFF141415)
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant

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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Add Project", color = textColor, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Project Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = inputColors,
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = inputColors
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (e.g. Android, Web, Systems)") },
                modifier = Modifier.fillMaxWidth(),
                colors = inputColors,
                singleLine = true
            )

            // Dynamic Tag-Based Tech Stack Input
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = techInput,
                    onValueChange = { input ->
                        if (input.endsWith(",") || input.endsWith("\n")) {
                            val tag = input.dropLast(1).trim()
                            if (tag.isNotEmpty() && !techList.contains(tag)) {
                                techList.add(tag)
                                view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                            }
                            techInput = ""
                        } else {
                            techInput = input
                        }
                    },
                    label = { Text("Add Tech Tag (Comma or Enter)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        val tag = techInput.trim()
                        if (tag.isNotEmpty() && !techList.contains(tag)) {
                            techList.add(tag)
                            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                        }
                        techInput = ""
                    }),
                    colors = inputColors,
                    singleLine = true
                )

                // Chips container
                if (techList.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        techList.forEach { tag ->
                            AnimatedTagChip(
                                tag = tag,
                                onRemove = {
                                    techList.remove(tag)
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = githubUrl,
                onValueChange = { githubUrl = it },
                label = { Text("GitHub Repository Link") },
                modifier = Modifier.fillMaxWidth(),
                colors = inputColors,
                singleLine = true
            )

            OutlinedTextField(
                value = demoUrl,
                onValueChange = { demoUrl = it },
                label = { Text("Live Demo Link") },
                modifier = Modifier.fillMaxWidth(),
                colors = inputColors,
                singleLine = true
            )

            OutlinedTextField(
                value = linkedinPostUrl,
                onValueChange = { linkedinPostUrl = it },
                label = { Text("LinkedIn Showcase Post Link") },
                modifier = Modifier.fillMaxWidth(),
                colors = inputColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stark CTA style: White background, Black text
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.saveProject(
                            title = title,
                            desc = description,
                            category = category,
                            tech = techList.toList(),
                            github = githubUrl,
                            demo = demoUrl,
                            linkedin = linkedinPostUrl,
                            onSaved = onProjectSaved
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color.White else Color.Black,
                    contentColor = if (isDark) Color.Black else Color.White
                ),
                enabled = title.isNotBlank()
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = if (isDark) Color.Black else Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Project", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AnimatedTagChip(
    tag: String,
    onRemove: () -> Unit
) {
    val view = LocalView.current
    var isVisible by remember { mutableStateOf(true) }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        finishedListener = { if (!isVisible) onRemove() }
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(150)
    )

    val isDark = MaterialTheme.colorScheme.background == Color(0xFF141415)
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
            .border(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .clickable {
                view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                isVisible = false
            }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = tag, color = textColor, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove Tag",
                tint = textColor.copy(alpha = 0.5f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
