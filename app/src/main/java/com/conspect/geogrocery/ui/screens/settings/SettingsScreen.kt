package com.conspect.geogrocery.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.conspect.geogrocery.BuildConfig
import com.conspect.geogrocery.R
import com.conspect.geogrocery.ui.util.stringRes

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SettingRow(
            icon = Icons.Default.LocationOn,
            title = stringRes(R.string.settings_bg_location_title),
            subtitle = stringRes(R.string.settings_bg_location_desc)
        )
        SettingRow(
            icon = Icons.Default.Notifications,
            title = stringRes(R.string.settings_notifications_title),
            subtitle = stringRes(R.string.settings_notifications_desc)
        )
        SettingRow(
            icon = Icons.Default.Info,
            title = stringRes(R.string.settings_about_title),
            subtitle = stringRes(R.string.settings_about_desc, BuildConfig.VERSION_NAME)
        )
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, subtitle: String) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
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
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
