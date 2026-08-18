package com.conspect.geogrocery.ui.screens.quickcreate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.conspect.geogrocery.R
import com.conspect.geogrocery.ui.util.stringRes

/** Loading screen that geocodes the spoken store name and creates the list, then routes onward. */
@Composable
fun QuickCreateScreen(
    store: String,
    onCreated: (String) -> Unit,
    onFailed: (String) -> Unit,
    viewModel: QuickCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(store) { viewModel.start(store) }
    LaunchedEffect(state) {
        when (val s = state) {
            is QuickCreateViewModel.State.Created -> onCreated(s.listId)
            is QuickCreateViewModel.State.Failed -> onFailed(s.query)
            QuickCreateViewModel.State.Loading -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Text(
            text = stringRes(R.string.quick_create_loading, store),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}
