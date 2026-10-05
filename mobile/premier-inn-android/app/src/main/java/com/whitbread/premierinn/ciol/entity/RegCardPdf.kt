package com.whitbread.premierinn.ciol.entity

import android.os.Parcelable
import com.whitbread.premierinn.ciol.utils.DATE_FORMAT
import com.whitbread.premierinn.ciol.viewmodel.RegCardGuestDetailsViewModel
import com.whitbread.premierinn.data.common.EMPTY_STRING
import kotlinx.parcelize.Parcelize
import org.threeten.bp.format.DateTimeFormatter

@Parcelize
data class RegCardPdfModel(
        var transactionId: String = EMPTY_STRING,
        val reservationId: String = EMPTY_STRING,
        val profileId: String = EMPTY_STRING,
        val hotelName: String = EMPTY_STRING,
        val hotelAddress: String = EMPTY_STRING,
        val arrivalDate: String = EMPTY_STRING,
        val departureDate: String = EMPTY_STRING,
        val leadGuest: RegCardPdfLeadGuest,
        val additionalGuests: List<RegCardPdfAdditionalGuest>
): Parcelable {
        companion object {
                fun from(transactionId: String, guestDetailsState: RegCardGuestDetailsViewModel.GuestDetailsState): RegCardPdfModel {
                        var regCardPdfLeadGuest: RegCardPdfLeadGuest? = null
                        val regCardPdfAdditionalGuestList: MutableList<RegCardPdfAdditionalGuest> = mutableListOf()
                        var profileId: String = EMPTY_STRING
                        var reservationId: String = EMPTY_STRING

                        guestDetailsState.stayingGuests?.forEach { stayingGuest ->
                                when(stayingGuest) {
                                        is AdditionalGuestUiModel -> {
                                                stayingGuest.let {
                                                        regCardPdfAdditionalGuestList.add(
                                                                RegCardPdfAdditionalGuest(
                                                                        firstName = it.firstName,
                                                                        lastName = it.lastName,
                                                                        dateOfBirth = it.dateOfBirth,
                                                                        nationality = it.nationality
                                                                )
                                                        )
                                                }
                                        }

                                        is LeadGuestUiModel -> {
                                                stayingGuest.let {
                                                        regCardPdfLeadGuest = RegCardPdfLeadGuest(
                                                                firstName = it.additionalGuestUiModel.firstName,
                                                                lastName = it.additionalGuestUiModel.lastName,
                                                                homeAddress = it.address.addressLine1,
                                                                postcode = it.address.postalCode,
                                                                city = it.address.cityName,
                                                                country = it.address.country,
                                                                dateOfBirth = it.additionalGuestUiModel.dateOfBirth,
                                                                nationality = it.additionalGuestUiModel.nationality,
                                                                passportNumber = it.additionalGuestUiModel.passportNumber
                                                        )
                                                        profileId = it.additionalGuestUiModel.profileId ?: EMPTY_STRING
                                                        reservationId = it.additionalGuestUiModel.reservationId
                                                }
                                        }
                                }
                        }

                        return RegCardPdfModel(
                                reservationId = reservationId,
                                profileId = profileId,
                                transactionId = transactionId,
                                hotelName = guestDetailsState.preStayModel?.preStayHeaderInfo?.hotelName ?: EMPTY_STRING,
                                hotelAddress = guestDetailsState.preStayModel?.preStayHeaderInfo?.hotelAddress ?: EMPTY_STRING,
                                arrivalDate = guestDetailsState.preStayModel?.preStayDetails?.startDate?.format(
                                        DateTimeFormatter.ofPattern(DATE_FORMAT)) ?: EMPTY_STRING,
                                departureDate = guestDetailsState.preStayModel?.preStayDetails?.endDate?.format(
                                        DateTimeFormatter.ofPattern(DATE_FORMAT)) ?: EMPTY_STRING,
                                leadGuest = regCardPdfLeadGuest ?: RegCardPdfLeadGuest(),
                                additionalGuests = regCardPdfAdditionalGuestList
                        )
                }
        }
}

@Parcelize
data class RegCardPdfLeadGuest(
        val firstName: String = EMPTY_STRING,
        val lastName: String = EMPTY_STRING,
        val homeAddress: String = EMPTY_STRING,
        val postcode: String = EMPTY_STRING,
        val city: String = EMPTY_STRING,
        val country: String = EMPTY_STRING,
        val dateOfBirth: String = EMPTY_STRING,
        val nationality: String = EMPTY_STRING,
        val passportNumber: String = EMPTY_STRING,
): Parcelable

@Parcelize
data class RegCardPdfAdditionalGuest(
        val firstName: String = EMPTY_STRING,
        val lastName: String = EMPTY_STRING,
        val dateOfBirth: String = EMPTY_STRING,
        val nationality: String = EMPTY_STRING
): Parcelable

data class RegCardPdfResult(
        val pdfBase64Encoded: String = EMPTY_STRING,
        val fileName: String = EMPTY_STRING
)

data class GenerateAndSaveRegCardPdfStatus(
        val status: String = EMPTY_STRING
)
