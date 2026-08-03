package com.example.portfolia.ui.screens

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.portfolia.PortfoliaApp
import com.example.portfolia.data.ReferenceEntity
import com.example.portfolia.ui.components.AppleGlassCard
import com.example.portfolia.ui.theme.LayoutDensity
import com.example.portfolia.ui.theme.ThemeAccent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReferenceViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as PortfoliaApp).database.referenceDao()

    val references: StateFlow<List<ReferenceEntity>> = dao.getAllReferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReference(title: String, url: String, category: String, groupName: String, notes: String) {
        viewModelScope.launch {
            dao.insertReference(
                ReferenceEntity(
                    title = title,
                    url = url,
                    category = category,
                    groupName = groupName,
                    notes = notes
                )
            )
        }
    }

    fun deleteReference(reference: ReferenceEntity) {
        viewModelScope.launch { dao.deleteReference(reference) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferencesScreen(
    accent: ThemeAccent,
    viewModel: ReferenceViewModel = viewModel()
) {
    val references by viewModel.references.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("All") }

    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    // Group selection states
    var selectedGroupDropdown by remember { mutableStateOf("General") }
    var customGroupName by remember { mutableStateOf("") }
    var isCreatingCustomGroup by remember { mutableStateOf(false) }

    // Dynamic folders list gathered from DB + defaults
    val dynamicFolders = remember(references) {
        val defaultList = listOf("General", "Docs", "Repos", "Tools", "Ideas")
        val dbGroups = references.map { it.groupName }.distinct()
        (defaultList + dbGroups).distinct().filter { it.isNotBlank() }
    }

    val tabs = remember(references) {
        listOf("All") + references.map { it.groupName }.distinct().filter { it != "All" && it.isNotBlank() }
    }

    val filteredReferences = remember(references, selectedTab) {
        if (selectedTab == "All") references
        else references.filter { it.groupName == selectedTab }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Saved Links & References",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = accent.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Link")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 14.dp)
        ) {
            // Tab Filter Bar (Horizontal Scrollable Chips Row)
            if (tabs.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        val bg by animateColorAsState(targetValue = if (isSelected) accent.primary else Color.White.copy(alpha = 0.05f))
                        val textCol by animateColorAsState(targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(bg)
                                .clickable {
                                    view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                                    selectedTab = tab
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = tab,
                                color = textCol,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (filteredReferences.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF8E8E93)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No links saved in this folder",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF8E8E93)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredReferences, key = { it.id }) { ref ->
                        AppleGlassCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ref.title,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.08f)
                                    ) {
                                        Text(
                                            text = ref.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Surface(
                                        shape = CircleShape,
                                        color = accent.primary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = ref.groupName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = accent.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            if (ref.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = ref.notes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.70f)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        if (ref.url.isNotBlank()) {
                                            val validUrl = if (!ref.url.startsWith("http://") && !ref.url.startsWith("https://")) {
                                                "https://${ref.url}"
                                            } else ref.url
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
                                            context.startActivity(intent)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White.copy(alpha = 0.15f),
                                        contentColor = Color.White
                                    ),
                                    shape = CircleShape
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Link")
                                }
                                IconButton(onClick = {
                                    view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.deleteReference(ref)
                                }) {
                                    Icon(
                                        Icons.Default.DeleteOutline,
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

        if (showAddDialog) {
            var dropdownExpanded by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = Color(0xFF1E1E20),
                tonalElevation = 0.dp,
                title = { Text("Save Reference Link", color = Color.White) },
                text = {
                    val inputColors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E1E20),
                        unfocusedContainerColor = Color(0xFF1E1E20),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color(0xFF8E8E93),
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color(0xFF2A2A2D)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            colors = inputColors,
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = url,
                            onValueChange = { url = it },
                            label = { Text("URL (e.g. github.com)") },
                            colors = inputColors,
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Sub-Category (e.g., Docs, Repo)") },
                            colors = inputColors,
                            singleLine = true
                        )

                        // Folder Group selection dropdown
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Folder / Group Selection",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E1E20))
                                    .border(1.dp, Color(0xFF2A2A2D), RoundedCornerShape(8.dp))
                                    .clickable { dropdownExpanded = !dropdownExpanded }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isCreatingCustomGroup) "Custom Group" else selectedGroupDropdown,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = if (dropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier
                                    .background(Color(0xFF1E1E20))
                                    .border(1.dp, Color(0xFF2A2A2D), RoundedCornerShape(4.dp))
                            ) {
                                dynamicFolders.forEach { folder ->
                                    DropdownMenuItem(
                                        text = { Text(folder, color = Color.White) },
                                        onClick = {
                                            selectedGroupDropdown = folder
                                            isCreatingCustomGroup = false
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("+ Create New Folder...", color = Color.White) },
                                    onClick = {
                                        isCreatingCustomGroup = true
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }

                        if (isCreatingCustomGroup) {
                            OutlinedTextField(
                                value = customGroupName,
                                onValueChange = { customGroupName = it },
                                label = { Text("New Folder Name") },
                                colors = inputColors,
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes / Description") },
                            colors = inputColors
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank() && url.isNotBlank()) {
                                val folderName = if (isCreatingCustomGroup) {
                                    customGroupName.ifBlank { "General" }
                                } else {
                                    selectedGroupDropdown
                                }
                                viewModel.addReference(
                                    title = title,
                                    url = url,
                                    category = if (category.isBlank()) "General" else category,
                                    groupName = folderName,
                                    notes = notes
                                )
                                title = ""; url = ""; category = ""; notes = ""; customGroupName = ""
                                isCreatingCustomGroup = false
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = Color.White.copy(alpha = 0.7f)) }
                }
            )
        }
    }
}
