package com.conspect.geogrocery.data.repository

import com.conspect.geogrocery.data.local.GroceryDao
import com.conspect.geogrocery.data.mapper.toDomain
import com.conspect.geogrocery.data.mapper.toEntity
import com.conspect.geogrocery.di.IoDispatcher
import com.conspect.geogrocery.domain.model.GroceryList
import com.conspect.geogrocery.domain.model.ListItem
import com.conspect.geogrocery.domain.repository.GroceryRepository
import com.conspect.geogrocery.geofence.GeofenceManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroceryRepositoryImpl @Inject constructor(
    private val dao: GroceryDao,
    private val geofenceManager: GeofenceManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GroceryRepository {

    override fun observeLists(): Flow<List<GroceryList>> =
        dao.observeListsWithItems().map { rows -> rows.map { it.toDomain() } }

    override fun observeList(listId: String): Flow<GroceryList?> =
        dao.observeListWithItems(listId).map { it?.toDomain() }

    override suspend fun getList(listId: String): GroceryList? = withContext(ioDispatcher) {
        dao.getListWithItems(listId)?.toDomain()
    }

    override suspend fun saveList(list: GroceryList) = withContext(ioDispatcher) {
        dao.upsertList(list.toEntity())
        syncGeofence(list)
    }

    override suspend fun deleteList(listId: String) = withContext(ioDispatcher) {
        geofenceManager.remove(listId)
        dao.deleteListById(listId)
    }

    override suspend fun setReminderEnabled(listId: String, enabled: Boolean) =
        withContext(ioDispatcher) {
            val entity = dao.getList(listId) ?: return@withContext
            val updated = entity.copy(reminderEnabled = enabled)
            dao.updateList(updated)
            syncGeofence(updated.toDomain())
        }

    override suspend fun setCompleted(listId: String, completed: Boolean) =
        withContext(ioDispatcher) {
            val entity = dao.getList(listId) ?: return@withContext
            val updated = entity.copy(isCompleted = completed)
            dao.updateList(updated)
            // Completing a list immediately disarms its geofence per requirement.
            syncGeofence(updated.toDomain())
        }

    override suspend fun addItem(listId: String, text: String) = withContext(ioDispatcher) {
        dao.upsertItem(
            com.conspect.geogrocery.data.local.entity.ListItemEntity(
                itemId = UUID.randomUUID().toString(),
                listId = listId,
                text = text.trim(),
                isDone = false
            )
        )
    }

    override suspend fun updateItem(item: ListItem) = withContext(ioDispatcher) {
        dao.updateItem(item.toEntity())
    }

    override suspend fun setItemDone(itemId: String, isDone: Boolean) = withContext(ioDispatcher) {
        dao.setItemDone(itemId, isDone)
    }

    override suspend fun deleteItem(item: ListItem) = withContext(ioDispatcher) {
        dao.deleteItem(item.toEntity())
    }

    override suspend fun refreshAllGeofences() = withContext(ioDispatcher) {
        val lists = dao.getActiveGeofenceLists().map { it.toDomain() }
        geofenceManager.registerAll(lists)
        Unit
    }

    /** Arms or disarms the geofence to match the list's current desired state. */
    private suspend fun syncGeofence(list: GroceryList) {
        if (list.geofenceShouldBeActive) {
            geofenceManager.register(list)
        } else {
            geofenceManager.remove(list.listId)
        }
    }
}
