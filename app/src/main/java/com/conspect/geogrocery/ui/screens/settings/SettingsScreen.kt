package com.conspect.geogrocery.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.conspect.geogrocery.BuildConfig
import com.conspect.geogrocery.R
import com.conspect.geogrocery.ui.theme.AccentGreen
import com.conspect.geogrocery.ui.util.stringRes
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val notificationsGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS).status.isGranted
    } else true

    val backgroundGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        rememberPermissionState(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION).status.isGranted
    } else true

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SettingRow(
            icon = Icons.Default.Notifications,
            title = stringRes(R.string.settings_notifications_title),
            status = if (notificationsGranted) stringRes(R.string.settings_status_on)
            else stringRes(R.string.settings_status_off),
            statusOk = notificationsGranted,
            hint = if (notificationsGranted) stringRes(R.string.settings_tap_manage)
            else stringRes(R.string.settings_notifications_hint_off),
            onClick = { context.startActivity(notificationSettingsIntent(context.packageName)) }
        )
        SettingRow(
            icon = Icons.Default.LocationOn,
            title = stringRes(R.string.settings_bg_location_title),
            status = if (backgroundGranted) stringRes(R.string.settings_status_always)
            else stringRes(R.string.settings_status_off),
            statusOk = backgroundGranted,
            hint = if (backgroundGranted) stringRes(R.string.settings_tap_manage)
            else stringRes(R.string.settings_bg_location_hint_off),
            onClick = { context.startActivity(appDetailsIntent(context.packageName)) }
        )
        SettingRow(
            icon = Icons.Default.Info,
            title = stringRes(R.string.settings_about_title),
            status = null,
            statusOk = true,
            hint = stringRes(R.string.settings_about_desc, BuildConfig.VERSION_NAME),
            onClick = null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    status: String?,
    statusOk: Boolean,
    hint: String,
    onClick: (() -> Unit)?
) {
    val cardModifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp)
    val shape = RoundedCornerShape(16.dp)
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (status != null) {
                        Text(
                            text = " · $status",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (statusOk) AccentGreen else MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onClick != null) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (onClick != null) {
        OutlinedCard(onClick = onClick, shape = shape, border = border, modifier = cardModifier) {
            content()
        }
    } else {
        OutlinedCard(shape = shape, border = border, modifier = cardModifier) {
            content()
        }
    }
}

private fun notificationSettingsIntent(packageName: String): Intent =
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

/** App details screen: from here the user opens Permissions → Location → "Allow all the time". */
private fun appDetailsIntent(packageName: String): Intent =
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
