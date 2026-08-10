package com.conspect.geogrocery.geofence

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.conspect.geogrocery.R
import com.conspect.geogrocery.domain.model.GroceryList
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                GeofenceConstants.NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.geofence_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.geofence_channel_desc)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /** Builds and shows the "you have arrived" reminder with a preview of open items. */
    fun showArrivalNotification(list: GroceryList) {
        if (!hasNotificationPermission()) return

        val open = list.openItems
        val preview = open.take(GeofenceConstants.PREVIEW_ITEM_COUNT)
            .joinToString("\n") { "• ${it.text}" }
        val remaining = open.size - preview.lines().count { it.isNotBlank() }
        val body = buildString {
            if (preview.isBlank()) {
                append(context.getString(R.string.notif_no_open_items))
            } else {
                append(preview)
                if (remaining > 0) {
                    append("\n")
                    append(context.getString(R.string.notif_more_items, remaining))
                }
            }
        }

        val deepLink = Uri.parse(GeofenceConstants.DEEP_LINK_PREFIX + list.listId)
        val contentIntent = Intent(Intent.ACTION_VIEW, deepLink).apply {
            `package` = context.packageName
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(
            context,
            list.listId.hashCode(),
            contentIntent,
            flags
        )

        val notification = NotificationCompat.Builder(
            context,
            GeofenceConstants.NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notif_title, list.location.locationName))
            .setContentText(open.firstOrNull()?.text ?: list.title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        NotificationManagerCompat.from(context)
            .notify(list.listId.hashCode(), notification)
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }
}
