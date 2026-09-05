package com.nexus.personaldashboard.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nexus.personaldashboard.data.db.AppDatabase
import com.nexus.personaldashboard.util.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tasks = database.taskDao().getAllTasks().firstOrNull() ?: emptyList()
                    tasks.filter { it.reminderEnabled && !it.isCompleted }.forEach { taskEntity ->
                        reminderScheduler.scheduleTaskReminder(taskEntity.toDomain())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
