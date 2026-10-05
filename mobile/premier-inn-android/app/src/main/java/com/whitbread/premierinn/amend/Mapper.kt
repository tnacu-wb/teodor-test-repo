package com.whitbread.premierinn.amend

import com.whitbread.premierinn.amend.amendguestsrooms.ParcelableAmendTotal
import com.whitbread.premierinn.amend.amendguestsrooms.ParcelableGuest
import com.whitbread.premierinn.amend.amendreview.uimodel.AmendedTotal
import com.whitbread.premierinn.common.ParcelableDailyRate
import com.whitbread.premierinn.common.ParcelablePrice
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.DailyRate
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.LeadGuest
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RatePlan
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.common.Room
import com.whitbread.premierinn.domain.common.RoomOpera
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.hoteldetails.entity.GalleryImageDomain
import com.whitbread.premierinn.domain.common.isAlternativeRoomOpera
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesSelectionDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendRoomsSelections
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendSelectedPackages
import com.whitbread.premierinn.hoteldetails.ParcelableRatePlanOpera
import com.whitbread.premierinn.hoteldetails.ParcelableRoomOpera
import com.whitbread.premierinn.postcodefinder.ParcelableAddress

fun PriceDomain.toParcelable(): ParcelablePrice {
    return ParcelablePrice(
            amount = amount,
            currency = currency
    )
}

fun ParcelablePrice.toPriceDomain(): PriceDomain {
    return PriceDomain(
            amount = amount,
            currency = currency
    )
}

fun AmendedTotal.toParcelable(): ParcelableAmendTotal {
    return ParcelableAmendTotal(
            title = title,
            description = description,
            amount = amount
    )
}

fun RatePlan.toParcelable() : ParcelableRatePlan {
    return ParcelableRatePlan(
            code = code,
            rateType = rateType,
            totalCost = totalCost.toParcelable(),
            cityTax = cityTax?.toParcelable(),
            roomList = roomList.toRoomParcelable()
    )
}

fun RatePlanOpera.toParcelable() : ParcelableRatePlanOpera {
    return ParcelableRatePlanOpera(
        code = code,
        rateType = rateType,
        cellCode = cellCode,
        totalCost = totalCost.toParcelable(),
        cityTax = cityTax?.toParcelable(),
        roomList = roomList.toRoomOperaParcelable(),
        alternateRoomList = alternateRoomList.toRoomOperaParcelable(),
        accessibleRoomList = accessibleRoomList?.toRoomOperaParcelable(),
        twinRoomList = twinRoomList?.toRoomOperaParcelable()
    )
}

fun List<Room>.toRoomParcelable() : List<ParcelableRoom> {
    return this.map { it.toParcelable() }
}


fun List<RoomOpera>.toRoomOperaParcelable() : List<ParcelableRoomOpera> {
    return this.map { it.toParcelable() }
}

fun ParcelableRatePlanOpera.toDomain() : RatePlanOpera {
    return RatePlanOpera(
        code = code,
        rateType = rateType,
        cellCode = cellCode,
        totalCost = totalCost.toPriceDomain(),
        cityTax = cityTax?.toPriceDomain(),
        roomList = roomList.toListOfRoomOpera(),
        alternateRoomList = alternateRoomList?.toListOfAlternateRoom() ?: emptyList(),
        accessibleRoomList = accessibleRoomList?.toListOfAccessibleRooms()
    )
}

fun List<ParcelableRoomOpera>.toListOfRoomOpera(): List<RoomOpera> {
    val listOfStdRooms = mutableListOf<RoomOpera>()
    this.forEach { parcelableRoom ->
        if (!parcelableRoom.roomClass.isAlternativeRoomOpera()) {
            listOfStdRooms.add(
                RoomOpera(
                    number = parcelableRoom.number,
                    type = parcelableRoom.type,
                    pmsRoomType = parcelableRoom.lettingType,
                    cost = parcelableRoom.cost.toPriceDomain(),
                    cityTax = null,
                    lettingType = parcelableRoom.roomClass,
                    cot = false,
                    adults = parcelableRoom.adults,
                    children = parcelableRoom.children,
                    infants = 0,
                    baseRateAmount = parcelableRoom.baseRateAmount,
                    specialRequests = parcelableRoom.specialRequests ?: emptyList()
                )
            )
        }
    }

    return listOfStdRooms
}

