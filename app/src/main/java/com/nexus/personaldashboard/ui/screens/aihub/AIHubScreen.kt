package com.nexus.personaldashboard.ui.screens.aihub

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.personaldashboard.domain.model.AIApp
import com.nexus.personaldashboard.ui.components.EmptyState
import com.nexus.personaldashboard.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIHubScreen(
    viewModel: AIHubViewModel = hiltViewModel(),
    onAddApp: () -> Unit,
    onEditApp: (Long) -> Unit
) {
    val apps by viewModel.aiApps.collectAsStateWithLifecycle()
    var notInstalledAppDialog by remember { mutableStateOf<AIApp?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Hub 🤖", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddApp,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add AI App",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    emoji = "🤖",
                    title = "No AI tools added yet",
                    subtitle = "Tap + to add ChatGPT, Gemini, Claude, or your favorite AI assistants"
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = 80.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(apps, key = { it.id }) { app ->
                    val isInstalled = viewModel.isAppInstalled(app.packageName)
                    AIAppGridCard(
                        app = app,
                        isInstalled = isInstalled,
                        onOpen = {
                            if (isInstalled) {
                                viewModel.launchApp(app)
                            } else {
                                notInstalledAppDialog = app
                            }
                        },
                        onEdit = { onEditApp(app.id) },
                        onPin = { viewModel.togglePin(app) },
                        onDelete = { viewModel.deleteApp(app) }
                    )
                }
            }
        }
    }

    notInstalledAppDialog?.let { app ->
        AlertDialog(
            onDismissRequest = { notInstalledAppDialog = null },
            title = { Text("التطبيق غير مثبت / App Not Installed") },
            text = {
                Text(
                    "تطبيق ${app.name} غير مثبت حاليًا على هاتفك.\nهل ترغب في فتح الموقع الرسمي؟"
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.openWebsite(app.websiteUrl)
                    notInstalledAppDialog = null
                }) {
                    Text("Open Website / فتح الموقع")
                }
            },
            dismissButton = {
                TextButton(onClick = { notInstalledAppDialog = null }) {
                    Text("Cancel / إلغاء")
                }
            }
        )
    }
}

@Composable
fun AIAppGridCard(
    app: AIApp,
    isInstalled: Boolean,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onPin: () -> Unit,
    onDelete: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "app_card_scale"
    )
    var showMenu by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        onClick = {
            pressed = true
            onOpen()
            pressed = false
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = app.iconEmoji,
                    fontSize = 38.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
                if (app.isPinned) {
                    Icon(
                        Icons.Rounded.PushPin,
                        contentDescription = "Pinned",
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopEnd),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = app.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (app.description.isNotBlank()) {
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isInstalled) "● Installed" else "○ Not Installed",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isInstalled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Rounded.MoreVert,
                            contentDescription = "Options",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = { Icon(Icons.Rounded.Edit, null) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (app.isPinned) "Unpin" else "Pin to Top") },
                            leadingIcon = { Icon(Icons.Rounded.PushPin, null) },
                            onClick = {
                                showMenu = false
                                onPin()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Delete,
                                    null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
            Button(
                onClick = onOpen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (isInstalled) "Open" else "Website",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
