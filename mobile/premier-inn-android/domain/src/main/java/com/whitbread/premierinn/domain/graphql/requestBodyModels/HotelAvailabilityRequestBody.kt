package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class HotelAvailabilityRequestBody(
    val arrival: String,
    val departure: String,
    val hotel: HotelInfoDetails,
    val rooms: List<RoomSearch>,
    val bookingChannel: BookingChannelDetails,
    val brand: String,
    val ratePlanCodes: List<String>?,
    val companyId: String?
    )

data class HotelInfoDetails(
    val identifier: String
)
data class RoomSearch(
    val adultsNumber: Int,
    val childrenNumber : Int,
    val cotRequired: Boolean,
    val roomType: String
)

