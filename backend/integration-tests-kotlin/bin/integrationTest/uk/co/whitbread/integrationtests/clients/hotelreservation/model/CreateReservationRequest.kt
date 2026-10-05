package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateReservationRequest(
    val reservations: List<CreateReservationRoomRequest>,
    val bookingChannel: CreateReservationBookingChannel,
    val bookingFlowId: String? = null,
    val getReservationsByIds: Boolean = false,
    val isOta: Boolean = false,
)

@Serializable
data class CreateReservationRoomRequest(
    val hotelId: String,
    val arrival: String,
    val departure: String,
    val adultsNumber: Int,
    val childrenNumber: Int = 0,
    val cotRequired: Boolean = false,
    val roomRates: CreateReservationRoomRateRequest,
    val reservationPackages: List<CreateReservationPackageRequest> = emptyList(),
)

@Serializable
data class CreateReservationRoomRateRequest(
    val ratePlanCode: String,
    val pmsRoomType: String,
    val specialRequests: List<String> = emptyList(),
    val startDate: String,
    val endDate: String,
    val promotionCode: String? = null,
    val promoKind: String? = null,
)

@Serializable
data class CreateReservationPackageRequest(
    val unitPrice: Double,
    val quantity: Int,
    val packageCode: String,
    val startDate: String,
    val endDate: String,
)

@Serializable
data class CreateReservationBookingChannel(
    val channel: String,
    val subchannel: String,
    val language: String? = null,
)
