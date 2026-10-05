package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.toLocalDate
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationAndAmendSummaryGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.entity.BookingConfirmation
import com.whitbread.premierinn.domain.booking.entity.GuestsRoom
import com.whitbread.premierinn.domain.booking.entity.HotelPreferenceDomain
import com.whitbread.premierinn.domain.booking.entity.LeadBookerAddress
import com.whitbread.premierinn.domain.booking.entity.LeadBookerDetails
import com.whitbread.premierinn.domain.booking.entity.PreStayDetails
import com.whitbread.premierinn.domain.booking.entity.ReservationById
import com.whitbread.premierinn.domain.booking.entity.ReservationPackage
import com.whitbread.premierinn.domain.booking.entity.RoomStay
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.EMPLOYEE_RATE_PLAN_CODE
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.EXTRAS_LIST
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.LeadGuest
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.ReservationGuest
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.common.mapReservationStatus
import com.whitbread.premierinn.domain.common.toPriceDomain
import com.whitbread.premierinn.domain.common.toRoomTypeGQL
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Rooms
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

const val TITLE_MR = "Mr"

fun BookingConfirmationGraphQLContract.BookingConfirmationData.mapToBookingDomain(
    hotelName: String?,
    isBusinessBooking: Boolean = false,
    countries: List<CountryDomain>
): Booking{
    var calculatedPrepaidAmount: PriceDomain?

    return this.data.bookingConfirmation?.let {
        calculatedPrepaidAmount = if (this.data.bookingConfirmation.balanceOutstanding == 0.0f) {
            PriceDomain(this.data.bookingConfirmation.totalCost,
                this.data.bookingConfirmation.currencyCode ?: GBP)
        } else {
            null
        }

        //Amend restrictions : Ours is a bit different than iOS
        // We check restricted flag and the other flags to show or hide sections or apply restrictions
        var amendRestrictions = AmendRestrictions.createWithDefaults()
        this.data.manageBooking?.let { manageBooking ->
            if (manageBooking.isAmendable && !manageBooking.isCancellable) {
                amendRestrictions = AmendRestrictions(nights = true, rooms = true,
                    guestNames = true, upsell = false, restricted = true)
            }
        }

        Booking(
            bookingReference = this.data.bookingConfirmation.bookingReference,
            leadGuestFullName = this.data.bookingConfirmation.retrieveGuestName(),
            arrivalDate = LocalDate.parse(this.data.bookingConfirmation.reservationByIdList[0].roomStay.arrivalDate),
            departureDate = LocalDate.parse(this.data.bookingConfirmation.reservationByIdList[0].roomStay.departureDate),
            hotelCode = this.data.bookingConfirmation.hotelId,
            hotelName = hotelName ?: EMPTY_STRING_DOMAIN,
            numberOfRooms = this.data.bookingConfirmation.reservationByIdList.size,
            numberOfGuests = this.data.bookingConfirmation.reservationByIdList.toNumberOfGuests(),
            leadGuestSurname = this.data.bookingConfirmation.reservationByIdList[0].reservationGuestList[0].surName,
            rateType = this.data.bookingConfirmation.reservationByIdList[0].roomStay.rateExtraInfo?.rateName ?: EMPTY_STRING_DOMAIN,
            totalCost = PriceDomain(
                this.data.bookingConfirmation.totalCost,
                this.data.bookingConfirmation.currencyCode ?: GBP
            ),
            balanceOutstanding = PriceDomain(
                this.data.bookingConfirmation.balanceOutstanding
                    ?: PriceDomain.createDefault().amount,
                this.data.bookingConfirmation.currencyCode ?: GBP
            ),
            prepaidAmount = calculatedPrepaidAmount,
            amendable = this.data.manageBooking?.isAmendable == true,
            cancellable = this.data.manageBooking?.isCancellable == true,
            isCancelled = this.data.bookingConfirmation.reservationByIdList[0].reservationStatus.mapReservationStatus(),
            isLinkedToAccount = false, // not migrated
            cardFeeApplies = false,
            details = Booking.Details(
                false, PriceDomain.createDefault(),
                this.data.bookingConfirmation.reservationByIdList[0].roomStay.rateExtraInfo?.rateName ?: EMPTY_STRING_DOMAIN
            ),
            upsells = this.data.bookingConfirmation.toListOfUpsells(),
            amendRestrictions = amendRestrictions,
            booker = createBookerGuestWithThirdPartyFallback(
                this.data.bookingConfirmation.reservationByIdList[0],
                this.data.bookingConfirmation.bookingReference,
                this.data.bookingConfirmation.isThirdPartyBooking
            ),
            rooms = this.data.bookingConfirmation.reservationByIdList.toListOfRooms(this.data.bookingConfirmation.bookingReference),
            isBusinessBooking = isBusinessBooking,
            preStayDetails = PreStayDetails(
                bookingFlowId = data.bookingConfirmation.bookingFlowID,
                hotelId = data.bookingConfirmation.hotelId,
                startDate = LocalDate.parse(data.bookingConfirmation.reservationByIdList[0].roomStay.arrivalDate),
                endDate = LocalDate.parse(data.bookingConfirmation.reservationByIdList[0].roomStay.departureDate),
                numberOfAdults = data.bookingConfirmation.reservationByIdList.toNumberOfAdults(),
                numberOfChildren = data.bookingConfirmation.reservationByIdList.toNumberOfChildren(),
                numberOfNights = data.bookingConfirmation.reservationByIdList[0].roomStay.mapToNumberOfNights(),
                bookerDetails = createBookerDetailsWithThirdPartyFallback(
                    data.bookingConfirmation.reservationByIdList[0],
                    data.bookingConfirmation.isThirdPartyBooking
                ),
                guestRooms = data.bookingConfirmation.mapToGuestRooms(countries),
                rooms = this.data.bookingConfirmation.reservationByIdList.toListOfRooms(),
                reservationGuests = this.data.bookingConfirmation.reservationByIdList[0].reservationGuestList
                    .map { it.toReservationGuestDomain(this.data.bookingConfirmation.reservationByIdList[0].reservationId) },
                preferences = this.data.bookingConfirmation.reservationByIdList.flatMap {
                    it.preferences ?: emptyList()
                }.distinct().map { it.mapToHotelPreferenceDomain() },
                preCheckInStatus = this.data.bookingConfirmation.reservationByIdList[0].preCheckInStatus ?: false
            ),
            isCheckInOnlineAvailable = this.data.manageBooking?.isCheckInOnlineAvailable ?: false,
            isCheckOutOnlineAvailable = this.data.manageBooking?.isCheckOutOnlineAvailable ?: false,
            basketStatus = this.data.bookingConfirmation.basketStatus,
            rateDescription = this.data.bookingConfirmation.reservationByIdList[0].roomStay.rateExtraInfo?.rateDescription ?: EMPTY_STRING_DOMAIN,
            rateCode = this.data.bookingConfirmation.reservationByIdList[0].roomStay.rateExtraInfo?.rateClassification ?: EMPTY_STRING_DOMAIN,
            isEmployeeBooking = this.data.bookingConfirmation.reservationByIdList[0].roomStay.rateExtraInfo?.rateClassification == EMPLOYEE_RATE_PLAN_CODE,
            isThirdPartyBooking = this.data.bookingConfirmation.isThirdPartyBooking,
            paymentOption = this.data.bookingConfirmation.paymentOption ?: EMPTY_STRING_DOMAIN,
            upsellsAddonsEnabled = this.data.bookingConfirmation.upsellsAddonsEnabled
        )
    } ?: Booking(
        bookingReference = EMPTY_STRING_DOMAIN,
        leadGuestFullName = EMPTY_STRING_DOMAIN,
        arrivalDate = LocalDate.now(),
        departureDate = LocalDate.now(),
        hotelCode = EMPTY_STRING,
        hotelName = hotelName ?: EMPTY_STRING_DOMAIN,
        numberOfRooms = -1,
        numberOfGuests = 0,
        leadGuestSurname = EMPTY_STRING,
        rateType = EMPTY_STRING,
        totalCost = PriceDomain.createDefault(),
        prepaidAmount = null,
        //PriceDomain(this.data.bookingConfirmation.totalCost.toFloat(),
        //  this.data.bookingConfirmation.currencyCode ?: GBP), // not migrated
        amendable = this.data.manageBooking?.isAmendable ?: false,
        cancellable = this.data.manageBooking?.isCancellable ?: false,
        isCancelled = false, // not migrated
        isLinkedToAccount = false, // not migrated
        cardFeeApplies = false,
        details = null,
        upsells = emptyList(),
        amendRestrictions = AmendRestrictions.Companion.createWithDefaults(
            nights = false,
            rooms = false,
            guestNames = false,
            upsell = false,
            restricted = false
        ),
        booker = Guest(
            roomNumber = 1,
            roomId = EMPTY_STRING,
            title = EMPTY_STRING,
            firstName = EMPTY_STRING,
            lastName = EMPTY_STRING,
            guestHistoryNumber = EMPTY_STRING_DOMAIN,
            address = null,
            phoneNumber = EMPTY_STRING,
            emailAddress = EMPTY_STRING
        ),
        rooms = null,
        isCheckInOnlineAvailable = this.data.manageBooking?.isCheckInOnlineAvailable ?: false,
        isCheckOutOnlineAvailable = this.data.manageBooking?.isCheckOutOnlineAvailable ?: false,
        basketStatus = null,
        isThirdPartyBooking = false,
        paymentOption = EMPTY_STRING_DOMAIN,
        upsellsAddonsEnabled = false
    )
}

