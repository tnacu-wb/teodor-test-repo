package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class CreateReservationRequestBody(
    val reservations: List<Reservations>,
    val bookingChannel: BookingChannelDetails
)

data class Reservations(
    val hotelId: String,
    val arrival: String,
    val departure: String,
    val basketReferenceId: String,
    val adultsNumber: Int,
    val childrenNumber: Int,
    val cotRequired: Boolean,
    val roomRates: RoomRates,
    val reservationPackages : List<ReservationPackage>?
)

data class RoomRates(
    val startDate: String,
    val endDate: String,
    val pmsRoomType: String,
    val specialRequests: List<String>?,
    val ratePlanCode: String,
    val promotionCode: String?,
    val promoKind: String?
)

data class ReservationPackage(
    val unitPrice: Float,
    val quantity: Int,
    val packageCode: String,
    val startDate: String,
    val endDate: String
)