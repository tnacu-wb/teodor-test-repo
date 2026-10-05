package com.whitbread.premierinn.api.response.availability

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Menu(
        @SerializedName("name") val name: String,
        @SerializedName("path") val path: String,
        @SerializedName("image") val image: String,
        @SerializedName("description") val description: String,
        @SerializedName("disclaimer") val disclaimer: String
) : Parcelable