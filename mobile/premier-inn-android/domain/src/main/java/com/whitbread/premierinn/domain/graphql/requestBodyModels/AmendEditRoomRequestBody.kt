package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class AmendEditRoomRequestBody(
    val tempBookingRef: String,
    val reservationId: String,
    val bookingChannel: BookingChannelDetails,
    val roomOccupancy: RoomOccupancyAmend,
    val leadGuest: LeadGuestAmend,
    val roomType: String,
    val token: String
)