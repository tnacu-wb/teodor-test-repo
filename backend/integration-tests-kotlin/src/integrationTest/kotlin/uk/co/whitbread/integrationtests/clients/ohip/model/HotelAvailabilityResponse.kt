package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class HotelAvailabilityResponse(
    val timestamp: String? = null,
    val hotelId: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val available: Boolean = false,
    val limitedAvailability: Boolean = false,
    val roomRates: List<HotelAvailabilityRoomRate> = emptyList(),
    val substitutionList: List<HotelAvailabilityRoomSubstitution> = emptyList(),
)

@Serializable
data class HotelAvailabilityRoomRate(
    val ratePlanCode: String? = null,
    val promotionCode: String? = null,
    val roomTypes: List<HotelAvailabilityRoomType> = emptyList(),
)

@Serializable
data class HotelAvailabilityRoomType(
    val roomType: String? = null,
    val adults: Int? = null,
    val children: Int? = null,
    val cotRequested: Boolean? = null,
    val rooms: List<HotelAvailabilityRoom> = emptyList(),
)

@Serializable
data class HotelAvailabilityRoom(
    val pmsRoomType: String? = null,
    val silentSubstitution: Boolean? = null,
    val roomClass: String? = null,
    val cotAvailable: Boolean? = null,
    val specialRequests: List<String> = emptyList(),
    val roomPriceBreakdown: HotelAvailabilityPriceBreakdown? = null,
    val mealsIncluded: HotelAvailabilityMealsIncluded? = null,
    val numberOfRoomsAvailable: Int? = null,
)

@Serializable
data class HotelAvailabilityPriceBreakdown(
    val totalNetAmount: Double? = null,
    val totalGrossAmount: Double? = null,
    val totalTaxAmount: Double? = null,
    val baseRateAmount: Double? = null,
    val effectiveRateAmount: Double? = null,
    val currencyCode: String? = null,
    val dailyPrices: List<HotelAvailabilityDailyPrice> = emptyList(),
)

@Serializable
data class HotelAvailabilityDailyPrice(
    val date: String? = null,
    val netPrice: Double? = null,
    val grossPrice: Double? = null,
    val effectiveRate: Double? = null,
)

@Serializable
data class HotelAvailabilityMealsIncluded(
    val mealName: String? = null,
    val breakfast: Boolean = false,
    val dinner: Boolean = false,
)

@Serializable
data class HotelAvailabilityRoomSubstitution(
    val type: String? = null,
    val silent: Boolean? = null,
    val specialRequest: String? = null,
    val accessibleSpecialRequest: String? = null,
    val codePackage: String? = null,
)
