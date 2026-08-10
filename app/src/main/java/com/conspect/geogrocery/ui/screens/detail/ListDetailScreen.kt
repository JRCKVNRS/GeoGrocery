package com.conspect.geogrocery.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.conspect.geogrocery.R
import com.conspect.geogrocery.domain.model.GroceryList
import com.conspect.geogrocery.domain.model.ListItem
import com.conspect.geogrocery.ui.util.stringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    onBack: () -> Unit,
    viewModel: ListDetailViewModel = hiltViewModel()
) {
    val list by viewModel.list.collectAsStateWithLifecycle()
    val current = list

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(current?.title ?: stringRes(R.string.title_list)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.deleteList(onBack) }) {
                        Icon(Icons.Default.Delete, contentDescription = stringRes(R.string.action_delete_list))
                    }
                }
            )
        }
    ) { padding ->
        if (current == null) return@Scaffold

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            HeaderCard(
                list = current,
                onReminderToggle = viewModel::setReminderEnabled,
                onCompletedToggle = viewModel::setCompleted
            )

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(current.items, key = { it.itemId }) { item ->
                    ItemRow(
                        item = item,
                        onToggle = { viewModel.setItemDone(item, it) },
                        onDelete = { viewModel.deleteItem(item) }
                    )
                }
            }

            HorizontalDivider()
            AddItemBar(onAdd = viewModel::addItem)
        }
    }
}

@Composable
private fun HeaderCard(
    list: GroceryList,
    onReminderToggle: (Boolean) -> Unit,
    onCompletedToggle: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Column {
                    Text(list.location.locationName, style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = stringRes(R.string.radius_summary, list.location.radiusMeters.toInt()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringRes(R.string.reminder_toggle),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = list.reminderEnabled,
                    onCheckedChange = onReminderToggle,
                    enabled = !list.isCompleted
                )
            }

            FilterChip(
                selected = list.isCompleted,
                onClick = { onCompletedToggle(!list.isCompleted) },
                label = {
                    Text(
                        if (list.isCompleted) stringRes(R.string.status_completed)
                        else stringRes(R.string.action_mark_completed)
                    )
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun ItemRow(
    item: ListItem,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = item.isDone, onCheckedChange = onToggle)
        Text(
            text = item.text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (item.isDone) TextDecoration.LineThrough else null,
            color = if (item.isDone) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = stringRes(R.string.action_delete_item))
        }
    }
}

@Composable
private fun AddItemBar(onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(stringRes(R.string.hint_add_item)) },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                onAdd(text)
                text = ""
            },
            enabled = text.isNotBlank()
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringRes(R.string.action_add_item),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