private fun createBookerDetailsWithThirdPartyFallback(
    reservation: BookingConfirmationGraphQLContract.ReservationByIdConfirmation,
    isThirdPartyBooking: Boolean
): LeadBookerDetails {
    val billing = reservation.billing
    return if (isThirdPartyBooking && billing == null) {
        val leadGuest = reservation.reservationGuestList.firstOrNull { !it.isAccompanyingGuest }

        LeadBookerDetails(
            leadGuest?.title ?: TITLE_MR,
            leadGuest?.givenName ?: EMPTY_STRING_DOMAIN,
            leadGuest?.surName ?: EMPTY_STRING_DOMAIN,
            EMPTY_STRING_DOMAIN,
            EMPTY_STRING_DOMAIN,
            leadGuest?.address?.mapToGuestAddress() ?: LeadBookerAddress.empty()
        )
    } else {
        LeadBookerDetails(
            billing?.title ?: TITLE_MR,
            billing?.firstName ?: EMPTY_STRING_DOMAIN,
            billing?.lastName ?: EMPTY_STRING_DOMAIN,
            billing?.email ?: EMPTY_STRING_DOMAIN,
            billing?.telephone ?: EMPTY_STRING_DOMAIN,
            LeadBookerAddress(
                billing?.address?.addressLine1 ?: EMPTY_STRING_DOMAIN,
                billing?.address?.addressLine2 ?: EMPTY_STRING_DOMAIN,
                billing?.address?.addressLine3 ?: EMPTY_STRING_DOMAIN,
                billing?.address?.addressLine4 ?: EMPTY_STRING_DOMAIN,
                billing?.address?.postalCode ?: EMPTY_STRING_DOMAIN,
                billing?.address?.cityName ?: EMPTY_STRING_DOMAIN,
                billing?.address?.country ?: EMPTY_STRING_DOMAIN
            )
        )
    }
}

