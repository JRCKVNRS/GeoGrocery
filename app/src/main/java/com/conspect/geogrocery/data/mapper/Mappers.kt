package com.conspect.geogrocery.data.mapper

import com.conspect.geogrocery.data.local.entity.GroceryListEntity
import com.conspect.geogrocery.data.local.entity.GroceryListWithItems
import com.conspect.geogrocery.data.local.entity.ListItemEntity
import com.conspect.geogrocery.domain.model.GroceryList
import com.conspect.geogrocery.domain.model.ListItem
import com.conspect.geogrocery.domain.model.StoreLocation

fun GroceryListWithItems.toDomain(): GroceryList = GroceryList(
    listId = list.listId,
    title = list.title,
    isCompleted = list.isCompleted,
    reminderEnabled = list.reminderEnabled,
    location = StoreLocation(
        locationName = list.locationName,
        address = list.address,
        latitude = list.latitude,
        longitude = list.longitude,
        radiusMeters = list.radiusMeters
    ),
    createdAt = list.createdAt,
    items = items.map { it.toDomain() }
)

fun GroceryListEntity.toDomain(): GroceryList = GroceryList(
    listId = listId,
    title = title,
    isCompleted = isCompleted,
    reminderEnabled = reminderEnabled,
    location = StoreLocation(locationName, address, latitude, longitude, radiusMeters),
    createdAt = createdAt,
    items = emptyList()
)

fun GroceryList.toEntity(): GroceryListEntity = GroceryListEntity(
    listId = listId,
    title = title,
    isCompleted = isCompleted,
    reminderEnabled = reminderEnabled,
    locationName = location.locationName,
    address = location.address,
    latitude = location.latitude,
    longitude = location.longitude,
    radiusMeters = location.radiusMeters,
    createdAt = createdAt
)

fun ListItemEntity.toDomain(): ListItem = ListItem(itemId, listId, text, isDone)

fun ListItem.toEntity(): ListItemEntity = ListItemEntity(itemId, listId, text, isDone)
