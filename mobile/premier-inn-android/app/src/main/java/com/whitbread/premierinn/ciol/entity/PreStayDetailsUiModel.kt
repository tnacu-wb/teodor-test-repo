package com.whitbread.premierinn.ciol.entity

import android.os.Parcelable
import com.whitbread.premierinn.ciol.uimodel.HotelPreferenceUiModel
import com.whitbread.premierinn.common.mapper.PriceDomainParcelable
import com.whitbread.premierinn.common.utils.StringUtils
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class PreStayDetailsUiModel(
    val bookingFlowId: String,
    val hotelId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val numberOfAdults: Int = 1,
    val numberOfChildren: Int = 0,
    val numberOfNights: Int = 1,
    val bookerDetails: LeadBookerDetailsUiModel,
    val roomGuests: List<GuestsRoomUiModel>,
    val rooms: List<RoomsUiModel>,
    val reservationGuests: List<ReservationGuestUiModel>,
    val preferences: List<HotelPreferenceUiModel>,
    val preCheckInStatus: Boolean = false
) : Parcelable

@Parcelize
data class ReservationGuestUiModel(
    val profileId: String?,
    val reservationId: String,
    val firstName: String,
    val lastName: String,
    val title: String?,
    val email: String?,
    val dateOfBirth: String?,
    val passportNumber: String?,
    val nationality: String?,
    val isAccompanyingGuest: Boolean,
    val address: LeadBookerAddressUiModel?
) : Parcelable

@Parcelize
data class RoomsUiModel(
    val adultsNumber: Int,
    val rate: String,
    val type: String
): Parcelable

@Parcelize
data class LeadBookerDetailsUiModel(
    val leadBookerTitle: String,
    val leadBookerFirstName: String,
    val leadBookerLastName: String,
    val leadBookerEmail: String,
    val leadBookerPhone: String,
    val address: LeadBookerAddressUiModel
) : Parcelable

@Parcelize
data class LeadBookerAddressUiModel(
    val addressLine1: String,
    val addressLine2: String,
    val addressLine3: String,
    val addressLine4: String,
    val postalCode: String,
    val cityName: String,
    val country: String
) : Parcelable

@Parcelize
data class GuestsRoomUiModel(
    val roomId: String,
    val leadGuestTitle: String,
    val leadGuestFirstName: String,
    val leadGuestLastName: String,
    val leadGuestNationality: String = StringUtils.EMPTY_STRING,
    val leadGuestPassportNumber: String = StringUtils.EMPTY_STRING,
    val isLeadGuestPassportNumberRequired: Boolean = true,
    val accompanyingGuestTitle: String = StringUtils.EMPTY_STRING,
    val accompanyingGuestFirstName: String = StringUtils.EMPTY_STRING,
    val accompanyingGuestLastName: String = StringUtils.EMPTY_STRING,
    val accompanyingGuestDateOfBirth: String = StringUtils.EMPTY_STRING,
    val accompanyingGuestNationality: String = StringUtils.EMPTY_STRING,
    val accompanyingGuestPassportNumber: String = StringUtils.EMPTY_STRING,
    var isAccompanyingGuestPassportNumberRequired: Boolean = true,
    val numberOfAdults: Int = 1,
    val numberOfChildren: Int,
    val purposeForStay: String? = null
) : Parcelable {

    fun isNationalityAndIdValidForLeadGuest() = !isNationalityMissingForLeadGuest() &&
        (!isLeadGuestPassportNumberRequired || leadGuestPassportNumber.isNotEmpty())

    fun isNationalityMissingForLeadGuest() = leadGuestNationality.isEmpty()

    fun isNationalityAndIdValidForAccompanyingGuest() =
        isAccompanyingGuestMissing() || (
            !isNationalityMissingForAccompanyingGuest() &&
                (!isAccompanyingGuestPassportNumberRequired || accompanyingGuestPassportNumber.isNotEmpty())
            )

    fun isNationalityMissingForAccompanyingGuest() = !isAccompanyingGuestMissing() && accompanyingGuestNationality.isEmpty()

    private fun isAccompanyingGuestMissing() = numberOfAdults == 1
}

@Parcelize
data class RoomUiModel(
    val adultsNumber: Int,
    val rate: String,
    val type: String
): Parcelable

@Parcelize
data class PreStayHeaderInfoUiModel(
    val hotelName: String,
    val hotelImage: String?,
    val hotelBrand: String,
    val hotelAddress: String
) : Parcelable

@Parcelize
data class PreStayUiModel(
    val preStayDetails: PreStayDetailsUiModel,
    val preStayHeaderInfo: PreStayHeaderInfoUiModel,
    val outstandingBalance: PriceDomainParcelable?,
    val basketReference: String,
    val isOpera: Boolean,
    val bookingReference: String,
    val rateCode: String,
    val rateDescription: String,
    val rateName: String,
    val isBusinessBooking: Boolean,
    val isThirdPartyBooking: Boolean,
    val paymentOption: String,
    val upsellsAddonsEnabled: Boolean,
) : Parcelable
