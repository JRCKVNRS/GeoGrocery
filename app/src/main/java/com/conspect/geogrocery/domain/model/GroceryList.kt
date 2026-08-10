package com.conspect.geogrocery.domain.model

data class GroceryList(
    val listId: String,
    val title: String,
    val isCompleted: Boolean,
    val reminderEnabled: Boolean,
    val location: StoreLocation,
    val createdAt: Long,
    val items: List<ListItem> = emptyList()
) {
    val openItems: List<ListItem> get() = items.filterNot { it.isDone }
    val doneCount: Int get() = items.count { it.isDone }

    /** A geofence should be armed only while the reminder is on and the list is not done. */
    val geofenceShouldBeActive: Boolean get() = reminderEnabled && !isCompleted
}

data class ListItem(
    val itemId: String,
    val listId: String,
    val text: String,
    val isDone: Boolean
)

data class StoreLocation(
    val locationName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float = 150f
)
