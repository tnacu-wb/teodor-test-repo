package com.whitbread.premierinn.api.response.booking

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Deprecated("This has to removed once " +
        "com.whitbread.premierinn.api.response.booking.BookingRatePlan is refactored")

@Parcelize
data class BookingPrice(
    @SerializedName("amount") val amount: Float,
    @SerializedName("currency") val currency: String
): Parcelable
