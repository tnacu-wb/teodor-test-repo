package com.whitbread.premierinn.api.response.availability

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Restaurant(
        @SerializedName("name") val name: String,
        @SerializedName("image") val imagePath: String,
        @SerializedName("description") val description: String,
        @SerializedName("menus") val menus: List<Menu>?) : Parcelable {

        val hasDisclaimer: Boolean
            get() = menus != null && menus.isNotEmpty() && menus[0].disclaimer.isNotBlank()
}