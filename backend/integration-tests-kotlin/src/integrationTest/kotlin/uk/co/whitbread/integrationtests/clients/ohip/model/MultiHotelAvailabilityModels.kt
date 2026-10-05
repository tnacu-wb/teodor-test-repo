package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

/** Query model for `GET /ohip/hotels/availabilities` (multi-hotel PBN/PBF search). */
data class MultiHotelAvailabilityRequest(
    val hotelIds: List<String>,
    val arrivalDate: LocalDate,
    val departureDate: LocalDate,
    val numberOfRooms: List<Int>,
    val roomTypes: List<String>,
    val adults: List<Int>,
    val children: List<Int>,
    val cotsRequired: List<Boolean>,
    val channel: String,
    val companyId: String? = null,
)

@Serializable
data class MultiHotelAvailabilityResponse(
    val timestamp: String? = null,
    val hotelAvailabilityResults: List<MultiHotelAvailabilityResult> = emptyList(),
)

@Serializable
data class MultiHotelAvailabilityResult(
    val hotelId: String? = null,
    val available: Boolean? = null,
    val arrivalDate: String? = null,
    val departureDate: String? = null,
    val roomTypes: List<MultiHotelAvailabilityRoomType> = emptyList(),
)

@Serializable
data class MultiHotelAvailabilityRoomType(
    val roomType: String? = null,
    val numberOfRooms: Int? = null,
    val adults: String? = null,
    val children: String? = null,
    val cotRequested: String? = null,
    val roomRates: List<MultiHotelAvailabilityRoomRate> = emptyList(),
)

@Serializable
data class MultiHotelAvailabilityRoomRate(
    val ratePlan: String? = null,
    val roomType: String? = null,
    val currency: String? = null,
    val totalPrice: Double? = null,
)
