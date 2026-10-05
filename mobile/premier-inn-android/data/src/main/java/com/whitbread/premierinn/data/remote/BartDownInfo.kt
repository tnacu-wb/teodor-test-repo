package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

data class BartDownInfo (
    @SerializedName("bartDowntimeTitle") val bartDowntimeTitle: String,
    @SerializedName("bartDowntimeDescription") val bartDowntimeDescription: String,
    @SerializedName("bartDowntimeMakeBooking") val bartDowntimeMakeBooking: String,
    @SerializedName("bartDowntimeExistingBooking") val bartDowntimeExistingBooking: String
)