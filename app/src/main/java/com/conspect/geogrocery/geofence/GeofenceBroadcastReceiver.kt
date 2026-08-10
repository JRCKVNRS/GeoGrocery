package com.conspect.geogrocery.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

/**
 * Entry point for Location Services geofence callbacks. Keeps work minimal: validates the event
 * and hands the entered list ids to an expedited [GeofenceWorker].
 */
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != GeofenceConstants.ACTION_GEOFENCE_EVENT) return

        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) {
            Log.e(TAG, "Geofence event error code: ${event.errorCode}")
            return
        }
        if (event.geofenceTransition != Geofence.GEOFENCE_TRANSITION_ENTER) return

        val listIds = event.triggeringGeofences
            ?.map { it.requestId }
            ?.toTypedArray()
            ?: return
        if (listIds.isEmpty()) return

        val request = OneTimeWorkRequestBuilder<GeofenceWorker>()
            .setInputData(Data.Builder().putStringArray(GeofenceWorker.KEY_LIST_IDS, listIds).build())
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    companion object {
        private const val TAG = "GeofenceReceiver"
    }
}
