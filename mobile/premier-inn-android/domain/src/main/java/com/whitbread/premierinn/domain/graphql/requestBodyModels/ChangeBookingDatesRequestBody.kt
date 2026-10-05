package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class ChangeBookingDatesRequestBody(
    val tempBookingRef: String,
    val newStartDate: String,
    val newEndDate: String,
    val token: String,
    val bookingChannel: BookingChannelDetails
)
