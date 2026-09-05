package com.nexus.personaldashboard.di

import android.content.Context
import androidx.room.Room
import com.nexus.personaldashboard.data.db.AppDatabase
import com.nexus.personaldashboard.data.db.dao.AIAppDao
import com.nexus.personaldashboard.data.db.dao.NotificationDao
import com.nexus.personaldashboard.data.db.dao.ScheduleDao
import com.nexus.personaldashboard.data.db.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "nexus_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideScheduleDao(db: AppDatabase): ScheduleDao = db.scheduleDao()

    @Provides
    fun provideAIAppDao(db: AppDatabase): AIAppDao = db.aiAppDao()

    @Provides
    fun providePrivateMessageDao(db: AppDatabase): com.nexus.personaldashboard.data.db.dao.PrivateMessageDao = db.privateMessageDao()
}
