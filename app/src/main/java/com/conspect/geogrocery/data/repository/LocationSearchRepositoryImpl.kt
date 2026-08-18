package com.conspect.geogrocery.data.repository

import com.conspect.geogrocery.data.remote.NominatimApi
import com.conspect.geogrocery.data.remote.dto.NominatimResultDto
import com.conspect.geogrocery.di.IoDispatcher
import com.conspect.geogrocery.domain.model.LocationSearchResult
import com.conspect.geogrocery.domain.repository.LocationSearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationSearchRepositoryImpl @Inject constructor(
    private val api: NominatimApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocationSearchRepository {

    override suspend fun search(query: String): Result<List<LocationSearchResult>> {
        if (query.isBlank()) return Result.success(emptyList())
        return withContext(ioDispatcher) {
            runCatching {
                api.search(query = query.trim())
                    .mapNotNull { it.toDomainOrNull() }
            }
        }
    }

    private fun NominatimResultDto.toDomainOrNull(): LocationSearchResult? {
        val latitude = lat?.toDoubleOrNull() ?: return null
        val longitude = lon?.toDoubleOrNull() ?: return null
        val display = displayName ?: return null
        return LocationSearchResult(
            displayName = display,
            primaryName = buildPrimaryName(display),
            latitude = latitude,
            longitude = longitude
        )
    }

    /**
     * A concise label for the top line of a result. Prefers a shop/venue name, otherwise the
     * street + house number (e.g. "Vriendschap 7"), falling back to the first part of the address.
     * The full address is always kept in [LocationSearchResult.displayName].
     */
    private fun NominatimResultDto.buildPrimaryName(display: String): String {
        name?.takeIf { it.isNotBlank() }?.let { return it }
        val a = address
        if (a != null) {
            val venue = a.shop ?: a.amenity ?: a.supermarket
            if (!venue.isNullOrBlank()) return venue
            val road = a.road?.takeIf { it.isNotBlank() }
            val houseNumber = a.houseNumber?.takeIf { it.isNotBlank() }
            when {
                road != null && houseNumber != null -> return "$road $houseNumber"
                road != null -> return road
            }
        }
        return display.substringBefore(",").trim()
    }
}
