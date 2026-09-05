package com.nexus.personaldashboard.data.repository

import com.nexus.personaldashboard.data.db.dao.AIAppDao
import com.nexus.personaldashboard.data.db.entity.toEntity
import com.nexus.personaldashboard.domain.model.AIApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIAppRepository @Inject constructor(
    private val dao: AIAppDao
) {
    fun getAllAIApps(): Flow<List<AIApp>> =
        dao.getAllAIApps().map { list -> list.map { it.toDomain() } }

    fun searchAIApps(query: String): Flow<List<AIApp>> =
        dao.searchAIApps(query).map { list -> list.map { it.toDomain() } }

    suspend fun insert(app: AIApp): Long = dao.insert(app.toEntity())
    suspend fun update(app: AIApp) = dao.update(app.toEntity())
    suspend fun delete(app: AIApp) = dao.delete(app.toEntity())
    suspend fun setPinned(id: Long, pinned: Boolean) = dao.setPinned(id, pinned)
    suspend fun deleteAll() = dao.deleteAll()
}
