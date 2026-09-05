package com.nexus.personaldashboard.ui.screens.aihub

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexus.personaldashboard.domain.model.AIApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAIAppScreen(
    appId: Long = 0L,
    viewModel: AIHubViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val apps by viewModel.aiApps.collectAsState()
    val existingApp = apps.find { it.id == appId }

    var name by remember(existingApp) { mutableStateOf(existingApp?.name ?: "") }
    var packageName by remember(existingApp) { mutableStateOf(existingApp?.packageName ?: "") }
    var deepLink by remember(existingApp) { mutableStateOf(existingApp?.deepLink ?: "") }
    var websiteUrl by remember(existingApp) { mutableStateOf(existingApp?.websiteUrl ?: "") }
    var description by remember(existingApp) { mutableStateOf(existingApp?.description ?: "") }
    var iconEmoji by remember(existingApp) { mutableStateOf(existingApp?.iconEmoji ?: "🤖") }

    val isInstalled = packageName.isNotBlank() && viewModel.isAppInstalled(packageName)
    val isEditing = appId != 0L

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit AI App" else "Add AI App",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = iconEmoji,
                onValueChange = { iconEmoji = it },
                label = { Text("Icon Emoji (e.g. 🤖, 🧠, ✨)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("App Name *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = packageName,
                onValueChange = { packageName = it },
                label = { Text("Package Name (e.g. com.openai.chatgpt)") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    Text(
                        if (isInstalled) "✅ Installed on this device"
                        else if (packageName.isNotBlank()) "⚠️ Not currently installed"
                        else "Used to launch the app directly"
                    )
                }
            )

            OutlinedTextField(
                value = deepLink,
                onValueChange = { deepLink = it },
                label = { Text("Deep Link / Intent (e.g. chatgpt://)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = websiteUrl,
                onValueChange = { websiteUrl = it },
                label = { Text("Website URL *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    val app = AIApp(
                        id = if (isEditing) appId else 0L,
                        name = name.trim(),
                        packageName = packageName.trim(),
                        deepLink = deepLink.trim(),
                        websiteUrl = websiteUrl.trim(),
                        description = description.trim(),
                        iconEmoji = iconEmoji.ifBlank { "🤖" }
                    )
                    if (isEditing) viewModel.updateApp(app) else viewModel.insertApp(app)
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = name.isNotBlank() && websiteUrl.isNotBlank()
            ) {
                Text(
                    if (isEditing) "Save Changes" else "Add AI App",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
