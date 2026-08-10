package com.conspect.geogrocery.ui.permissions

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Requests the runtime permissions geofencing needs, in the order Android requires:
 *  1. Foreground location (+ notifications on Android 13+) via one dialog.
 *  2. Background location, which must be requested *after* foreground is granted and, on
 *     Android 11+, is resolved through the system settings screen.
 *
 * The app content is always rendered — lists can be created without permissions; geofences are
 * only armed once the [com.conspect.geogrocery.geofence.GeofenceManager] sees them granted.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionGate(content: @Composable () -> Unit) {

    val foregroundPermissions = buildList {
        add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val foregroundState = rememberMultiplePermissionsState(foregroundPermissions)

    val backgroundState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        rememberPermissionState(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } else null

    LaunchedEffect(Unit) {
        if (!foregroundState.allPermissionsGranted) {
            foregroundState.launchMultiplePermissionRequest()
        }
    }

    // Once foreground location is granted, escalate to background location for geofencing.
    LaunchedEffect(foregroundState.allPermissionsGranted) {
        val fineGranted = foregroundState.permissions
            .firstOrNull { it.permission == android.Manifest.permission.ACCESS_FINE_LOCATION }
            ?.status?.isGranted == true
        if (fineGranted && backgroundState != null && !backgroundState.status.isGranted) {
            backgroundState.launchPermissionRequest()
        }
    }

    content()
}
