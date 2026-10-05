package com.whitbread.premierinn.domain.booking.entity

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.ReservationGuest
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Rooms
import org.threeten.bp.LocalDate

data class PreStayDetails(
    val bookingFlowId: String,
    val hotelId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val numberOfAdults: Int = 1,
    val numberOfChildren: Int = 0,
    val numberOfNights: Int = 1,
    val bookerDetails: LeadBookerDetails,
    val guestRooms: List<GuestsRoom>,
    val rooms: List<Rooms>,
    val reservationGuests: List<ReservationGuest>,
    val preferences: List<HotelPreferenceDomain>,
    val preCheckInStatus: Boolean = false
)

data class LeadBookerDetails(
    val leadBookerTitle: String,
    val leadBookerFirstName: String,
    val leadBookerLastName: String,
    val leadBookerEmail: String,
    val leadBookerPhone: String,
    val address: LeadBookerAddress
)

data class LeadBookerAddress(
    val addressLine1: String,
    val addressLine2: String,
    val addressLine3: String,
    val addressLine4: String,
    val postalCode: String,
    val cityName: String,
    val country: String
) {
    companion object {
        fun empty() = LeadBookerAddress(
            EMPTY_STRING_DOMAIN,
            EMPTY_STRING_DOMAIN,
            EMPTY_STRING_DOMAIN,
            EMPTY_STRING_DOMAIN,
            EMPTY_STRING_DOMAIN,
            EMPTY_STRING_DOMAIN,
            EMPTY_STRING_DOMAIN
        )
    }
}

data class GuestsRoom(
    val roomId: String,
    val leadGuestTitle: String = EMPTY_STRING_DOMAIN,
    val leadGuestFirstName: String = EMPTY_STRING_DOMAIN,
    val leadGuestLastName: String = EMPTY_STRING_DOMAIN,
    val leadGuestDateOfBirth: String = EMPTY_STRING_DOMAIN,
    val leadGuestNationality: String = EMPTY_STRING_DOMAIN,
    val leadGuestPassportNumber: String = EMPTY_STRING_DOMAIN,
    var isLeadGuestPassportNumberRequired: Boolean = true,
    val accompanyingGuestTitle: String = EMPTY_STRING_DOMAIN,
    val accompanyingGuestFirstName: String = EMPTY_STRING_DOMAIN,
    val accompanyingGuestLastName: String = EMPTY_STRING_DOMAIN,
    val accompanyingGuestDateOfBirth: String = EMPTY_STRING_DOMAIN,
    val accompanyingGuestNationality: String = EMPTY_STRING_DOMAIN,
    val accompanyingGuestPassportNumber: String = EMPTY_STRING_DOMAIN,
    var isAccompanyingGuestPassportNumberRequired: Boolean = true,
    val numberOfAdults: Int = 1,
    val numberOfChildren: Int,
    val purposeOfStay: String?
) {

    fun isNationalityValidForLeadGuest() = leadGuestNationality.isNotEmpty() &&
            (!isLeadGuestPassportNumberRequired || leadGuestPassportNumber.isNotEmpty())

    fun isNationalityValidForAccompanyingGuest() =
        isAccompanyingGuestMissing() || (
                accompanyingGuestNationality.isNotEmpty() &&
                        (!isAccompanyingGuestPassportNumberRequired || accompanyingGuestPassportNumber.isNotEmpty())
                )

    private fun isAccompanyingGuestMissing() = numberOfAdults == 1
}

data class PreStayHeaderInfo(
    val hotelName: String,
    val hotelImage: String?,
    val hotelBrand: String,
    val hotelAddress: String
)

data class PreStayModel(
    val preStayDetails: PreStayDetails,
    val preStayHeaderInfo: PreStayHeaderInfo,
    val outstandingBalance: PriceDomain?,
    val basketReference: String,
    val isOpera: Boolean,
    val bookingReference: String,
    val rateCode: String,
    val rateDescription: String,
    val rateName: String,
    val isBusinessBooking: Boolean,
    val isThirdPartyBooking: Boolean,
    val paymentOption: String,
    val upsellsAddonsEnabled: Boolean
)
