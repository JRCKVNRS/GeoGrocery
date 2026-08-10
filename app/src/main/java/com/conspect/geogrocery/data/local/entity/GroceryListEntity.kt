package com.conspect.geogrocery.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A location-linked grocery list. When [reminderEnabled] is true and the list is not
 * [isCompleted], a geofence is registered for ([latitude], [longitude], [radiusMeters]).
 */
@Entity(tableName = "grocery_lists")
data class GroceryListEntity(
    @PrimaryKey val listId: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val reminderEnabled: Boolean = true,
    val locationName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float = 150f,
    val createdAt: Long = System.currentTimeMillis()
)
