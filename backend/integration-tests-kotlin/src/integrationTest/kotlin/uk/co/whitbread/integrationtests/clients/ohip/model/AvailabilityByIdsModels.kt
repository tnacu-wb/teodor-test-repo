package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

/** Query model for `GET /ohip/hotels/availabilities/distr` (distribution multi-hotel search). */
data class AvailabilityByIdsRequest(
    val hotelIds: List<String>,
    val arrivalDate: LocalDate,
    val departureDate: LocalDate,
    val roomTypes: List<String>,
    val adults: List<Int>,
    val children: List<Int>,
    val cotsRequired: List<Boolean>,
    val channel: String,
    val subchannel: String,
    val language: String,
    val ratePlanCodes: List<String> = emptyList(),
    val globalCompanyId: String? = null,
    val negotiatedRateDisplaySets: List<String> = emptyList(),
    val pmsRoomTypes: List<String> = emptyList(),
)

@Serializable
data class AvailabilityByIdsResponse(
    val hotelAvailability: List<HotelAvailabilityResponse> = emptyList(),
)
