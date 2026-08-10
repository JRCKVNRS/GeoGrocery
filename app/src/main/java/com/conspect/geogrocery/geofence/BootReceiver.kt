package com.conspect.geogrocery.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * Geofences do not survive a reboot, so on BOOT_COMPLETED we enqueue a worker that re-registers
 * every list whose reminder should currently be active.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) return

        val request = OneTimeWorkRequestBuilder<GeofenceReRegisterWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
