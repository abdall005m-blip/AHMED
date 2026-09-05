package com.nexus.personaldashboard.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nexus.personaldashboard.domain.model.AIApp

@Entity(tableName = "ai_apps")
data class AIAppEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val packageName: String,
    val deepLink: String = "",
    val websiteUrl: String,
    val description: String = "",
    val iconEmoji: String = "🤖",
    val isPinned: Boolean = false,
    val orderIndex: Int = 0
) {
    fun toDomain() = AIApp(
        id = id,
        name = name,
        packageName = packageName,
        deepLink = deepLink,
        websiteUrl = websiteUrl,
        description = description,
        iconEmoji = iconEmoji,
        isPinned = isPinned,
        orderIndex = orderIndex
    )
}

fun AIApp.toEntity() = AIAppEntity(
    id = id,
    name = name,
    packageName = packageName,
    deepLink = deepLink,
    websiteUrl = websiteUrl,
    description = description,
    iconEmoji = iconEmoji,
    isPinned = isPinned,
    orderIndex = orderIndex
)
