package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

/**
 *
 */
data class SearchTopDestinationItem(
        @SerializedName("name") val name: String,
        @SerializedName("lat") val lat: Float,
        @SerializedName("long") val long: Float)




