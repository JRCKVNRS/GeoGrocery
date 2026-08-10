package com.conspect.geogrocery.ui.screens.lists

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.conspect.geogrocery.R
import com.conspect.geogrocery.domain.model.GroceryList
import com.conspect.geogrocery.ui.theme.AccentGreen
import com.conspect.geogrocery.ui.util.stringRes

@Composable
fun ListsScreen(
    contentPadding: PaddingValues,
    onCreateList: () -> Unit,
    onOpenList: (String) -> Unit,
    viewModel: ListsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding()
            )
    ) {
        CreateListButton(
            onClick = onCreateList,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (!state.isLoading && state.lists.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.lists, key = { it.listId }) { list ->
                    ListCard(
                        list = list,
                        onClick = { onOpenList(list.listId) },
                        onReminderToggle = { viewModel.onReminderToggled(list.listId, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateListButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = stringRes(R.string.action_new_list).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListCard(
    list: GroceryList,
    onClick: () -> Unit,
    onReminderToggle: (Boolean) -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = list.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (list.isCompleted) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Switch(
                        checked = list.reminderEnabled,
                        onCheckedChange = onReminderToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = AccentGreen
                        )
                    )
                }
            }

            StatusChip(list = list, modifier = Modifier.padding(top = 8.dp))

            Text(
                text = subtitleFor(list),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun StatusChip(list: GroceryList, modifier: Modifier = Modifier) {
    val (icon: ImageVector, label: String) = when {
        list.isCompleted -> Icons.Default.CheckCircle to stringRes(R.string.status_completed)
        list.reminderEnabled -> Icons.Default.NotificationsActive to stringRes(R.string.status_active)
        else -> Icons.Default.NotificationsOff to stringRes(R.string.status_reminder_off)
    }
    val tint = if (list.isCompleted || list.reminderEnabled) AccentGreen
    else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .size(16.dp)
                .padding(end = 0.dp)
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = tint,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
private fun subtitleFor(list: GroceryList): String {
    val total = list.items.size
    val open = list.openItems.size
    return if (total > 0 && open == 0) {
        stringRes(R.string.list_all_done, total)
    } else {
        stringRes(R.string.list_open_count, total, open)
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringRes(R.string.empty_lists),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
