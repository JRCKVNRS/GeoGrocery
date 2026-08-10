package com.conspect.geogrocery.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * One result from the Nominatim `/search` endpoint. Only the fields the app needs are mapped;
 * Moshi ignores the rest.
 */
@JsonClass(generateAdapter = true)
data class NominatimResultDto(
    @Json(name = "place_id") val placeId: Long?,
    @Json(name = "display_name") val displayName: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "lat") val lat: String?,
    @Json(name = "lon") val lon: String?,
    @Json(name = "address") val address: AddressDto?
)

@JsonClass(generateAdapter = true)
data class AddressDto(
    @Json(name = "shop") val shop: String?,
    @Json(name = "amenity") val amenity: String?,
    @Json(name = "supermarket") val supermarket: String?,
    @Json(name = "road") val road: String?,
    @Json(name = "house_number") val houseNumber: String?,
    @Json(name = "postcode") val postcode: String?,
    @Json(name = "city") val city: String?,
    @Json(name = "town") val town: String?,
    @Json(name = "village") val village: String?
)
