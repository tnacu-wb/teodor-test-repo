package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

data class RateCodePricingRequest(
    val hotelId: String,
    val arrivalDate: LocalDate,
    val departureDate: LocalDate,
    val ratePlanCode: String,
    val roomTypes: List<String>,
    val adultsNo: List<Int>,
    val childrenNo: List<Int>,
)

@Serializable
data class RateCodePricingResponse(
    val ratePlanCode: String? = null,
    val totalNetAmount: Double? = null,
    val currencyCode: String? = null,
)
