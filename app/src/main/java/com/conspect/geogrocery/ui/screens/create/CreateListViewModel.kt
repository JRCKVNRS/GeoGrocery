package com.conspect.geogrocery.ui.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conspect.geogrocery.domain.model.GroceryList
import com.conspect.geogrocery.domain.model.LocationSearchResult
import com.conspect.geogrocery.domain.model.StoreLocation
import com.conspect.geogrocery.domain.repository.GroceryRepository
import com.conspect.geogrocery.domain.repository.LocationSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CreateListUiState(
    val title: String = "",
    val query: String = "",
    val searchResults: List<LocationSearchResult> = emptyList(),
    val selectedLocation: LocationSearchResult? = null,
    val radiusMeters: Float = 150f,
    val isSearching: Boolean = false,
    val searchError: String? = null
) {
    val canSave: Boolean get() = title.isNotBlank() && selectedLocation != null
}

@OptIn(FlowPreview::class)
@HiltViewModel
class CreateListViewModel @Inject constructor(
    private val groceryRepository: GroceryRepository,
    private val locationSearchRepository: LocationSearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateListUiState())
    val uiState = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(350)
                .distinctUntilChanged()
                .collect { q -> runSearch(q) }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }

    fun onQueryChange(value: String) {
        _uiState.update {
            it.copy(query = value, selectedLocation = null, searchError = null)
        }
        queryFlow.value = value
    }

    fun onRadiusChange(value: Float) = _uiState.update { it.copy(radiusMeters = value) }

    fun onLocationSelected(result: LocationSearchResult) {
        _uiState.update {
            it.copy(
                selectedLocation = result,
                query = result.primaryName,
                searchResults = emptyList()
            )
        }
    }

    private suspend fun runSearch(query: String) {
        if (query.isBlank() || _uiState.value.selectedLocation != null) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        _uiState.update { it.copy(isSearching = true, searchError = null) }
        locationSearchRepository.search(query)
            .onSuccess { results ->
                _uiState.update { it.copy(searchResults = results, isSearching = false) }
            }
            .onFailure {
                _uiState.update { s ->
                    s.copy(isSearching = false, searchResults = emptyList(), searchError = "search_failed")
                }
            }
    }

    /** Persists the new list and returns its id via [onSaved]. */
    fun save(onSaved: (String) -> Unit) {
        val state = _uiState.value
        val location = state.selectedLocation ?: return
        val listId = UUID.randomUUID().toString()
        val list = GroceryList(
            listId = listId,
            title = state.title.trim(),
            isCompleted = false,
            reminderEnabled = true,
            location = StoreLocation(
                locationName = location.primaryName,
                address = location.displayName,
                latitude = location.latitude,
                longitude = location.longitude,
                radiusMeters = state.radiusMeters
            ),
            createdAt = System.currentTimeMillis(),
            items = emptyList()
        )
        viewModelScope.launch {
            groceryRepository.saveList(list)
            onSaved(listId)
        }
    }
}
