package com.conspect.geogrocery.geofence

object GeofenceConstants {
    /** Action for the explicit broadcast we send from Location Services' PendingIntent. */
    const val ACTION_GEOFENCE_EVENT = "com.conspect.geogrocery.action.GEOFENCE_EVENT"

    /** How long a geofence may take to trigger after entry, in ms (0 = as soon as possible). */
    const val NOTIFICATION_RESPONSIVENESS_MS = 0

    /** Deep link template opened when a notification is tapped. */
    const val DEEP_LINK_PREFIX = "geogrocery://list/"

    const val NOTIFICATION_CHANNEL_ID = "geofence_reminders"

    /** Max open items previewed in a notification body. */
    const val PREVIEW_ITEM_COUNT = 3
}
