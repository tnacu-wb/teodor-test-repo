package com.whitbread.premierinn.data.remote

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 */
interface SearchItemApi {

    @GET("v1/autocomplete")
    fun search(
        @Query("input") query: String,
        @Query("gplaces[components]") googlePlacesFilter: String = "country:uk|country:de",
        @Query("hotels.limit") numberOfHotels: Int = 5
    ): Single<SnowDropApiContract.AutocompleteSuggestions>
}