private fun createBookerGuestWithThirdPartyFallback(
    reservation: BookingConfirmationGraphQLContract.ReservationByIdConfirmation,
    bookingReference: String,
    isThirdPartyBooking: Boolean
): Guest {
    val billing = reservation.billing

    return if (isThirdPartyBooking && billing == null) {
        val leadGuest = reservation.reservationGuestList.firstOrNull { !it.isAccompanyingGuest }

        Guest(
            roomNumber = 1,
            roomId = EMPTY_STRING,
            title = leadGuest?.title ?: TITLE_MR,
            firstName = leadGuest?.givenName ?: EMPTY_STRING_DOMAIN,
            lastName = leadGuest?.surName ?: EMPTY_STRING_DOMAIN,
            guestHistoryNumber = bookingReference,
            address = leadGuest?.address?.mapToAddress(),
            phoneNumber = null,
            emailAddress = leadGuest?.email
        )
    } else {
        Guest(
            roomNumber = 1,
            roomId = EMPTY_STRING,
            title = billing?.title ?: TITLE_MR,
            firstName = billing?.firstName ?: EMPTY_STRING_DOMAIN,
            lastName = billing?.lastName ?: EMPTY_STRING_DOMAIN,
            guestHistoryNumber = bookingReference,
            address = billing?.address?.mapToAddress(),
            phoneNumber = billing?.telephone,
            emailAddress = billing?.email
        )
    }
}

