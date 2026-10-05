package com.whitbread.premierinn.domain.graphql.requestBodyModels


data class AddNewRoomRequestBody(
    val bookingChannel: BookingChannelDetails,
    val tempBookingRef: String,
    val roomOccupancy: RoomOccupancyAmend,
    val leadGuest: LeadGuestAmend,
    val roomType: String,
    val token: String,
    val ratePlanCode: String
)