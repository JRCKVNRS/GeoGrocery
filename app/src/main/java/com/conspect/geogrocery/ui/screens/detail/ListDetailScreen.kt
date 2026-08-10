package com.conspect.geogrocery.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.conspect.geogrocery.R
import com.conspect.geogrocery.domain.model.GroceryList
import com.conspect.geogrocery.domain.model.ListItem
import com.conspect.geogrocery.ui.theme.AccentGreen
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
        containerColor = MaterialTheme.colorScheme.background,
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
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringRes(R.string.action_delete_list)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { padding ->
        if (current == null) return@Scaffold

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            HeaderCard(
                list = current,
                onReminderToggle = viewModel::setReminderEnabled
            )

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(current.items, key = { it.itemId }) { item ->
                    ItemRow(
                        item = item,
                        onToggle = { viewModel.setItemDone(item, it) },
                        onDelete = { viewModel.deleteItem(item) }
                    )
                }
            }

            AddItemBar(onAdd = viewModel::addItem)

            CompletionButton(
                isCompleted = current.isCompleted,
                onClick = { viewModel.setCompleted(!current.isCompleted) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun HeaderCard(
    list: GroceryList,
    onReminderToggle: (Boolean) -> Unit
) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = list.location.locationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = list.location.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringRes(R.string.reminder_toggle),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = list.reminderEnabled,
                    onCheckedChange = onReminderToggle,
                    enabled = !list.isCompleted,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = AccentGreen
                    )
                )
            }
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
        Checkbox(
            checked = item.isDone,
            onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(checkedColor = AccentGreen)
        )
        Text(
            text = item.text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (item.isDone) TextDecoration.LineThrough else null,
            color = if (item.isDone) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringRes(R.string.action_delete_item),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(stringRes(R.string.hint_add_item)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                onAdd(text)
                text = ""
            },
            enabled = text.isNotBlank(),
            modifier = Modifier
                .padding(start = 8.dp)
                .size(48.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringRes(R.string.action_add_item),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CompletionButton(
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (isCompleted) AccentGreen else MaterialTheme.colorScheme.outline
        ),
        modifier = modifier.fillMaxWidth().height(52.dp)
    ) {
        if (isCompleted) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(18.dp).padding(end = 4.dp)
            )
        }
        Text(
            text = if (isCompleted) stringRes(R.string.action_mark_active)
            else stringRes(R.string.action_mark_completed),
            fontWeight = FontWeight.SemiBold,
            color = if (isCompleted) AccentGreen else MaterialTheme.colorScheme.onBackground
        )
    }
}
