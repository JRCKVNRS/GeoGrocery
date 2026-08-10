package com.conspect.geogrocery.domain.repository

import com.conspect.geogrocery.domain.model.LocationSearchResult

interface LocationSearchRepository {
    /** Autocomplete search against OpenStreetMap. Returns an empty list on empty/blank query. */
    suspend fun search(query: String): Result<List<LocationSearchResult>>
}
