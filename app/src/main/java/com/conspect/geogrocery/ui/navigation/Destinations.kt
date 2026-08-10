package com.conspect.geogrocery.ui.navigation

import com.conspect.geogrocery.geofence.GeofenceConstants

object Routes {
    const val LISTS = "lists"
    const val CREATE_LIST = "create_list"
    const val LIST_DETAIL = "list_detail"
    const val ARG_LIST_ID = "listId"

    fun detail(listId: String) = "$LIST_DETAIL/$listId"

    /** Matches the notification deep link geogrocery://list/{listId}. */
    const val DETAIL_DEEP_LINK = "${GeofenceConstants.DEEP_LINK_PREFIX}{$ARG_LIST_ID}"
}
