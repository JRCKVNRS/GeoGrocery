package com.conspect.geogrocery.data.remote

import com.conspect.geogrocery.data.remote.dto.NominatimResultDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {

    /**
     * Free OpenStreetMap geocoding search. No API key required.
     * A unique `User-Agent` header is attached by an OkHttp interceptor as required by the
     * Nominatim Usage Policy.
     */
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("limit") limit: Int = 5
    ): List<NominatimResultDto>

    companion object {
        const val BASE_URL = "https://nominatim.openstreetmap.org/"
    }
}
