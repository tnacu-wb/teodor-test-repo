package com.whitbread.premierinn.domain.graphql.findBooking.entity

data class FindBookingDomain(
    val sourcePms: String,
    val bookingReference: String,
    val uuidBasketReference: String,
    val token: String?,
    val hotelId: String,
    val isThirdPartyBooking: Boolean
)