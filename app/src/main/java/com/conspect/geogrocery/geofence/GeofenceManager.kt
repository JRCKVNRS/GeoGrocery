package com.conspect.geogrocery.geofence

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.conspect.geogrocery.domain.model.GroceryList
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin wrapper over the Google Geofencing API. The list's [GroceryList.listId] is used verbatim
 * as the geofence request id, so add/remove map one-to-one to a list.
 */
@Singleton
class GeofenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client: GeofencingClient = LocationServices.getGeofencingClient(context)

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = GeofenceConstants.ACTION_GEOFENCE_EVENT
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        PendingIntent.getBroadcast(context, 0, intent, flags)
    }

    fun hasLocationPermissions(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val background = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else true
        return fine && background
    }

    /** Registers (or replaces) the geofence for [list]. No-op if permissions are missing. */
    @SuppressLint("MissingPermission")
    suspend fun register(list: GroceryList): Result<Unit> {
        if (!hasLocationPermissions()) {
            return Result.failure(SecurityException("Location permissions not granted"))
        }
        val geofence = Geofence.Builder()
            .setRequestId(list.listId)
            .setCircularRegion(
                list.location.latitude,
                list.location.longitude,
                list.location.radiusMeters
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setNotificationResponsiveness(GeofenceConstants.NOTIFICATION_RESPONSIVENESS_MS)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(0) // do not fire immediately if already inside on registration
            .addGeofence(geofence)
            .build()

        return runCatching {
            client.addGeofences(request, pendingIntent).await()
            Unit
        }
    }

    /** Removes the geofence for a single list. */
    suspend fun remove(listId: String): Result<Unit> = runCatching {
        client.removeGeofences(listOf(listId)).await()
        Unit
    }

    /** Re-registers every list that should currently be armed (used after reboot). */
    suspend fun registerAll(lists: List<GroceryList>): Result<Unit> = runCatching {
        lists.filter { it.geofenceShouldBeActive }.forEach { register(it) }
        Unit
    }
}
