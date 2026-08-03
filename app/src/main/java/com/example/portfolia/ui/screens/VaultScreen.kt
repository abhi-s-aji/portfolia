package com.example.portfolia.ui.screens

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.portfolia.PortfoliaApp
import com.example.portfolia.data.DevSecretEntity
import com.example.portfolia.ui.components.AppleGlassCard
import com.google.gson.GsonBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VaultViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as PortfoliaApp).database.secretDao()
    val secrets: StateFlow<List<DevSecretEntity>> = dao.getAllSecrets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSecret(title: String, secretType: String, envNameOrUrl: String?, secretValue: String) {
        viewModelScope.launch {
            val encryptedValue = android.util.Base64.encodeToString(secretValue.toByteArray(), android.util.Base64.DEFAULT).trim()
            dao.insertSecret(
                DevSecretEntity(
                    title = title,
                    secretType = secretType,
                    envNameOrUrl = envNameOrUrl,
                    secretValue = encryptedValue
                )
            )
        }
    }

    fun deleteSecret(secret: DevSecretEntity) {
        viewModelScope.launch {
            dao.deleteSecret(secret)
        }
    }

    fun decryptValue(encryptedValue: String): String {
        return try {
            val decodedBytes = android.util.Base64.decode(encryptedValue, android.util.Base64.DEFAULT)
            String(decodedBytes)
        } catch (e: Exception) {
            encryptedValue
        }
    }
}

fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}

