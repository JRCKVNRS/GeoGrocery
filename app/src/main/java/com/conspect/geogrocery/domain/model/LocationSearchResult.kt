package com.conspect.geogrocery.domain.model

/** A place returned by the location search, ready to be linked to a list. */
data class LocationSearchResult(
    val displayName: String,
    val primaryName: String,
    val latitude: Double,
    val longitude: Double
)
