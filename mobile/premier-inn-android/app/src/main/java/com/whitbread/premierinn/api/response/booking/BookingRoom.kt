package com.whitbread.premierinn.api.response.booking

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Deprecated(message = "Use com.whitbread.premierinn.data.remote.BookingRoom")
@Parcelize
data class BookingRoom(
        @SerializedName("roomNumber") val roomNumber: Int,
        @SerializedName("dailyRates") val dailyRates: List<DailyRate>,
        @SerializedName("type") val type: String,
        // TODO Change the lettingType type from String to LettingType
        @SerializedName("lettingType") val lettingType: String,
        @SerializedName("adults") val adults: Int,
        @SerializedName("children") val children: Int,
        @SerializedName("cotRequired") val cot: Boolean,
        @SerializedName("totalCost") val totalCost: BookingPrice,
        @SerializedName("cityTax") val cityTax: BookingPrice?,
        @SerializedName("alternativeRooms") val alternativeRooms: List<AlternativeRoom>?,
        @SerializedName("status") val status: Status?): Parcelable {

    val hasAlternativeRooms: Boolean
        get() = !alternativeRooms.isNullOrEmpty()

    enum class Status {
        @SerializedName("ROOM_TYPE_GUARANTEED") ROOM_TYPE_GUARANTEED,
        @SerializedName("ALTERNATIVE_ROOM_TYPE_OFFERED") ALTERNATIVE_ROOM_TYPE_OFFERED
    }
}