fun List<ParcelableRoomOpera>.toListOfAlternateRoom(): List<RoomOpera> {
    val listOfAltRooms = mutableListOf<RoomOpera>()
    this.forEach { parcelableRoom ->
            listOfAltRooms.add(
                RoomOpera(
                    number = parcelableRoom.number,
                    type = parcelableRoom.type,
                    pmsRoomType = parcelableRoom.lettingType,
                    cost = parcelableRoom.cost.toPriceDomain(),
                    cityTax = null,
                    lettingType = parcelableRoom.roomClass,
                    cot = false,
                    adults = parcelableRoom.adults,
                    children = parcelableRoom.children,
                    infants = 0,
                    baseRateAmount = parcelableRoom.baseRateAmount,
                    specialRequests = parcelableRoom.specialRequests ?: emptyList()
                )
            )
    }

    return listOfAltRooms
}

fun List<ParcelableRoomOpera>.toListOfAccessibleRooms(): List<RoomOpera> {
    val listOfAccRooms = mutableListOf<RoomOpera>()
    this.forEach { parcelableRoom ->
        if (parcelableRoom.type == RoomType.ACCESSIBLE) {
            listOfAccRooms.add(
                RoomOpera(
                    number = parcelableRoom.number,
                    type = parcelableRoom.type,
                    pmsRoomType = parcelableRoom.lettingType,
                    cost = parcelableRoom.cost.toPriceDomain(),
                    cityTax = null,
                    lettingType = parcelableRoom.roomClass,
                    cot = false,
                    adults = parcelableRoom.adults,
                    children = parcelableRoom.children,
                    infants = 0,
                    baseRateAmount = parcelableRoom.baseRateAmount,
                    specialRequests = parcelableRoom.specialRequests ?: emptyList()
                    )
            )
        }
    }

    return listOfAccRooms
}

fun Room.toParcelable() : ParcelableRoom {
    return ParcelableRoom(
            number = number,
            type = type,
            cost = cost.toParcelable(),
            cityTax = cityTax?.toParcelable(),
            lettingType = lettingType
    )
}

fun List<Booking.Room>.toListOfParcelableRooms(): List<ParcelableRoomOperaAmend> {
    val listOfParcelableRoom = mutableListOf<ParcelableRoomOperaAmend>()
    this.forEach { room ->
        listOfParcelableRoom.add(ParcelableRoomOperaAmend(
            number = room.roomId.toInt(),
            type = room.roomType,
            numberOfAdults = room.numberOfAdults,
            numberOfChildren = room.numberOfChildren,
            cost = ParcelablePrice(0f, GBP),
            cityTax = ParcelablePrice(0f, GBP),
            lettingType = room.lettingType ?: EMPTY_STRING
        ))
    }

    return listOfParcelableRoom
}

fun List<ParcelableRoomOperaAmend>.toListOfBookingRoom(): List<Booking.Room> {
    val listOfBookingRooms = mutableListOf<Booking.Room>()
    this.forEach { parcelableRoom ->
        listOfBookingRooms.add(
            Booking.Room(
                roomId = parcelableRoom.number.toString(),
                roomType = parcelableRoom.type,
                lettingType = parcelableRoom.lettingType,
                numberOfAdults = parcelableRoom.numberOfAdults,
                numberOfChildren = parcelableRoom.numberOfChildren,
                leadGuestInfo = LeadGuest.createDefault())
        )
    }

    return listOfBookingRooms
}

fun RoomOpera.toParcelable() : ParcelableRoomOpera {
    return ParcelableRoomOpera(
            number = number,
            type = type,
            cost = cost.toParcelable(),
            cityTax = cityTax?.toParcelable(),
            lettingType = pmsRoomType,
            roomClass = lettingType,
            adults = adults,
            children = children,
            cot = cot,
            dailyRates = dailyRates.toParcelable(),
            baseRateAmount = baseRateAmount,
            specialRequests = specialRequests
    )
}

fun List<DailyRate>.toParcelable() : List<ParcelableDailyRate> {
    return this.map { it.toParcelable() }
}

fun DailyRate.toParcelable(): ParcelableDailyRate {
    return ParcelableDailyRate(
        date = date,
        price = price.toParcelable()
    )
}

fun ParcelableRatePlan.toDomain() : RatePlan {
    return RatePlan(
            code = code,
            rateType = rateType,
            totalCost = totalCost.toPriceDomain(),
            cityTax = cityTax?.toPriceDomain(),
            roomList = roomList.toRoomDomain()
    )
}

