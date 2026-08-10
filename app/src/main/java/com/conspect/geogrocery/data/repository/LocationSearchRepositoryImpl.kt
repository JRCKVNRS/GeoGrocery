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
        val primary = name?.takeIf { it.isNotBlank() }
            ?: display.substringBefore(",").trim()
        return LocationSearchResult(
            displayName = display,
            primaryName = primary,
            latitude = latitude,
            longitude = longitude
        )
    }
}
