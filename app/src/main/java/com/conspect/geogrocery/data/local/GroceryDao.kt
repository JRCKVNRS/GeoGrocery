package com.conspect.geogrocery.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.conspect.geogrocery.data.local.entity.GroceryListEntity
import com.conspect.geogrocery.data.local.entity.GroceryListWithItems
import com.conspect.geogrocery.data.local.entity.ListItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {

    // ---- Lists ----

    @Transaction
    @Query("SELECT * FROM grocery_lists ORDER BY isCompleted ASC, createdAt DESC")
    fun observeListsWithItems(): Flow<List<GroceryListWithItems>>

    @Transaction
    @Query("SELECT * FROM grocery_lists WHERE listId = :listId")
    fun observeListWithItems(listId: String): Flow<GroceryListWithItems?>

    @Transaction
    @Query("SELECT * FROM grocery_lists WHERE listId = :listId")
    suspend fun getListWithItems(listId: String): GroceryListWithItems?

    /** Lists that should currently have an active geofence. Used on boot re-registration. */
    @Query("SELECT * FROM grocery_lists WHERE reminderEnabled = 1 AND isCompleted = 0")
    suspend fun getActiveGeofenceLists(): List<GroceryListEntity>

    @Query("SELECT * FROM grocery_lists WHERE listId = :listId")
    suspend fun getList(listId: String): GroceryListEntity?

    @Upsert
    suspend fun upsertList(list: GroceryListEntity)

    @Update
    suspend fun updateList(list: GroceryListEntity)

    @Delete
    suspend fun deleteList(list: GroceryListEntity)

    @Query("DELETE FROM grocery_lists WHERE listId = :listId")
    suspend fun deleteListById(listId: String)

    // ---- Items ----

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItem(item: ListItemEntity)

    @Update
    suspend fun updateItem(item: ListItemEntity)

    @Delete
    suspend fun deleteItem(item: ListItemEntity)

    @Query("UPDATE list_items SET isDone = :isDone WHERE itemId = :itemId")
    suspend fun setItemDone(itemId: String, isDone: Boolean)
}
