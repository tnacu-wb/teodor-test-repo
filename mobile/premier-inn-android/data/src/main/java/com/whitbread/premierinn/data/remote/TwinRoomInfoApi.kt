package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

data class TwinRoomInfoApi(
    @SerializedName("lettingType") val lettingType: String,
    @SerializedName("label") val label: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String
)