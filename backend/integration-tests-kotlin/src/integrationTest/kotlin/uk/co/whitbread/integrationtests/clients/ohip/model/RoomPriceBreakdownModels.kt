package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

data class RoomPriceBreakdownRequest(
    val hotelId: String,
    val arrivalDate: LocalDate,
    val departureDate: LocalDate,
    val ratePlanCode: String,
    val roomTypes: List<String>,
    val adultsNo: List<Int>,
    val childrenNo: List<Int>,
)

@Serializable
data class RoomPriceBreakdownResponse(
    val priceBreakdown: List<RoomPriceBreakdown> = emptyList(),
)

@Serializable
data class RoomPriceBreakdown(
    val totalNetAmount: Double? = null,
    val totalGrossAmount: Double? = null,
    val totalTaxAmount: Double? = null,
    val baseRateAmount: Double? = null,
    val effectiveRateAmount: Double? = null,
    val currencyCode: String? = null,
    val dailyPrices: List<RoomPriceBreakdownDailyPrice> = emptyList(),
)

@Serializable
data class RoomPriceBreakdownDailyPrice(
    val date: String? = null,
    val netPrice: Double? = null,
    val grossPrice: Double? = null,
    val effectiveRate: Double? = null,
)
