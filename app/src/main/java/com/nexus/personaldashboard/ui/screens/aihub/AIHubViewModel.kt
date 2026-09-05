package com.nexus.personaldashboard.ui.screens.aihub

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.personaldashboard.data.repository.AIAppRepository
import com.nexus.personaldashboard.domain.model.AIApp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIHubViewModel @Inject constructor(
    private val repo: AIAppRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val aiApps: StateFlow<List<AIApp>> = repo.getAllAIApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun isAppInstalled(packageName: String): Boolean {
        if (packageName.isBlank()) return false
        return try {
            context.packageManager.getLaunchIntentForPackage(packageName) != null
        } catch (e: Exception) {
            false
        }
    }

    fun launchApp(app: AIApp, onFallbackPrompt: ((AIApp) -> Unit)? = null) {
        // Priority 1: Launch Native Android App directly if installed
        val launchIntent = try {
            if (app.packageName.isNotBlank()) {
                context.packageManager.getLaunchIntentForPackage(app.packageName)
            } else null
        } catch (e: Exception) {
            null
        }

        if (launchIntent != null) {
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(launchIntent)
            return
        }

        // Priority 2: Deep Link (if package wasn't resolvable by standard launch intent)
        if (app.deepLink.isNotBlank()) {
            try {
                val deepLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(app.deepLink)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                if (deepLinkIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(deepLinkIntent)
                    return
                }
            } catch (e: Exception) {
                // deep link failed, proceed to fallback
            }
        }

        // Priority 3: Fallback callback or open official website
        if (onFallbackPrompt != null) {
            onFallbackPrompt(app)
        } else {
            openWebsite(app.websiteUrl)
        }
    }

    fun openWebsite(url: String) {
        if (url.isBlank()) return
        try {
            val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else url
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun togglePin(app: AIApp) = viewModelScope.launch {
        repo.setPinned(app.id, !app.isPinned)
    }

    fun deleteApp(app: AIApp) = viewModelScope.launch {
        repo.delete(app)
    }

    fun insertApp(app: AIApp) = viewModelScope.launch {
        repo.insert(app)
    }

    fun updateApp(app: AIApp) = viewModelScope.launch {
        repo.update(app)
    }
}
