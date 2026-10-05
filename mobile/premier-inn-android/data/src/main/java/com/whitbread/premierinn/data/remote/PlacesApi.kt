package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface PlacesApi {
    @GET("places/{placeid}")
    fun location(@Path("placeid") placeId: String,
                 @Query("fields") fields: String = "location",
                 @Query("key") apiKey: String,
                 @Header("X-Android-Package") packageName: String,
                 @Header("X-Android-Cert") packageSignature: String,): Single<PlacesApiResponse>




    data class PlacesApiResponse(@SerializedName("location") val location: Location?)

    data class Location(@SerializedName("latitude") val lat: Double,
                        @SerializedName("longitude") val lon: Double)
}