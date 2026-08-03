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

    val isDark = MaterialTheme.colorScheme.background == Color(0xFF141415)
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Saved Links & References",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = if (isDark) Color.White else Color.Black,
                contentColor = if (isDark) Color.Black else Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add, 
                    contentDescription = "Add Link", 
                    tint = if (isDark) Color.Black else Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                        val bg by animateColorAsState(
                            targetValue = if (isSelected) {
                                if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                            } else {
                                if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF)
                            }
                        )
                        val textCol by animateColorAsState(targetValue = if (isSelected) textColor else subTextColor)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(bg)
                                .border(
                                    1.dp,
                                    if (isSelected) Color.Transparent else if (isDark) Color(0xFF2A2A2D) else Color(0xFFE5E5EA),
                                    RoundedCornerShape(20.dp)
                                )
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
                                    color = textColor
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)
                                ) {
                                    Text(
                                        text = ref.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textColor.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            if (ref.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = ref.notes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = subTextColor
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
                                        containerColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.05f),
                                        contentColor = textColor
                                    ),
                                    shape = CircleShape
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp), tint = textColor)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Link", color = textColor)
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
                containerColor = if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF),
                tonalElevation = 0.dp,
                title = { Text("Save Reference Link", color = textColor) },
                text = {
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
                                color = textColor.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF))
                                    .border(1.dp, if (isDark) Color(0xFF2A2A2D) else Color(0xFFE5E5EA), RoundedCornerShape(8.dp))
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
                                        color = textColor
                                    )
                                    Icon(
                                        imageVector = if (dropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = textColor
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier
                                    .background(if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF))
                                    .border(1.dp, if (isDark) Color(0xFF2A2A2D) else Color(0xFFE5E5EA), RoundedCornerShape(4.dp))
                            ) {
                                dynamicFolders.forEach { folder ->
                                    DropdownMenuItem(
                                        text = { Text(folder, color = textColor) },
                                        onClick = {
                                            selectedGroupDropdown = folder
                                            isCreatingCustomGroup = false
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("+ Create New Folder...", color = textColor) },
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color.White else Color.Black,
                            contentColor = if (isDark) Color.Black else Color.White
                        )
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = subTextColor) }
                }
            )
        }
    }
}
