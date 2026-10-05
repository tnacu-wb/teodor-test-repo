package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

class SnowDropApiContract {
    class AutocompleteSuggestions(@SerializedName("properties") val properties: List<Property>,
                                  @SerializedName("managedPlaces") val managedPlaces: List<ManagedPlace>,
                                  @SerializedName("places") val places: List<GooglePlace>)

    class Property(@SerializedName("code") val hotelCode: String,
                   @SerializedName("brand") val brand: String?,
                   @SerializedName("suggestion") val name: String,
                   @SerializedName("geometry") val location: Geometry)

    class ManagedPlace(@SerializedName("suggestion") val name: String,
                       @SerializedName("geometry") val location: Geometry?)

    class GooglePlace(@SerializedName("suggestion") val name: String,
                      @SerializedName("placeId") val placeId: String?)

    class Geometry(@SerializedName("coordinates") val coordinates: List<Double>)
}