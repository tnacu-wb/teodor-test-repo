package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class HoldBookingRequestBody(
    val createReservationRequestBody: CreateReservationRequestBody,
    val packagesRequestBody: HotelPackagesRequestBody,
    val country: String,
    val language: String,
    val bookingChannel: BookingChannelDetails
)
