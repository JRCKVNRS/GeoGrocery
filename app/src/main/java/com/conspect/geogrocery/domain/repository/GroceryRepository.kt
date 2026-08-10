package com.conspect.geogrocery.domain.repository

import com.conspect.geogrocery.domain.model.GroceryList
import com.conspect.geogrocery.domain.model.ListItem
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for lists and items. Implementations are responsible for keeping the
 * geofence registration in sync whenever a change affects [GroceryList.geofenceShouldBeActive].
 */
interface GroceryRepository {

    fun observeLists(): Flow<List<GroceryList>>

    fun observeList(listId: String): Flow<GroceryList?>

    suspend fun getList(listId: String): GroceryList?

    /** Creates or updates a list and (re)registers/removes its geofence accordingly. */
    suspend fun saveList(list: GroceryList)

    suspend fun deleteList(listId: String)

    /** Toggles the per-list reminder and arms/disarms the geofence. */
    suspend fun setReminderEnabled(listId: String, enabled: Boolean)

    /** Marks a list done/not-done. Completing a list immediately disables its geofence. */
    suspend fun setCompleted(listId: String, completed: Boolean)

    suspend fun addItem(listId: String, text: String)

    suspend fun updateItem(item: ListItem)

    suspend fun setItemDone(itemId: String, isDone: Boolean)

    suspend fun deleteItem(item: ListItem)

    /** Re-registers all geofences that should be active (invoked after device reboot). */
    suspend fun refreshAllGeofences()
}
