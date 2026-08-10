package com.conspect.geogrocery.geofence

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.conspect.geogrocery.domain.repository.GroceryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** Re-registers all active geofences after a reboot. */
@HiltWorker
class GeofenceReRegisterWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: GroceryRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return runCatching { repository.refreshAllGeofences() }
            .fold(onSuccess = { Result.success() }, onFailure = { Result.retry() })
    }
}