fun BookingConfirmationGraphQLContract.BookingConfirmation.mapToBookingConfirmationDomain() =
    BookingConfirmation(
        bookingReference = bookingReference,
        balanceOutstanding = balanceOutstanding,
        bookingFlowID = bookingFlowID,
        currencyCode = currencyCode,
        hotelId = hotelId,
        infoMessages = infoMessages,
        newTotal = newTotal,
        policyCode = policyCode,
        previousTotal = previousTotal,
        reservationByIdList = reservationByIdList.map { it.mapToReservationByIdDomain() },
        totalCost = totalCost,
        basketStatus = basketStatus
    )

fun BookingConfirmationGraphQLContract.ReservationByIdConfirmation.mapToReservationByIdDomain() =
    ReservationById(
        reservationId = reservationId,
        reservationStatus = reservationStatus,
        reservationGuestList = reservationGuestList.map { it.toReservationGuestDomain(reservationId) },
        reservationPackageList = reservationPackageList.map { it.mapToReservationPackageDomain() },
        roomStay = roomStay.mapToRoomStayDomain(),
        purposeOfStay = additionalGuestInfo?.purposeOfStay ?: EMPTY_STRING_DOMAIN
    )

fun BookingConfirmationGraphQLContract.ReservationPackagesDetails.mapToReservationPackageDomain() =
    ReservationPackage(
        description = description,
        packageCode = packageCode,
        unitPrice = unitPrice,
        totalQuantity = totalQuantity,
        computedPrice = computedPrice
    )

fun BookingConfirmationGraphQLContract.RoomStayConfirmation.mapToRoomStayDomain() =
    RoomStay(
        adultsNumber = adultsNumber,
        childrenNumber = childrenNumber,
        cot = cot,
        roomType = roomType,
        ratePlanCode = ratePlanCode,
        arrivalDate = arrivalDate,
        departureDate = departureDate,
        roomPrice = roomPrice
    )

