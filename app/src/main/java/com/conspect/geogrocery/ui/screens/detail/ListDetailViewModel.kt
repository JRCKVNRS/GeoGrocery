package com.conspect.geogrocery.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conspect.geogrocery.domain.model.GroceryList
import com.conspect.geogrocery.domain.model.ListItem
import com.conspect.geogrocery.domain.repository.GroceryRepository
import com.conspect.geogrocery.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val repository: GroceryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val listId: String = checkNotNull(savedStateHandle[Routes.ARG_LIST_ID])

    val list: StateFlow<GroceryList?> =
        repository.observeList(listId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    fun addItem(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch { repository.addItem(listId, text) }
    }

    fun setItemDone(item: ListItem, isDone: Boolean) = viewModelScope.launch {
        repository.setItemDone(item.itemId, isDone)
    }

    fun deleteItem(item: ListItem) = viewModelScope.launch { repository.deleteItem(item) }

    fun renameItem(item: ListItem, text: String) = viewModelScope.launch {
        repository.updateItem(item.copy(text = text.trim()))
    }

    fun setReminderEnabled(enabled: Boolean) = viewModelScope.launch {
        repository.setReminderEnabled(listId, enabled)
    }

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        repository.setCompleted(listId, completed)
    }

    fun rename(title: String) = viewModelScope.launch {
        val current = repository.getList(listId) ?: return@launch
        if (title.isNotBlank()) repository.saveList(current.copy(title = title.trim()))
    }

    fun deleteList(onDeleted: () -> Unit) = viewModelScope.launch {
        repository.deleteList(listId)
        onDeleted()
    }
}
