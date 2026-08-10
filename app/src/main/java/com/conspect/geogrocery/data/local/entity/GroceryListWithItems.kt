package com.conspect.geogrocery.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/** A list together with all of its checklist items, resolved in a single query. */
data class GroceryListWithItems(
    @Embedded val list: GroceryListEntity,
    @Relation(
        parentColumn = "listId",
        entityColumn = "listId"
    )
    val items: List<ListItemEntity>
)
