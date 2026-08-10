package com.conspect.geogrocery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.conspect.geogrocery.data.local.entity.GroceryListEntity
import com.conspect.geogrocery.data.local.entity.ListItemEntity

@Database(
    entities = [GroceryListEntity::class, ListItemEntity::class],
    version = 1,
    exportSchema = true
)
abstract class GeoGroceryDatabase : RoomDatabase() {
    abstract fun groceryDao(): GroceryDao

    companion object {
        const val NAME = "geogrocery.db"
    }
}
