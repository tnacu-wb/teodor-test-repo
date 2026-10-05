package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class AvailabilityBookingChannel(
    val channel: String,
    val subchannel: String,
    val language: String? = null,
)

@Serializable
data class AvailabilityRoomV2(
    val tag: String,
    val roomTypes: List<String> = emptyList(),
    val adults: Int? = null,
    val children: Int? = null,
    val numberOfRooms: Int? = null,
)

/** Body for `POST /ohip/v2/hotels/availabilities` (minimum-rate multi-hotel summary). */
@Serializable
data class MultiHotelAvailabilityV2Request(
    val bookingChannel: AvailabilityBookingChannel,
    val hotelIds: List<String>,
    val arrivalDate: String,
    val departureDate: String,
    val rooms: List<AvailabilityRoomV2>,
)

@Serializable
data class MultiHotelAvailabilityV2Response(
    val hotelAvailabilityResults: List<MinimumRateHotelResult> = emptyList(),
)

@Serializable
data class MinimumRateHotelResult(
    val hotelId: String? = null,
    val available: Boolean? = null,
    val currency: String? = null,
    val minimumRate: Double? = null,
)

@Serializable
data class AvailabilityCorporateRate(
    val corporateId: String,
    val ratePlanSets: List<String> = emptyList(),
)

@Serializable
data class AvailabilityRatesV2(
    val corporateRates: AvailabilityCorporateRate? = null,
    val ratePlanCodes: List<String> = emptyList(),
)

@Serializable
data class AvailabilityRatesV3(
    val corporateRates: List<AvailabilityCorporateRate> = emptyList(),
    val ratePlanCodes: List<String> = emptyList(),
)

/** Body for `POST /ohip/v2/hotels/availabilities/distr` (multi-room-rate search). */
@Serializable
data class AvailabilityByIdsV2Request(
    val bookingChannel: AvailabilityBookingChannel,
    val hotelIds: List<String>,
    val arrivalDate: String,
    val departureDate: String,
    val rooms: List<AvailabilityRoomV2>,
    val rates: AvailabilityRatesV2,
)

/** Body for `POST /ohip/v3/hotels/availabilities/distr` (multi-corporate variant). */
@Serializable
data class AvailabilityByIdsV3Request(
    val bookingChannel: AvailabilityBookingChannel,
    val hotelIds: List<String>,
    val arrivalDate: String,
    val departureDate: String,
    val rooms: List<AvailabilityRoomV2>,
    val rates: AvailabilityRatesV3,
)

@Serializable
data class AvailabilityByIdsV2Response(
    val hotelAvailability: List<AvailabilityByIdsV2Hotel> = emptyList(),
)

@Serializable
data class AvailabilityByIdsV2Hotel(
    val hotelId: String? = null,
    val roomStays: List<AvailabilityByIdsV2RoomStay> = emptyList(),
)

@Serializable
data class AvailabilityByIdsV2RoomStay(
    val roomClass: String? = null,
    val roomTypes: List<AvailabilityByIdsV2RoomType> = emptyList(),
)

@Serializable
data class AvailabilityByIdsV2RoomType(
    val tag: String? = null,
    val roomType: String? = null,
    val adults: String? = null,
    val children: String? = null,
    val numberOfRooms: String? = null,
    val specialRequests: List<String> = emptyList(),
    val roomRates: List<AvailabilityByIdsV2RoomRate> = emptyList(),
)

@Serializable
data class AvailabilityByIdsV2RoomRate(
    val ratePlanCode: String? = null,
    val displaySet: String? = null,
    val currencyCode: String? = null,
    val globalCompanyId: String? = null,
)