fun List<ParcelableRoom>.toRoomDomain() : List<Room> {
    return this.map { it.toRoomDomain() }
}
fun ParcelableRoom.toRoomDomain() : Room {
    return Room(
            number = number,
            type = type,
            cost = cost.toPriceDomain(),
            cityTax = cityTax?.toPriceDomain(),
            lettingType = lettingType
    )
}

fun Guest.toParcelable() : ParcelableGuest {
    return ParcelableGuest(
            roomNumber = roomNumber,
            roomId = roomId,
            title = title,
            firstName = firstName,
            lastName = lastName,
            guestHistoryNumber = guestHistoryNumber,
            address = address?.toParcelable(),
            emailAddress = emailAddress,
            phoneNumber = phoneNumber)
}

fun Address?.toParcelable() : ParcelableAddress? {
    return if (this != null) {
        ParcelableAddress(
                line1 = line1,
                line2 = line2,
                line3 = line3,
                line4 = line4,
                line5 = line5,
                postcode = postCode,
                companyName = companyName,
                countryCode = countryCode)
    } else null
}

fun ParcelableGuest.toDomain() : Guest {
    return Guest(roomNumber = roomNumber,
            roomId = roomId,
            title = title,
            firstName = firstName,
            lastName = lastName,
            guestHistoryNumber = guestHistoryNumber,
            address = address?.toDomain(),
            emailAddress = emailAddress,
            phoneNumber = phoneNumber)
}

fun ParcelableAddress?.toDomain() : Address? {
    return if (this != null) {
        Address(
                line1 = line1,
                line2 = line2,
                line3 = line3,
                line4 = line4,
                line5 = line5,
                postCode = postcode,
                companyName = companyName,
                countryCode = countryCode)
    } else null
}

fun List<PackagesSelectionDomain>.toListOfAmendSelectedPackages(): List<AmendSelectedPackages> {
    val listOfPackagesSelection = mutableListOf<AmendSelectedPackages>()
    this.forEach { packagesSelection ->
        listOfPackagesSelection.add(
            AmendSelectedPackages(
                id = packagesSelection.id!!,
                noOfSelections = packagesSelection.noOfSelections
            )
        )
    }
    return listOfPackagesSelection
}

fun List<RoomSelectionDomain>.toListOfAmendRoomsSelections(): List<AmendRoomsSelections> {
    val listOfRoomsSelection = mutableListOf<AmendRoomsSelections>()
    this.forEach { roomSelection ->
        listOfRoomsSelection.add(
            AmendRoomsSelections(
                reservationId = roomSelection.reservationId!!,
                packagesSelection = roomSelection.packagesSelection.toListOfAmendSelectedPackages().toMutableList()
            )
        )
    }
    return listOfRoomsSelection
}

fun PromotionsInformationDomain.toListOfParcelablePromotionsInformationDomain(): ParcelablePromotionsInformationDomain {
    return ParcelablePromotionsInformationDomain(
        showPromo = showPromo,
        isWithinPromoWindow = isWithinPromoWindow,
        promotionCode = promotionCode,
        landingPage = landingPage,
        promoBannerColour = promoBannerColour,
        promoBannerIcon = promoBannerIcon,
        termsLink = termsLink,
        appPromoBannerTitle = appPromoBannerTitle,
        appPromoBannerSubtitle = appPromoBannerSubtitle,
        appPromoInvalidMessage = appPromoInvalidMessage,
        appPromoExpiredMessage = appPromoExpiredMessage,
        appPromoAmendMessage = appPromoAmendMessage,
        promoBookingInfo = promoBookingInfo?.let { ParcelablePromoBookingInfoDomain(it.ratePlanCode, it.promotionCode) },
        promoBox = promoBox?.let {
            ParcelablePromoBoxDomain(
                title = it.title,
                button = it.button,
                whenInvalid = it.whenInvalid,
                whenMultipleRedeem = it.whenMultipleRedeem,
                whenSuccess = it.whenSuccess,
                whenEmpty = it.whenEmpty,
                whenCodeAlreadyApplied = it.whenCodeAlreadyApplied,
                whenUnavailable = it.whenUnavailable,
                whenCodeExpired = it.whenCodeExpired
            )
        },
        promoKind = promoKind,
        promoBoxStatus = promoBoxStatus,
        promoBoxMessageKey = promoBoxMessageKey
    )
}

fun List<GalleryImageDomain>.toImageSrcList(): List<String> {
    val imageSrcList = mutableListOf<String>()
    this.forEach { galleryImage ->
        imageSrcList.add(galleryImage.imageSrc)
    }

    return imageSrcList
}