fun triggerBiometricAuth(
    context: Context,
    activity: FragmentActivity?,
    onSuccess: () -> Unit
) {
    if (activity == null) {
        onSuccess()
        return
    }
    val biometricManager = BiometricManager.from(context)
    val canAuthenticate = biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
    )
    
    if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
        Toast.makeText(context, "Biometric authentication bypassed (unsupported/unenrolled)", Toast.LENGTH_SHORT).show()
        onSuccess()
        return
    }

    val executor = ContextCompat.getMainExecutor(context)
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock Secret Vault")
        .setSubtitle("Authenticate to view sensitive keys")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        .build()

    biometricPrompt.authenticate(promptInfo)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    viewModel: VaultViewModel = viewModel()
) {
    val context = LocalContext.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val activity = remember(context) { context.findActivity() }
    val secrets by viewModel.secrets.collectAsState()

    var isUnlocked by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showExportOptions by remember { mutableStateOf(false) }

    // Add Secret Form States
    var secretTitle by remember { mutableStateOf("") }
    var secretType by remember { mutableStateOf("API_KEY") } // "API_KEY", "ENV_VAR", "WEB_LINK_CONFIG"
    var keyNameOrUrl by remember { mutableStateOf("") }
    var secretValue by remember { mutableStateOf("") }

    val filters = listOf("All", "API Keys", ".env Variables", "Web Configs")
    val filteredSecrets = remember(secrets, selectedFilter) {
        when (selectedFilter) {
            "API Keys" -> secrets.filter { it.secretType == "API_KEY" }
            ".env Variables" -> secrets.filter { it.secretType == "ENV_VAR" }
            "Web Configs" -> secrets.filter { it.secretType == "WEB_LINK_CONFIG" }
            else -> secrets
        }
    }

    val isDark = MaterialTheme.colorScheme.background == Color(0xFF141415)
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Env File Saver Launcher
    val createEnvFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    val envText = buildString {
                        secrets.forEach { secret ->
                            val key = secret.envNameOrUrl?.uppercase()?.replace(Regex("[^A-Z0-9_]"), "_") ?: "SECRET"
                            val decrypted = viewModel.decryptValue(secret.secretValue)
                            appendLine("$key=\"$decrypted\"")
                        }
                    }
                    output.write(envText.toByteArray())
                    Toast.makeText(context, "Exported successfully as .env file", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Trigger biometric check on mount
    LaunchedEffect(Unit) {
        if (!isUnlocked) {
            triggerBiometricAuth(context, activity) {
                isUnlocked = true
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Dev Secret Vault", color = textColor, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    if (isUnlocked) {
                        IconButton(onClick = { showExportOptions = true }) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "Export Vault", tint = textColor)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (isUnlocked) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = if (isDark) Color.White else Color.Black,
                    contentColor = if (isDark) Color.Black else Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Secret",
                        tint = if (isDark) Color.Black else Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        if (!isUnlocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                AppleGlassCard(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            modifier = Modifier.size(56.dp),
                            tint = subTextColor
                        )
                        Text(
                            text = "Secret Vault is Locked",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = "Authenticate via biometrics to view secure keys, tokens, and system environment variables.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = subTextColor,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = {
                                triggerBiometricAuth(context, activity) {
                                    isUnlocked = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color.White else Color.Black,
                                contentColor = if (isDark) Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Unlock Vault", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 14.dp)
            ) {
                // Filter row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val isSelected = selectedFilter == filter
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
                                    selectedFilter = filter
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = filter,
                                color = textCol,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredSecrets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = subTextColor
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No developer secrets recorded",
                                style = MaterialTheme.typography.titleMedium,
                                color = subTextColor
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredSecrets, key = { it.id }) { secret ->
                            var isRevealed by remember { mutableStateOf(false) }
                            val decryptedValue = remember(secret.secretValue) { viewModel.decryptValue(secret.secretValue) }

                            AppleGlassCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = secret.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = textColor
                                    )
                                    val typeLabel = when (secret.secretType) {
                                        "API_KEY" -> "API Key"
                                        "ENV_VAR" -> "ENV Var"
                                        else -> "Web Config"
                                    }
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)
                                    ) {
                                        Text(
                                            text = typeLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = textColor.copy(alpha = 0.8f),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                if (!secret.envNameOrUrl.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = secret.envNameOrUrl,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = subTextColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Secret display row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isDark) Color(0xFF141415) else Color(0xFFF6F6F8),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isDark) Color(0xFF2A2A2D) else Color(0xFFE5E5EA),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isRevealed) decryptedValue else "••••••••••••••••",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textColor,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { isRevealed = !isRevealed },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle Visibility",
                                            tint = subTextColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("DevSecret", decryptedValue))
                                            Toast.makeText(context, "Copied to clipboard. Auto-clearing in 30s.", Toast.LENGTH_SHORT).show()

                                            coroutineScope.launch {
                                                delay(30000)
                                                if (clipboard.primaryClip?.getItemAt(0)?.text == decryptedValue) {
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
                                                    Toast.makeText(context, "Clipboard cleared for security.", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.05f),
                                            contentColor = textColor
                                        ),
                                        shape = CircleShape
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = textColor)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Copy Key", color = textColor, style = MaterialTheme.typography.bodyMedium)
                                    }

                                    IconButton(onClick = {
                                        view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                                        viewModel.deleteSecret(secret)
                                    }) {
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

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                containerColor = if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF),
                tonalElevation = 0.dp,
                title = { Text("Save Dev Secret Key", color = textColor) },
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
                            value = secretTitle,
                            onValueChange = { secretTitle = it },
                            label = { Text("Title") },
                            colors = inputColors,
                            singleLine = true
                        )

                        // Segmented type selector
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Secret Type",
                                style = MaterialTheme.typography.bodySmall,
                                color = subTextColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val types = listOf("API_KEY" to "API Key", "ENV_VAR" to "ENV Var", "WEB_LINK_CONFIG" to "Web Config")
                                types.forEach { (type, label) ->
                                    val isSelected = secretType == type
                                    val buttonBg = if (isSelected) {
                                        if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                                    } else Color.Transparent

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(buttonBg)
                                            .border(
                                                1.dp,
                                                if (isSelected) Color.Transparent else if (isDark) Color(0xFF2A2A2D) else Color(0xFFE5E5EA),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { secretType = type }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = textColor,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        val keyLabel = when (secretType) {
                            "API_KEY" -> "Key Identifier (e.g. STRIPE_API_KEY)"
                            "ENV_VAR" -> "Environment Variable Name"
                            else -> "Configuration Target Link / Host URL"
                        }
                        OutlinedTextField(
                            value = keyNameOrUrl,
                            onValueChange = { keyNameOrUrl = it },
                            label = { Text(keyLabel) },
                            colors = inputColors,
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = secretValue,
                            onValueChange = { secretValue = it },
                            label = { Text("Secret Payload / Value") },
                            colors = inputColors
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (secretTitle.isNotBlank() && secretValue.isNotBlank()) {
                                viewModel.addSecret(
                                    title = secretTitle,
                                    secretType = secretType,
                                    envNameOrUrl = keyNameOrUrl.ifBlank { null },
                                    secretValue = secretValue
                                )
                                secretTitle = ""; secretType = "API_KEY"; keyNameOrUrl = ""; secretValue = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color.White else Color.Black,
                            contentColor = if (isDark) Color.Black else Color.White
                        )
                    ) {
                        Text("Save Key", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = subTextColor) }
                }
            )
        }

        if (showExportOptions) {
            AlertDialog(
                onDismissRequest = { showExportOptions = false },
                containerColor = if (isDark) Color(0xFF1E1E20) else Color(0xFFFFFFFF),
                title = { Text("Export Secret Vault", color = textColor) },
                text = {
                    Text(
                        "Please authenticate via biometrics to proceed with downloading the .env backup file or copying the JSON structure.",
                        color = subTextColor
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showExportOptions = false
                            triggerBiometricAuth(context, activity) {
                                createEnvFileLauncher.launch("portfolia.env")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color.White else Color.Black,
                            contentColor = if (isDark) Color.Black else Color.White
                        )
                    ) {
                        Text("Download .env File", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showExportOptions = false
                            triggerBiometricAuth(context, activity) {
                                val gson = GsonBuilder().setPrettyPrinting().create()
                                val backupList = secrets.map {
                                    mapOf(
                                        "title" to it.title,
                                        "secretType" to it.secretType,
                                        "envNameOrUrl" to it.envNameOrUrl,
                                        "secretValue" to viewModel.decryptValue(it.secretValue)
                                    )
                                }
                                val jsonText = gson.toJson(backupList)
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("DevSecretsBackup", jsonText))
                                Toast.makeText(context, "JSON backup copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Copy JSON Backup", color = textColor)
                    }
                }
            )
        }
    }
}
