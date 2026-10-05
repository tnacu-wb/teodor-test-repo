package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class FindBookingRequestBody(
        val resNo : String,
        val lastName: String,
        val arrivalDate: String,
        val language: String,
        val country: String,
        val bookingChannel: BookingChannelDetails
)