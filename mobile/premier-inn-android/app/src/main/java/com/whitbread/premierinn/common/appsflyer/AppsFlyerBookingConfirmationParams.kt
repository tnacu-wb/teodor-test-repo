package com.whitbread.premierinn.common.appsflyer

data class AppsFlyerBookingConfirmationParams(
    val bookingReference: String,
    val revenue: Double,
    val currency: String,
    val hotelCode: String,
    val hotel: String
) {
    constructor(bookingReference: String, revenue: Double, currency: String, hotelCode: String) : this(
        bookingReference,
        revenue,
        currency,
        hotelCode,
        "hotel"
    )
}