fun BookingConfirmationAndAmendSummaryGraphQLContract.BookingConfirmationAndAmendSummaryData.mapToBookingDomain(
    hotelName: String?
): Booking {
    var calculatedPrepaidAmount: PriceDomain?

    return this.data.bookingConfirmation?.let {
        calculatedPrepaidAmount = if (this.data.bookingConfirmation.balanceOutstanding == 0.0f) {
            PriceDomain(this.data.bookingConfirmation.totalCost,
                this.data.bookingConfirmation.currencyCode ?: GBP)
        } else {
            null
        }
        Booking(
            bookingReference = this.data.bookingConfirmation.bookingReference,
            leadGuestFullName = this.data.bookingConfirmation.retrieveGuestName(),
            arrivalDate = LocalDate.parse(this.data.bookingConfirmation.reservationByIdList[0].roomStay.arrivalDate),
            departureDate = LocalDate.parse(this.data.bookingConfirmation.reservationByIdList[0].roomStay.departureDate),
            hotelCode = this.data.bookingConfirmation.hotelId,
            hotelName = hotelName ?: EMPTY_STRING_DOMAIN,
            numberOfRooms = this.data.bookingConfirmation.reservationByIdList.size,
            numberOfGuests = this.data.bookingConfirmation.reservationByIdList.toNumberOfGuests(),
            leadGuestSurname = this.data.bookingConfirmation.reservationByIdList[0].reservationGuestList[0].surName,
            rateType = this.data.bookingConfirmation.reservationByIdList[0].roomStay.rateExtraInfo?.rateName ?: EMPTY_STRING_DOMAIN,
            totalCost = PriceDomain(this.data.bookingConfirmation.totalCost,
                this.data.bookingConfirmation.currencyCode
            ),
            prepaidAmount = calculatedPrepaidAmount,
            amendable = this.data.manageBooking?.isAmendable == true,
            cancellable = this.data.manageBooking?.isCancellable == true,
            isCancelled = this.data.bookingConfirmation.reservationByIdList[0].reservationStatus.mapReservationStatus(),
            isLinkedToAccount = false, // not migrated
            cardFeeApplies = false,
            details = Booking.Details(false, PriceDomain.createDefault(),
                this.data.bookingConfirmation.reservationByIdList[0].roomStay.rateExtraInfo?.rateName ?: EMPTY_STRING_DOMAIN),
            upsells = this.data.bookingConfirmation.toListOfUpsells(),
            amendRestrictions = AmendRestrictions.Companion.createWithDefaults(
                nights = false,
                rooms = false,
                guestNames = false,
                upsell = false,
                restricted = false
            ),
            booker = createBookerGuestWithThirdPartyFallback(
                this.data.bookingConfirmation.reservationByIdList[0],
                this.data.bookingConfirmation.bookingReference,
                this.data.bookingConfirmation.isThirdPartyBooking
            ),
            rooms = this.data.bookingConfirmation.reservationByIdList.toListOfRooms(this.data.bookingConfirmation.bookingReference),
            rateDescription = this.data.bookingConfirmation.reservationByIdList[0].roomStay.rateExtraInfo?.rateDescription ?: EMPTY_STRING_DOMAIN,
            rateCode = this.data.bookingConfirmation.reservationByIdList[0].roomStay.rateExtraInfo?.rateClassification ?: EMPTY_STRING_DOMAIN,
            isEmployeeBooking = this.data.bookingConfirmation.reservationByIdList[0].roomStay.rateExtraInfo?.rateClassification == EMPLOYEE_RATE_PLAN_CODE,
            isThirdPartyBooking = this.data.bookingConfirmation.isThirdPartyBooking,
            paymentOption = this.data.bookingConfirmation.paymentOption ?: EMPTY_STRING_DOMAIN,
            upsellsAddonsEnabled = this.data.bookingConfirmation.upsellsAddonsEnabled
        )
    } ?: Booking(
        bookingReference = EMPTY_STRING_DOMAIN,
        leadGuestFullName = EMPTY_STRING_DOMAIN,
        arrivalDate = LocalDate.now(),
        departureDate = LocalDate.now(),
        hotelCode = EMPTY_STRING,
        hotelName = hotelName ?: EMPTY_STRING_DOMAIN,
        numberOfRooms = -1,
        numberOfGuests = 0,
        leadGuestSurname = EMPTY_STRING,
        rateType = EMPTY_STRING,
        totalCost = PriceDomain.createDefault(),
        balanceOutstanding = PriceDomain.createDefault(),
        prepaidAmount = null,
        //PriceDomain(this.data.bookingConfirmation.totalCost.toFloat(),
        //  this.data.bookingConfirmation.currencyCode ?: GBP), // not migrated
        amendable = this.data.manageBooking?.isAmendable == true,
        cancellable = this.data.manageBooking?.isCancellable == true,
        isCancelled = false, // not migrated
        isLinkedToAccount = false, // not migrated
        cardFeeApplies = false,
        details = null,
        upsells = emptyList(),
        amendRestrictions = AmendRestrictions.Companion.createWithDefaults(
            nights = false,
            rooms = false,
            guestNames = false,
            upsell = false,
            restricted = false
        ),
        booker = Guest(
            roomNumber = 1,
            roomId = EMPTY_STRING,
            title = EMPTY_STRING,
            firstName = EMPTY_STRING,
            lastName = EMPTY_STRING,
            guestHistoryNumber = EMPTY_STRING_DOMAIN,
            address = null,
            phoneNumber = EMPTY_STRING,
            emailAddress = EMPTY_STRING
        ),
        rooms = null,
        isThirdPartyBooking = false,
        paymentOption = EMPTY_STRING_DOMAIN,
        upsellsAddonsEnabled = false
    )
}

fun BookingConfirmationGraphQLContract.GuestInfo.toReservationGuestDomain(reservationId: String) =
    ReservationGuest(
        profileId = profileId,
        reservationId = reservationId,
        firstName = givenName,
        lastName = surName,
        title = title,
        email = email,
        dateOfBirth = additionalDetails?.dob,
        passportNumber = additionalDetails?.passportNumber,
        nationality = additionalDetails?.nationality,
        isAccompanyingGuest = isAccompanyingGuest,
        address = address?.mapToGuestAddress()
    )

