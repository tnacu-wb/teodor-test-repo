package com.whitbread.premierinn.domain.hotel.entity

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RateClassificationsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomTypeInfoDomain
import com.whitbread.premierinn.domain.graphql.hdp.usecase.GraphQLHDPUseCase

data class HotelBookingAvailabilityState (
    val action: GraphQLHDPUseCase.AvailabilityAction? = null,
    private val hotelAvailability: HotelAvailabilityDomain? = null,
    private val ratesInformation: List<RateClassificationsDomain>? = null,
    private val listOfRoomTypeInformation: List<RoomTypeInfoDomain>? = null,
    private val promoCode: String = EMPTY_STRING_DOMAIN,
    val sitewidePromoActive: Boolean = false,
    val invalidDiscountCodeMessage: String? = null,
    private val promoKind: String? = null
) {
    val isHotelAvailabilitySuccessful: Boolean
        get() = (action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess
                && hotelAvailability != null && hotelAvailability.error != null)
                && hotelAvailability.available

    val isHotelAvailabilitySuccessfulHDP: Boolean
        get() = action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess &&
                hotelAvailability?.available == true
                &&
                hotelAvailability.startDate != "" &&
                hotelAvailability.endDate != "" &&
                !hotelAvailability.roomRateDomainList.isNullOrEmpty()

    val isAvailabilitySuccessAndRoomsAvailable: Boolean
        get() = action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess
                && hotelAvailability != null
                && checkIfRoomsAvailable(hotelAvailability)

    val isAvailabilityStateValid: Boolean
        get() = isHotelAvailabilitySuccessfulHDP && isAvailabilitySuccessAndRoomsAvailable

    val isAvailabilityLoading: Boolean
        get() = action is GraphQLHDPUseCase.AvailabilityAction.AvailabilityLoading

    private val isAvailabilityErrorOrFullyBooked: Boolean
        get() = action is GraphQLHDPUseCase.AvailabilityAction.AvailabilityError ||
                action is GraphQLHDPUseCase.AvailabilityAction.AvailabilityFullyBooked

    val isHotelAvailabilityUnsuccessful: Boolean
        get() = isAvailabilityErrorOrFullyBooked || !isHotelAvailabilitySuccessfulHDP

    val isHotelSuccessfulOrFullyBooked: Boolean
        get() = action is GraphQLHDPUseCase.AvailabilityAction.AvailabilityFullyBooked ||
                action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess

    val getHotelAvailability : HotelAvailabilityDomain?
        get() = if ( action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess) {
            hotelAvailability
        } else {
            null
        }

    val isLimitedAvailabilityTrue: Boolean
        get() = ((action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess
                && hotelAvailability?.limitedAvailability ?: false))

    fun checkIfRoomsAvailable(hotelAvailability: HotelAvailabilityDomain?): Boolean {
        return if (hotelAvailability?.roomRateDomainList != null) {
            if (hotelAvailability.roomRateDomainList.isNotEmpty() && hotelAvailability.roomRateDomainList.first().roomTypesDomainList.isNotEmpty()) {
                hotelAvailability.roomRateDomainList.first().roomTypesDomainList.first().roomOptionsDomainList.isNotEmpty()
            } else {
                false
            }
        } else {
            false
        }
    }

    val getRateInformation : List<RateClassificationsDomain>?
        get() = if (action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess) {
            ratesInformation
        } else {
            null
        }

    val getRoomTypeInformation : List<RoomTypeInfoDomain>?
        get() = if (action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess) {
            listOfRoomTypeInformation
        } else {
            null
        }

    val getPromoCodeFromAvailability : String
        get() = if (action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess) {
            promoCode
        } else {
            EMPTY_STRING_DOMAIN
        }

    val getPromoKindFromAvailability : String?
        get() = if (action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess) {
            promoKind
        } else {
            null
        }
}