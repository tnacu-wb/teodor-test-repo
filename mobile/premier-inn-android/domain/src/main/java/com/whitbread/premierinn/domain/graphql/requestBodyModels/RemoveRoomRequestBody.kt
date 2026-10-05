package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class RemoveRoomRequestBody(
    val tempBookingRef: String,
    val reservationId: String,
    val token: String,
    val bookingChannel: BookingChannelDetails
)