fun List<BookingConfirmationGraphQLContract.ReservationByIdConfirmation>.toNumberOfGuests() : Int {
    var totalGuests = 0
    this.forEach { guest->
        totalGuests = guest.roomStay.adultsNumber.toInt() + guest.roomStay.childrenNumber.toInt()
    }

    return totalGuests
}

fun  BookingConfirmationGraphQLContract. RoomStayConfirmation.mapToNumberOfNights() =
    ChronoUnit.DAYS.between(LocalDate.parse(arrivalDate), LocalDate.parse(departureDate)).toInt()


fun BookingConfirmationGraphQLContract.BookingConfirmation.mapToGuestRooms(countries: List<CountryDomain>): List<GuestsRoom> {
    val resultList = ArrayList<GuestsRoom>()

    this.reservationByIdList.forEach { reservation ->
        var guestsRoom = GuestsRoom(
            roomId = reservation.reservationId,
            numberOfAdults = reservation.roomStay.adultsNumber.toInt(),
            numberOfChildren = reservation.roomStay.childrenNumber.toInt(),
            purposeOfStay = reservation.additionalGuestInfo?.purposeOfStay ?: EMPTY_STRING
        )

        reservation.reservationGuestList.map { roomGuest ->
            if (!roomGuest.isAccompanyingGuest) {
                // add Lead Guest to RoomGuests
                guestsRoom = guestsRoom.copy(
                    leadGuestTitle = roomGuest.title ?: TITLE_MR,
                    leadGuestFirstName = roomGuest.givenName,
                    leadGuestLastName = roomGuest.surName,
                    leadGuestNationality = roomGuest.additionalDetails?.nationality ?: EMPTY_STRING,
                    isLeadGuestPassportNumberRequired = countries.firstOrNull { it.countryName == roomGuest.additionalDetails?.nationality }?.requiresPassportInfo
                        ?: countries.firstOrNull { it.countryIsoCode == roomGuest.additionalDetails?.nationality }?.requiresPassportInfo
                        ?: true,
                    // Don't map this as it doesn't come in plaintext format
                    leadGuestPassportNumber = EMPTY_STRING // roomGuest.additionalDetails.passportNumber ?: EMPTY_STRING,
                )
            } else {
                // add Accompanying Guest details
                guestsRoom = guestsRoom.copy(
                    accompanyingGuestTitle = roomGuest.title ?: TITLE_MR,
                    accompanyingGuestFirstName = roomGuest.givenName,
                    accompanyingGuestLastName = roomGuest.surName,
                    accompanyingGuestNationality = roomGuest.additionalDetails?.nationality ?: EMPTY_STRING,
                    isAccompanyingGuestPassportNumberRequired = countries.firstOrNull { it.countryName == roomGuest.additionalDetails?.nationality }?.requiresPassportInfo
                        ?: countries.firstOrNull { it.countryIsoCode == roomGuest.additionalDetails?.nationality }?.requiresPassportInfo
                        ?: true,
                    // Don't map this as it doesn't come in plaintext format
                    accompanyingGuestPassportNumber = EMPTY_STRING
                )
            }
        }

        resultList.add(guestsRoom)
    }
    return resultList
}

fun List<BookingConfirmationGraphQLContract.ReservationByIdConfirmation>.toNumberOfAdults() : Int {
    var totalAdults = 0
    this.forEach { guest->
        totalAdults += guest.roomStay.adultsNumber.toInt()
    }

    return totalAdults
}

fun List<BookingConfirmationGraphQLContract.ReservationByIdConfirmation>.toNumberOfChildren() : Int {
    var totalChildren = 0
    this.forEach { guest->
        totalChildren += guest.roomStay.childrenNumber.toInt()
    }

    return totalChildren
}

fun List<BookingConfirmationGraphQLContract.ReservationByIdConfirmation>.toListOfRooms(basketReference: String) : List<Booking.Room> {
    val listOfBookingRoom = mutableListOf<Booking.Room>()
    this.forEach { guest->
        listOfBookingRoom.add(guest.mapToRoomsList(basketReference))
    }

    return listOfBookingRoom
}

fun List<BookingConfirmationGraphQLContract.ReservationByIdConfirmation>.toListOfRooms() : List<Rooms> {
    val listOfBookingRoom = mutableListOf<Rooms>()
    this.forEach { guest->
        listOfBookingRoom.add(
            Rooms(
                adultsNumber = guest.roomStay.adultsNumber.toInt(),
                rate = guest.roomStay.ratePlanCode,
                type = guest.roomStay.roomType,
            )
        )
    }

    return listOfBookingRoom
}

