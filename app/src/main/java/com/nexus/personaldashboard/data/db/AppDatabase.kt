package com.nexus.personaldashboard.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nexus.personaldashboard.data.db.dao.AIAppDao
import com.nexus.personaldashboard.data.db.dao.NotificationDao
import com.nexus.personaldashboard.data.db.dao.ScheduleDao
import com.nexus.personaldashboard.data.db.dao.TaskDao
import com.nexus.personaldashboard.data.db.entity.AIAppEntity
import com.nexus.personaldashboard.data.db.entity.NotificationEntity
import com.nexus.personaldashboard.data.db.entity.ScheduleEntity
import com.nexus.personaldashboard.data.db.entity.TaskEntity

import com.nexus.personaldashboard.data.db.dao.PrivateMessageDao
import com.nexus.personaldashboard.data.db.entity.PrivateMessageEntity

@Database(
    entities = [
        NotificationEntity::class,
        TaskEntity::class,
        ScheduleEntity::class,
        AIAppEntity::class,
        PrivateMessageEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun taskDao(): TaskDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun aiAppDao(): AIAppDao
    abstract fun privateMessageDao(): PrivateMessageDao
}
