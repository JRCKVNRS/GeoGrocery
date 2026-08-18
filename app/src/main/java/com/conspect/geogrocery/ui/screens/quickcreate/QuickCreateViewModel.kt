package com.conspect.geogrocery.ui.screens.quickcreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conspect.geogrocery.domain.model.GroceryList
import com.conspect.geogrocery.domain.model.StoreLocation
import com.conspect.geogrocery.domain.repository.GroceryRepository
import com.conspect.geogrocery.domain.repository.LocationSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/** Smallest selectable radius, used by default for voice-created lists. */
private const val SMALLEST_RADIUS_METERS = 50f

@HiltViewModel
class QuickCreateViewModel @Inject constructor(
    private val groceryRepository: GroceryRepository,
    private val locationSearchRepository: LocationSearchRepository
) : ViewModel() {

    sealed interface State {
        data object Loading : State
        data class Created(val listId: String) : State
        data class Failed(val query: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    private var started = false

    /**
     * Geocodes [store] via OpenStreetMap, then creates a reminder list at the first match with the
     * smallest radius. Invoked once per screen (e.g. from a "Hey Google, maak een boodschappenlijst
     * voor …" App Action, or the geogrocery://create?store= deep link).
     */
    fun start(store: String) {
        if (started) return
        started = true
        viewModelScope.launch {
            val query = store.trim()
            if (query.isBlank()) {
                _state.value = State.Failed(query)
                return@launch
            }
            val first = locationSearchRepository.search(query).getOrNull()?.firstOrNull()
            if (first == null) {
                _state.value = State.Failed(query)
                return@launch
            }
            val listId = UUID.randomUUID().toString()
            groceryRepository.saveList(
                GroceryList(
                    listId = listId,
                    title = query.replaceFirstChar { it.uppercase() },
                    isCompleted = false,
                    reminderEnabled = true,
                    location = StoreLocation(
                        locationName = first.primaryName,
                        address = first.displayName,
                        latitude = first.latitude,
                        longitude = first.longitude,
                        radiusMeters = SMALLEST_RADIUS_METERS
                    ),
                    createdAt = System.currentTimeMillis()
                )
            )
            _state.value = State.Created(listId)
        }
    }
}
