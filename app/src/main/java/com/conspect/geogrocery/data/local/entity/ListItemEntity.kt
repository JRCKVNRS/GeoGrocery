package com.conspect.geogrocery.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A single checklist item belonging to a [GroceryListEntity]. Deleting the parent list
 * cascades to its items.
 */
@Entity(
    tableName = "list_items",
    foreignKeys = [
        ForeignKey(
            entity = GroceryListEntity::class,
            parentColumns = ["listId"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId")]
)
data class ListItemEntity(
    @PrimaryKey val itemId: String = UUID.randomUUID().toString(),
    val listId: String,
    val text: String,
    val isDone: Boolean = false
)
