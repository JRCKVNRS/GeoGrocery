package com.conspect.geogrocery.geofence

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.conspect.geogrocery.domain.repository.GroceryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Runs off the main thread to load each entered list from the DB and post its reminder
 * notification. Enqueued by [GeofenceBroadcastReceiver] so the work survives the receiver's
 * short lifetime even when the app process was killed.
 */
@HiltWorker
class GeofenceWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: GroceryRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val listIds = inputData.getStringArray(KEY_LIST_IDS) ?: return Result.success()
        for (listId in listIds) {
            val list = repository.getList(listId) ?: continue
            // Guard against a stale trigger for a list that has since been completed or muted.
            if (!list.geofenceShouldBeActive) continue
            notificationHelper.showArrivalNotification(list)
        }
        return Result.success()
    }

    companion object {
        const val KEY_LIST_IDS = "key_list_ids"
    }
}
