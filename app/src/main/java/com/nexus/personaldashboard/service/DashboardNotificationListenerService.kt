package com.nexus.personaldashboard.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.nexus.personaldashboard.data.db.AppDatabase
import com.nexus.personaldashboard.data.db.entity.NotificationEntity
import com.nexus.personaldashboard.domain.model.NotificationPriority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DashboardNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var database: AppDatabase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val ACTION_NOTIFICATION_POSTED = "com.nexus.NOTIFICATION_POSTED"
        private val IGNORED_PACKAGES = setOf(
            "android",
            "com.android.systemui",
            "com.android.settings",
            "com.nexus.personaldashboard"
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        if (sbn.packageName in IGNORED_PACKAGES) return
        if (sbn.isOngoing) return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val content = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""

        if (title.isBlank() && content.isBlank()) return

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val isSilent = audioManager?.let {
            it.ringerMode == AudioManager.RINGER_MODE_SILENT || it.ringerMode == AudioManager.RINGER_MODE_VIBRATE
        } ?: false

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
        val isDND = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager?.currentInterruptionFilter != android.app.NotificationManager.INTERRUPTION_FILTER_ALL
        } else false

        val priority = when (notification.priority) {
            Notification.PRIORITY_MAX, Notification.PRIORITY_HIGH -> NotificationPriority.HIGH
            Notification.PRIORITY_DEFAULT -> NotificationPriority.MEDIUM
            else -> NotificationPriority.LOW
        }

        val appName = try {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(sbn.packageName, 0)
            ).toString()
        } catch (e: Exception) {
            sbn.packageName
        }

        val entity = NotificationEntity(
            appPackage = sbn.packageName,
            appName = appName,
            title = title,
            content = content,
            timestamp = sbn.postTime,
            priority = priority.name,
            isSilentMode = isSilent,
            isDND = isDND,
            category = notification.category ?: ""
        )

        serviceScope.launch {
            try {
                database.notificationDao().insert(entity)
                val intent = Intent(ACTION_NOTIFICATION_POSTED)
                intent.setPackage(packageName)
                sendBroadcast(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Kept in local history for personal dashboard logging as requested
    }
}
