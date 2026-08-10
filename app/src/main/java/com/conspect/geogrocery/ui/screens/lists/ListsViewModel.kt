package com.conspect.geogrocery.ui.screens.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conspect.geogrocery.domain.model.GroceryList
import com.conspect.geogrocery.domain.repository.GroceryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListsUiState(
    val lists: List<GroceryList> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ListsViewModel @Inject constructor(
    private val repository: GroceryRepository
) : ViewModel() {

    val uiState: StateFlow<ListsUiState> =
        repository.observeLists()
            .map { ListsUiState(lists = it, isLoading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ListsUiState()
            )

    fun onReminderToggled(listId: String, enabled: Boolean) = viewModelScope.launch {
        repository.setReminderEnabled(listId, enabled)
    }

    fun onDeleteList(listId: String) = viewModelScope.launch {
        repository.deleteList(listId)
    }
}
