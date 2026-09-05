package com.nexus.personaldashboard.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.personaldashboard.ui.components.EmptyState
import com.nexus.personaldashboard.ui.components.GlassCard
import com.nexus.personaldashboard.ui.components.NexusSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    NexusSearchBar(
                        query = query,
                        onQueryChange = viewModel::setQuery,
                        placeholder = "Search notifications, tasks, AI..."
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
        if (query.trim().length < 2) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    emoji = "🔍",
                    title = "Type to search",
                    subtitle = "Search across notifications, tasks, schedules, and AI apps"
                )
            }
        } else {
            val totalResults = results.notifications.size + results.tasks.size + results.schedules.size + results.aiApps.size
            if (totalResults == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        emoji = "🧐",
                        title = "No results found",
                        subtitle = "Try searching with a different keyword"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (results.notifications.isNotEmpty()) {
                        item {
                            Text(
                                "Notifications",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        items(results.notifications) { notif ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    notif.appName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    notif.title.ifBlank { notif.content },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (results.tasks.isNotEmpty()) {
                        item {
                            Text(
                                "Tasks",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        items(results.tasks) { task ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    task.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                if (task.description.isNotBlank()) {
                                    Text(
                                        task.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (results.schedules.isNotEmpty()) {
                        item {
                            Text(
                                "Schedules",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        items(results.schedules) { schedule ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    schedule.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (results.aiApps.isNotEmpty()) {
                        item {
                            Text(
                                "AI Tools",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        items(results.aiApps) { app ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(app.iconEmoji, style = MaterialTheme.typography.titleLarge)
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text(app.name, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            app.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
