package com.whitbread.premierinn.ciol.mapper

import com.whitbread.premierinn.ciol.entity.GuestsRoomUiModel
import com.whitbread.premierinn.ciol.entity.LeadBookerAddressUiModel
import com.whitbread.premierinn.ciol.entity.LeadBookerDetailsUiModel
import com.whitbread.premierinn.ciol.entity.PreStayDetailsUiModel
import com.whitbread.premierinn.ciol.entity.PreStayHeaderInfoUiModel
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.entity.RoomsUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.convertToReservationGuestUiModel
import com.whitbread.premierinn.ciol.uimodel.HotelPreferenceUiModel
import com.whitbread.premierinn.common.mapper.toParcelable
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.booking.entity.GuestsRoom
import com.whitbread.premierinn.domain.booking.entity.HotelPreferenceDomain
import com.whitbread.premierinn.domain.booking.entity.LeadBookerAddress
import com.whitbread.premierinn.domain.booking.entity.LeadBookerDetails
import com.whitbread.premierinn.domain.booking.entity.PreStayDetails
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Rooms


fun PreStayModel.convertToUiModel() =
    PreStayUiModel(
        preStayDetails = preStayDetails.convertToUiModelPreStayDetails(),
        preStayHeaderInfo = convertToUiModelHeaderInfo(),
        outstandingBalance = outstandingBalance?.toParcelable(),
        basketReference = basketReference,
        isOpera = isOpera,
        bookingReference = bookingReference,
        rateCode = rateCode,
        rateDescription = rateDescription,
        rateName = rateName,
        isBusinessBooking = isBusinessBooking,
        isThirdPartyBooking = isThirdPartyBooking,
        paymentOption = paymentOption,
        upsellsAddonsEnabled = upsellsAddonsEnabled
    )

fun PreStayModel.convertToUiModelHeaderInfo() = PreStayHeaderInfoUiModel(
    hotelName = preStayHeaderInfo.hotelName,
    hotelImage = preStayHeaderInfo.hotelImage,
    hotelBrand = preStayHeaderInfo.hotelBrand,
    hotelAddress = preStayHeaderInfo.hotelAddress
)

fun PreStayModel.convertToHotelPackagesRequestBody(deviceLocaleProvider: DeviceLocaleProvider) = HotelPackagesRequestBody(
    hotelId = preStayDetails.hotelId,
    startDate = preStayDetails.startDate.toString(),
    endDate = preStayDetails.endDate.toString(),
    adultsNumber = preStayDetails.numberOfAdults,
    childrenNumber = preStayDetails.numberOfChildren,
    nightsNumber = preStayDetails.numberOfNights,
    language = deviceLocaleProvider.getDeviceLanguage().lowercase(),
    country = deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()).lowercase(),
    bookingFlowId = preStayDetails.bookingFlowId,
    basketReferenceId = basketReference,
    channel = if (isBusinessBooking) Channel.BB else Channel.PI,
)

fun PreStayDetails.convertToUiModelPreStayDetails() = PreStayDetailsUiModel(
    bookingFlowId,
    hotelId,
    startDate,
    endDate,
    numberOfAdults,
    numberOfChildren,
    numberOfNights,
    bookerDetails.convertToUiModelLeadBookerDetails(),
    guestRooms.convertToUiModelRoomGuestsList(),
    rooms.map { it.convertToRoomsUiModel() },
    reservationGuests.map { it.convertToReservationGuestUiModel() },
    preferences.map { it.convertToHotelPreferenceUiModel() },
    preCheckInStatus
)

fun LeadBookerDetails.convertToUiModelLeadBookerDetails() = LeadBookerDetailsUiModel(
    leadBookerTitle,
    leadBookerFirstName,
    leadBookerLastName,
    leadBookerEmail,
    leadBookerPhone,
    address.convertToUiModelLeadBookerAddress()
)

fun LeadBookerAddress.convertToUiModelLeadBookerAddress() =
    LeadBookerAddressUiModel(
        addressLine1,
        addressLine2,
        addressLine3,
        addressLine4,
        postalCode,
        cityName,
        country
    )

fun List<GuestsRoom>.convertToUiModelRoomGuestsList(): List<GuestsRoomUiModel> {
    val resultList = ArrayList<GuestsRoomUiModel>()
    forEach { item ->
        resultList.add(
            GuestsRoomUiModel(
                roomId = item.roomId,
                leadGuestTitle = item.leadGuestTitle,
                leadGuestFirstName = item.leadGuestFirstName,
                leadGuestLastName = item.leadGuestLastName,
                leadGuestNationality = item.leadGuestNationality,
                leadGuestPassportNumber = item.leadGuestPassportNumber,
                isLeadGuestPassportNumberRequired = item.isLeadGuestPassportNumberRequired,
                accompanyingGuestTitle = item.accompanyingGuestTitle,
                accompanyingGuestFirstName = item.accompanyingGuestFirstName,
                accompanyingGuestLastName = item.accompanyingGuestLastName,
                accompanyingGuestNationality = item.accompanyingGuestNationality,
                accompanyingGuestPassportNumber = item.accompanyingGuestPassportNumber,
                isAccompanyingGuestPassportNumberRequired = item.isAccompanyingGuestPassportNumberRequired,
                numberOfAdults = item.numberOfAdults,
                numberOfChildren = item.numberOfChildren
            )
        )
    }
    return resultList
}

fun Rooms.convertToRoomsUiModel() = RoomsUiModel(
    adultsNumber = adultsNumber,
    rate = rate,
    type = type
)

fun HotelPreferenceDomain.convertToHotelPreferenceUiModel() = HotelPreferenceUiModel(
    code = code,
    preferenceGroup = preferenceGroup,
    label = label
)
