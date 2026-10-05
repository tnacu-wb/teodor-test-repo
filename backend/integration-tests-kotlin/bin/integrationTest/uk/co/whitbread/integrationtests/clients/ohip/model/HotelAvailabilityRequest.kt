package uk.co.whitbread.integrationtests.clients.ohip.model

import java.time.LocalDate

data class HotelAvailabilityRequest(
    val hotelId: String,
    val arrivalDate: LocalDate,
    val departureDate: LocalDate,
    val roomTypes: List<String>,
    val adults: List<Int>,
    val children: List<Int>,
    val cotsRequired: List<Boolean>,
    val channel: String,
    val subchannel: String,
    val language: String? = null,
    val companyId: String? = null,
    val promotionCode: String? = null,
)
