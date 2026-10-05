package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class CancelInformationRequestBody(
    val basketReference: String,
    val hotelId: String,
    val userDateTime: String,
    val token: String,
    val bookingChannel: BookingChannelDetails
)