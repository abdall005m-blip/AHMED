package com.nexus.personaldashboard.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexus.personaldashboard.data.preferences.UserPreferencesRepository
import com.nexus.personaldashboard.data.repository.AIAppRepository
import com.nexus.personaldashboard.domain.model.AIApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefsRepo: UserPreferencesRepository,
    private val aiAppRepo: AIAppRepository
) : ViewModel() {

    fun setOnboardingDone() {
        viewModelScope.launch {
            prefsRepo.setOnboardingDone(true)
            insertDefaultAIApps()
        }
    }

    private suspend fun insertDefaultAIApps() {
        val existing = aiAppRepo.getAllAIApps().first()
        if (existing.isNotEmpty()) return

        val defaults = listOf(
            AIApp(
                name = "ChatGPT",
                packageName = "com.openai.chatgpt",
                deepLink = "chatgpt://",
                websiteUrl = "https://chat.openai.com",
                description = "AI Assistant by OpenAI",
                iconEmoji = "🤖",
                isPinned = true,
                orderIndex = 0
            ),
            AIApp(
                name = "Gemini",
                packageName = "com.google.android.apps.bard",
                deepLink = "",
                websiteUrl = "https://gemini.google.com",
                description = "AI Assistant by Google",
                iconEmoji = "✨",
                isPinned = true,
                orderIndex = 1
            ),
            AIApp(
                name = "Claude",
                packageName = "com.anthropic.claude",
                deepLink = "claude://",
                websiteUrl = "https://claude.ai",
                description = "AI Assistant by Anthropic",
                iconEmoji = "🧠",
                isPinned = false,
                orderIndex = 2
            ),
            AIApp(
                name = "Perplexity",
                packageName = "ai.perplexity.app",
                deepLink = "perplexity://",
                websiteUrl = "https://www.perplexity.ai",
                description = "AI-powered search engine",
                iconEmoji = "🔍",
                isPinned = false,
                orderIndex = 3
            ),
            AIApp(
                name = "Copilot",
                packageName = "com.microsoft.copilot",
                deepLink = "",
                websiteUrl = "https://copilot.microsoft.com",
                description = "AI Assistant by Microsoft",
                iconEmoji = "💡",
                isPinned = false,
                orderIndex = 4
            )
        )
        defaults.forEach { aiAppRepo.insert(it) }
    }
}
