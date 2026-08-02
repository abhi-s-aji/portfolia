package com.example.portfolia.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.portfolia.PortfoliaApp
import com.example.portfolia.data.ProjectEntity
import kotlinx.coroutines.launch

class FormViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as PortfoliaApp).database.projectDao()

    fun saveProject(title: String, desc: String, category: String, tech: String, github: String, onSaved: () -> Unit) {
        viewModelScope.launch {
            dao.insertProject(
                ProjectEntity(
                    title = title,
                    description = desc,
                    category = if (category.isBlank()) "General" else category,
                    techStack = tech,
                    githubUrl = github
                )
            )
            onSaved()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectFormScreen(
    onBackClick: () -> Unit,
    onProjectSaved: () -> Unit,
    viewModel: FormViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var techStack by remember { mutableStateOf("") }
    var githubUrl by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("New Project", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Project Title") },
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
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
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
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (e.g. Android, Web)") },
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
                value = techStack,
                onValueChange = { techStack = it },
                label = { Text("Tech Stack (e.g. Kotlin, Compose)") },
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
                value = githubUrl,
                onValueChange = { githubUrl = it },
                label = { Text("GitHub Repository Link") },
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
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.saveProject(title, description, category, techStack, githubUrl, onProjectSaved)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFA2D55))
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Project")
            }
        }
    }
}
