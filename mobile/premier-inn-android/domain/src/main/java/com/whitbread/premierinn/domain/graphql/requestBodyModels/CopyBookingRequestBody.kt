package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class CopyBookingRequestBody(
    val originalBasketReference: String,
    val token: String,
    val bookingChannel: BookingChannelDetails
)