fun BookingConfirmationGraphQLContract.Address.mapToAddress(): Address {
    return Address(
        line1 = this.addressLine1 ?: EMPTY_STRING,
        line2 = this.addressLine2,
        line3 = this.addressLine3,
        line4 = this.addressLine4,
        postCode = this.postalCode,
        countryCode = this.country
    )
}

fun BookingConfirmationGraphQLContract.Address.mapToGuestAddress(): LeadBookerAddress {
    return LeadBookerAddress(
        addressLine1 = this.addressLine1 ?: EMPTY_STRING,
        addressLine2 = this.addressLine2 ?: EMPTY_STRING,
        addressLine3 = this.addressLine3 ?: EMPTY_STRING,
        addressLine4 = this.addressLine4 ?: EMPTY_STRING,
        cityName = this.cityName ?: EMPTY_STRING,
        postalCode = this.postalCode ?: EMPTY_STRING,
        country = this.countryCode ?: EMPTY_STRING,
    )
}

fun BookingConfirmationGraphQLContract.ReservationByIdConfirmation.mapToRoomsList(basketReference: String):
        Booking.Room {
    return Booking.Room(
        roomId = reservationId,
        roomType = this.roomStay.roomType.toRoomTypeGQL(),
        lettingType = this.roomStay.roomType,
        numberOfAdults = this.roomStay.adultsNumber.toInt(),
        numberOfChildren = this.roomStay.childrenNumber.toInt(),
        leadGuestInfo =  LeadGuest(
            Guest(
                lastName =  this.reservationGuestList[0].surName,
                firstName = this.reservationGuestList[0].givenName,
                guestHistoryNumber = basketReference,
                address = this.billing?.address?.mapToAddress(),
                phoneNumber = this.billing?.telephone,
                emailAddress = this.reservationGuestList[0].email ?: this.billing?.email,
                title = this.reservationGuestList[0].title ?: TITLE_MR
            ),
            passport = null,
            nextDestination = EMPTY_STRING,
            nationalityCountryCode = EMPTY_STRING
        ),
    )
}

fun BookingConfirmationGraphQLContract.HotelPreference.mapToHotelPreferenceDomain() = HotelPreferenceDomain(
    code = code,
    preferenceGroup = preferenceCode,
    label = EMPTY_STRING
)

fun BookingConfirmationGraphQLContract.BookingConfirmation.retrieveGuestName(): String {
    val guest = this.reservationByIdList[0].reservationGuestList[0]
    return "${guest.title ?: TITLE_MR} ${guest.givenName} ${guest.surName}"
}

fun BookingConfirmationGraphQLContract.BookingConfirmation.toListOfUpsells(): List<Upsell>{
    val listOfUpsell = mutableListOf<Upsell>()
    val postingDate = this.reservationByIdList.first().roomStay.departureDate
    if (this.reservationByIdList.first().reservationPackageList.isEmpty()) return listOfUpsell
    for ((index, reservationById) in this.reservationByIdList.withIndex()) {

        for (eachUpsell in reservationById.reservationPackageList) {
            if (eachUpsell.packageCode !in EXTRAS_LIST) {
                listOfUpsell.add(Upsell(
                    quantity = eachUpsell.totalQuantity,
                    category = Upsell.Category.BREAKFAST,
                    legend = eachUpsell.description,
                    unitCost = eachUpsell.computedPrice.toPriceDomain(this.currencyCode),
                    code = eachUpsell.packageCode,
                    roomId = reservationById.reservationId,
                    postingDate = postingDate.toLocalDate()
                ))
            }
            else {
                listOfUpsell.add(Upsell(
                    quantity = eachUpsell.totalQuantity,
                    category = Upsell.Category.OTHER,
                    legend = eachUpsell.description,
                    unitCost = eachUpsell.computedPrice.toPriceDomain(this.currencyCode),
                    code = eachUpsell.packageCode,
                    roomId = reservationById.reservationId,
                    postingDate = postingDate.toLocalDate()
                ))
            }

        }
    }

    return listOfUpsell
}