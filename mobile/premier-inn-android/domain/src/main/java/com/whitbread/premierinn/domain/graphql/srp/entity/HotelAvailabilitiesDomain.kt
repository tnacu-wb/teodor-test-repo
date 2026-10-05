package com.whitbread.premierinn.domain.graphql.srp.entity

import com.whitbread.premierinn.domain.common.Availability
import com.whitbread.premierinn.domain.common.toAvailability

data class HotelAvailabilitiesDomain(
        val multiHotelAvailabilities: List<SingleHotelAvailabilityDomain>?,
)

data class SingleHotelAvailabilityDomain(
        val hotelId: String,
        val name: String,
        val hotelAvailability: ShortHotelAvailabilityDomain,
        val hotelInformation: ShortHotelInformationDomain
)

data class ShortHotelAvailabilityDomain(
        val distance: Float,
        val lowestRoomRate: LowestRoomRateDomain?,
        val available: Boolean,
        val limitedAvailability: Boolean,
        val pmsSource: String,
        val cellCode: String
) {
    val isFullyBooked = this.toAvailability() == Availability.SOLD_OUT
}

data class LowestRoomRateDomain(
        val netTotal: Float,
        val currencyCode: String
)

data class ShortHotelInformationDomain(
        val brand: String,
        val thumbnailImages: List<ThumbnailImageDomain>?,
        val hotelFacilities: List<FacilityItemDomain>,
        val messagingFlag: MessagingFlagDomain,
        val coordinates: CoordinatesDomain,
)

data class ThumbnailImageDomain(
        val imageSrc: String?,
)

data class FacilityItemDomain(
        val code: String,
        val description: String,
)

data class MessagingFlagDomain(
        val text: String,
        val color: String
)

data class CoordinatesDomain(
        val latitude: Float,
        val longitude: Float
)