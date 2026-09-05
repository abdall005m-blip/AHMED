package com.nexus.personaldashboard.data.db.dao

import androidx.room.*
import com.nexus.personaldashboard.data.db.entity.AIAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AIAppDao {
    @Query("SELECT * FROM ai_apps ORDER BY isPinned DESC, orderIndex ASC, name ASC")
    fun getAllAIApps(): Flow<List<AIAppEntity>>

    @Query("SELECT * FROM ai_apps WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchAIApps(query: String): Flow<List<AIAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: AIAppEntity): Long

    @Update
    suspend fun update(app: AIAppEntity)

    @Delete
    suspend fun delete(app: AIAppEntity)

    @Query("UPDATE ai_apps SET isPinned = :pinned WHERE id = :id")
    suspend fun setPinned(id: Long, pinned: Boolean)

    @Query("DELETE FROM ai_apps")
    suspend fun deleteAll()
}
