package com.whitbread.premierinn.ciol.viewmodel.state.utils

import com.whitbread.premierinn.ciol.entity.LeadBookerDetailsUiModel
import com.whitbread.premierinn.ciol.utils.getFirstName
import com.whitbread.premierinn.ciol.utils.getLastName
import com.whitbread.premierinn.ciol.utils.getTitle
import com.whitbread.premierinn.ciol.utils.isSecondGuestPresent
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.booking.entity.GuestsRoom
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.common.translateTitleToGermanIfApplicable
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AccompanyingGuestDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRegCardRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuest
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestAdditionalDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestDetailsRegCard
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdatePreStayInfoRequestBody

private const val REASON_LEISURE= "LEI"

fun buildUpdateRequestBody(
    preStayModel: PreStayModel,
    bookerName: String,
    bookerEmail: String,
    bookerPhone: String,
    rooms: List<GuestsRoom>,
    leadBookerName: String,
    deviceLocaleProvider: DeviceLocaleProvider
) = UpdatePreStayInfoRequestBody(
    basketReference = preStayModel.basketReference,
    hotelId = preStayModel.preStayDetails.hotelId,
    reasonForStay = REASON_LEISURE,
    addressLine1 = preStayModel.preStayDetails.bookerDetails.address.addressLine1,
    addressLine2 = preStayModel.preStayDetails.bookerDetails.address.addressLine2,
    addressLine3 = preStayModel.preStayDetails.bookerDetails.address.addressLine3,
    addressLine4 = preStayModel.preStayDetails.bookerDetails.address.addressLine4,
    postalCode = preStayModel.preStayDetails.bookerDetails.address.postalCode,
    countryCode = preStayModel.preStayDetails.bookerDetails.address.country,
    title = bookerName.getTitle(),
    firstName = bookerName.getFirstName(),
    lastName = bookerName.getLastName(),
    emailAddress = bookerEmail,
    mobile = bookerPhone,
    stayingGuests = rooms.map { room ->
        StayingGuest(
            sameAsBooker = leadBookerName == getLeadGuestName(room, deviceLocaleProvider),
            stayingGuestDetails = StayingGuestDetails(
                room.leadGuestTitle,
                room.leadGuestFirstName,
                room.leadGuestLastName,
                StayingGuestAdditionalDetails(
                    passportNumber = room.leadGuestPassportNumber.ifBlank { null },
                    nationality = room.leadGuestNationality,
                )
            ),
            accompanyingGuestDetails = if (room.isSecondGuestPresent()) {
                AccompanyingGuestDetails(
                    room.accompanyingGuestTitle,
                    room.accompanyingGuestFirstName,
                    room.accompanyingGuestLastName,
                    StayingGuestAdditionalDetails(
                        passportNumber = room.accompanyingGuestPassportNumber.ifBlank { null },
                        nationality = room.accompanyingGuestNationality.ifBlank { null },
                    )
                )
            } else {
                null
            }
        )
    }
)

private fun getLeadGuestName(room: GuestsRoom, deviceLocaleProvider: DeviceLocaleProvider): String {
    return listOf(
        room.leadGuestTitle.translateTitleToGermanIfApplicable(deviceLocaleProvider.getDeviceLanguage()),
        room.leadGuestFirstName,
        room.leadGuestLastName
    ).filter { it.isNotEmpty() }.joinToString(" ")
}

fun buildCreateReservationGuestRegCardRequestBody(
    basketReference: String,
    hotelId: String,
    reasonForStay: String?,
    preCheckIn: Boolean?,
    acceptFutureMailing: Boolean? = null,
    addressType: String? = null,
    bookerDetails: LeadBookerDetailsUiModel,
    language: String,
    countryCode: String? = null,
    stayingGuests: List<StayingGuestDetailsRegCard>
) = CreateReservationGuestRegCardRequestBody(
    basketReference = basketReference,
    hotelId = hotelId,
    reasonForStay = reasonForStay ?: REASON_LEISURE,
    preCheckIn = preCheckIn,
    title = bookerDetails.leadBookerTitle,
    acceptFutureMailing = acceptFutureMailing,
    emailAddress = bookerDetails.leadBookerEmail,
    firstName = bookerDetails.leadBookerFirstName,
    lastName = bookerDetails.leadBookerLastName,
    addressType = addressType,
    addressLine1 = bookerDetails.address.addressLine1,
    addressLine2 = bookerDetails.address.addressLine2,
    addressLine3 = bookerDetails.address.addressLine3,
    addressLine4 = bookerDetails.address.addressLine4,
    postalCode = bookerDetails.address.postalCode,
    mobile = bookerDetails.leadBookerPhone,
    language = language,
    countryCode = countryCode,
    stayingGuests = stayingGuests
)
