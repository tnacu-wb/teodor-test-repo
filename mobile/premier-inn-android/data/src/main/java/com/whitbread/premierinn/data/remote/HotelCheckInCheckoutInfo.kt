package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName

data class HotelCheckInCheckoutInfo(
    @SerializedName("bookingDetailsCheckInInfo") val bookingDetailsCheckInInfo: String,
    @SerializedName("bookingDetailsCheckOutInfo") val bookingDetailsCheckOutInfo: String,
    @SerializedName("summaryOrPaymentBreakdownCheckInInfo") val summaryOrPaymentBreakdownCheckInInfo: String,
    @SerializedName("summaryOrPaymentBreakdownCheckOutInfo") val summaryOrPaymentBreakdownCheckOutInfo: String
)

data class AllCheckInTimesInfo(
    @SerializedName("ukCheckInTimes") val ukCheckInTimes: HotelCheckInCheckoutInfo,
    @SerializedName("germanyCheckInTimes") val germanyCheckInTimes: HotelCheckInCheckoutInfo
)