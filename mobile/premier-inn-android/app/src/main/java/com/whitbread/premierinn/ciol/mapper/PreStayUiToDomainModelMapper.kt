package com.whitbread.premierinn.ciol.mapper

import com.whitbread.premierinn.ciol.entity.GuestsRoomUiModel
import com.whitbread.premierinn.ciol.entity.LeadBookerAddressUiModel
import com.whitbread.premierinn.ciol.entity.LeadBookerDetailsUiModel
import com.whitbread.premierinn.ciol.entity.PreStayDetailsUiModel
import com.whitbread.premierinn.ciol.entity.PreStayHeaderInfoUiModel
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.entity.ReservationGuestUiModel
import com.whitbread.premierinn.ciol.entity.RoomsUiModel
import com.whitbread.premierinn.ciol.uimodel.HotelPreferenceUiModel
import com.whitbread.premierinn.common.mapper.PriceDomainParcelable
import com.whitbread.premierinn.domain.booking.entity.GuestsRoom
import com.whitbread.premierinn.domain.booking.entity.HotelPreferenceDomain
import com.whitbread.premierinn.domain.booking.entity.LeadBookerAddress
import com.whitbread.premierinn.domain.booking.entity.LeadBookerDetails
import com.whitbread.premierinn.domain.booking.entity.PreStayDetails
import com.whitbread.premierinn.domain.booking.entity.PreStayHeaderInfo
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.ReservationGuest
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Rooms


fun PreStayUiModel.toPreStayDomainModel() = PreStayModel(
    preStayDetails = preStayDetails.toPreStayDetailsDomainModel(),
    preStayHeaderInfo = preStayHeaderInfo.toPreStayHeaderInfoDomainModel(),
    outstandingBalance = outstandingBalance?.toPriceDomainModel(),
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

fun PreStayDetailsUiModel.toPreStayDetailsDomainModel() = PreStayDetails(
    bookingFlowId = bookingFlowId,
    hotelId = hotelId,
    startDate = startDate,
    endDate = endDate,
    numberOfAdults = numberOfAdults,
    numberOfChildren = numberOfChildren,
    numberOfNights = numberOfNights,
    bookerDetails = bookerDetails.toLeadBookerDetailsDomainModel(),
    guestRooms = roomGuests.map { it.toRoomGuestsDomainModel() },
    rooms = rooms.map { it.toRoomsDomainModel() },
    reservationGuests = reservationGuests.map { it.toReservationGuestDomainModel() },
    preferences = preferences.map { it.toHotelPreferenceDomain() },
    preCheckInStatus = preCheckInStatus
)

fun PreStayHeaderInfoUiModel.toPreStayHeaderInfoDomainModel() = PreStayHeaderInfo(
    hotelName = hotelName,
    hotelImage = hotelImage,
    hotelBrand = hotelBrand,
    hotelAddress = hotelAddress
)

fun LeadBookerDetailsUiModel.toLeadBookerDetailsDomainModel() = LeadBookerDetails(
    leadBookerTitle = leadBookerTitle,
    leadBookerFirstName = leadBookerFirstName,
    leadBookerLastName = leadBookerLastName,
    leadBookerEmail = leadBookerEmail,
    leadBookerPhone = leadBookerPhone,
    address = address.toLeadBookerAddressDomainModel()
)

fun GuestsRoomUiModel.toRoomGuestsDomainModel() = GuestsRoom(
    roomId = roomId,
    leadGuestTitle = leadGuestTitle,
    leadGuestFirstName = leadGuestFirstName,
    leadGuestLastName = leadGuestLastName,
    leadGuestNationality = leadGuestNationality,
    leadGuestPassportNumber = leadGuestPassportNumber,
    isLeadGuestPassportNumberRequired = isLeadGuestPassportNumberRequired,
    accompanyingGuestTitle = accompanyingGuestTitle,
    accompanyingGuestFirstName = accompanyingGuestFirstName,
    accompanyingGuestLastName = accompanyingGuestLastName,
    accompanyingGuestNationality = accompanyingGuestNationality,
    accompanyingGuestPassportNumber = accompanyingGuestPassportNumber,
    isAccompanyingGuestPassportNumberRequired = isAccompanyingGuestPassportNumberRequired,
    numberOfAdults = numberOfAdults,
    numberOfChildren = numberOfChildren,
    purposeOfStay = purposeForStay
)

fun RoomsUiModel.toRoomsDomainModel() = Rooms(
    adultsNumber = adultsNumber,
    rate = rate,
    type = type
)

fun LeadBookerAddressUiModel.toLeadBookerAddressDomainModel() = LeadBookerAddress(
    addressLine1 = addressLine1,
    addressLine2 = addressLine2,
    addressLine3 = addressLine3,
    addressLine4 = addressLine4,
    postalCode = postalCode,
    cityName = cityName,
    country = country
)

fun ReservationGuestUiModel.toReservationGuestDomainModel() = ReservationGuest(
    reservationId = reservationId,
    profileId = profileId,
    firstName = firstName,
    lastName = lastName,
    title = title,
    email = email,
    dateOfBirth = dateOfBirth,
    passportNumber = passportNumber,
    nationality = nationality,
    isAccompanyingGuest = isAccompanyingGuest,
    address = address?.toLeadBookerAddressDomainModel()
)

fun HotelPreferenceUiModel.toHotelPreferenceDomain() = HotelPreferenceDomain(
    code = code,
    preferenceGroup = preferenceGroup,
    label = label
)

fun PriceDomainParcelable.toPriceDomainModel() = PriceDomain(amount, currency)
