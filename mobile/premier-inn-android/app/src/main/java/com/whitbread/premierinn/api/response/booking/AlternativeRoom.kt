package com.whitbread.premierinn.api.response.booking

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Deprecated(message = "Use com.whitbread.premierinn.data.remote.AlternativeRoom")
@Parcelize
data class AlternativeRoom(@SerializedName("dailyRates") val dailyRates: List<DailyRate>?,
                           @SerializedName("lettingType") val lettingType: String?,
                           @SerializedName("totalCost") val totalCost: BookingPrice?,
                           @SerializedName("cityTax") val cityTax: BookingPrice?,
                           @SerializedName("alternativeType") val alternativeType: AlternativeType?,
                           @SerializedName("numberAvailable") val numberAvailable: Int?): Parcelable {
    enum class AlternativeType {
        @SerializedName("ACCESSIBLE")
        ACCESSIBLE,
        @SerializedName("ROOM_UPSELL")
        UPSELL
    }
}