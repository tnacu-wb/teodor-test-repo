package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class ConfirmAmendLogicRequestBody(
    val bookingChannel: BookingChannelDetails,
    val tempBookingRef: String,
    val originalBookingRef: String,
    val token: String,
    val paymentOptionSelected: String,
    val environment: String
)