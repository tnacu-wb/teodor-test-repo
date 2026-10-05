package com.whitbread.premierinn.data.booking

import com.whitbread.premierinn.data.booking.entity.BookingEntity
import com.whitbread.premierinn.data.booking.entity.BookingWithRooms
import com.whitbread.premierinn.data.booking.entity.RoomEntity
import com.whitbread.premierinn.data.booking.entity.RoomEntityGuest
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.mapToPrice
import com.whitbread.premierinn.data.common.mapToRestriction
import com.whitbread.premierinn.data.remote.ApiCommon
import com.whitbread.premierinn.data.remote.ReservationApiContract
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.LeadGuest

fun ReservationApiContract.ReservationDetails?.numberOfGuests(): Int {
    var totalGuests = 0
    this?.let {
        rooms?.let {
            for (room in rooms) {
                room?.let {
                    room.adults?.let {
                        totalGuests += room.adults
                    }
                    room.children?.let {
                        totalGuests += room.children
                    }
                }
            }
        }
    }
    return totalGuests
}

fun ApiCommon.Address?.mapToAddress(): Address? {
    return this?.let {
        Address(
            line1 = it.line1.orEmpty(),
            line2 = it.line2,
            line3 = it.line3,
            line4 = it.line4,
            line5 = it.line5,
            postCode = it.postcode,
            companyName = it.companyName,
            countryCode = it.countryCode
        )
    }
}

fun Booking.mapToBookingEntity(): BookingEntity {
    return BookingEntity(
        bookingReference = this.bookingReference,
        leadGuestSurname = this.leadGuestSurname,
        arrivalDate = this.arrivalDate,
        departureDate = this.departureDate,
        hotelCode = this.hotelCode,
        hotelName = this.hotelName,
        numberOfRooms = this.numberOfRooms,
        leadGuestFullName = this.leadGuestFullName,
        rateClass = this.rateType,
        prePaidAmount = this.prepaidAmount.mapToPrice(),
        totalCost = this.totalCost.mapToPrice(),
        amendable = this.amendable,
        cancellable = this.cancellable,
        isCanceled = this.isCancelled,
        isLinkedToAccount = isLinkedToAccount,
        amendRestrictions = this.amendRestrictions.mapToRestriction(),
        businessBooking = this.isBusinessBooking,
        bookingStatus = this.bookingStatus,
        isCheckInOnlineAvailable = this.isCheckInOnlineAvailable,
        isCheckOutOnlineAvailable = this.isCheckOutOnlineAvailable,
        basketStatus = this.basketStatus,
        hotelCountry = this.hotelCountry,
        employeeBooking = this.isEmployeeBooking,
        isThirdPartyBooking = this.isThirdPartyBooking
    )
}

private fun Booking.Room.mapToRoomEntity(bookingReference: String): RoomEntity {
    return RoomEntity(
        bookingReference = bookingReference,
        roomId = roomId,
        roomType = roomType,
        lettingType = lettingType ?: EMPTY_STRING,
        numberOfAdults = numberOfAdults,
        numberOfChildren = numberOfChildren,
        leadGuest = RoomEntityGuest(
            title = leadGuestInfo.guest.title,
            firstName = leadGuestInfo.guest.firstName,
            lastName = leadGuestInfo.guest.lastName
        )
    )
}

fun BookingEntity.mapToBooking(rooms: List<Booking.Room>): Booking {
    return Booking(
        bookingReference = this.bookingReference,
        leadGuestSurname = this.leadGuestSurname,
        hotelCode = this.hotelCode,
        hotelName = this.hotelName,
        arrivalDate = this.arrivalDate,
        departureDate = this.departureDate,
        rateType = this.rateClass,
        numberOfRooms = this.numberOfRooms,
        leadGuestFullName = this.leadGuestFullName,
        prepaidAmount = this.prePaidAmount.mapToPrice(),
        totalCost = this.totalCost.mapToPrice(),
        cardFeeApplies = false,
        amendable = this.amendable,
        cancellable = this.cancellable,
        isCancelled = this.isCanceled,
        isLinkedToAccount = this.isLinkedToAccount,
        amendRestrictions = this.amendRestrictions.mapToRestriction(),
        rooms = rooms,
        isBusinessBooking = this.businessBooking,
        bookingStatus = this.bookingStatus,
        isCheckInOnlineAvailable = this.isCheckInOnlineAvailable,
        isCheckOutOnlineAvailable = this.isCheckOutOnlineAvailable,
        basketStatus = this.basketStatus,
        hotelCountry = this.hotelCountry,
        isEmployeeBooking = this.employeeBooking,
        isThirdPartyBooking = this.isThirdPartyBooking
    )
}

fun BookingWithRooms.mapToBooking(): Booking {
    return Booking(
        bookingReference = booking.bookingReference,
        leadGuestSurname = booking.leadGuestSurname,
        hotelCode = booking.hotelCode,
        hotelName = booking.hotelName,
        arrivalDate = booking.arrivalDate,
        departureDate = booking.departureDate,
        rateType = booking.rateClass,
        numberOfRooms = booking.numberOfRooms,
        leadGuestFullName = booking.leadGuestFullName,
        prepaidAmount = booking.prePaidAmount.mapToPrice(),
        totalCost = booking.totalCost.mapToPrice(),
        cardFeeApplies = false,
        amendable = booking.amendable,
        cancellable = booking.cancellable,
        isCancelled = booking.isCanceled,
        isLinkedToAccount = booking.isLinkedToAccount,
        amendRestrictions = booking.amendRestrictions.mapToRestriction(),
        rooms = rooms.map { roomEntity -> roomEntity.mapToRoom() },
        isBusinessBooking = booking.businessBooking,
        bookingStatus = booking.bookingStatus,
        isCheckInOnlineAvailable = booking.isCheckInOnlineAvailable,
        isCheckOutOnlineAvailable = booking.isCheckOutOnlineAvailable,
        basketStatus = booking.basketStatus,
        hotelCountry = booking.hotelCountry,
        isEmployeeBooking = booking.employeeBooking,
        isThirdPartyBooking = booking.isThirdPartyBooking
    )
}

private fun RoomEntity.mapToRoom(): Booking.Room {
    return Booking.Room(
        roomId = roomId,
        roomType = roomType,
        lettingType = EMPTY_STRING_DOMAIN, //TODO: in another ticket,
        numberOfAdults = numberOfAdults,
        numberOfChildren = numberOfChildren,
        leadGuestInfo = LeadGuest(
            guest = leadGuest.mapToGuest(),
            nationalityCountryCode = null,
            carRegistration = null,
            passport = null,
            nextDestination = null
        )
    )
}

private fun RoomEntityGuest.mapToGuest(): Guest {
    return Guest(
        title = title,
        firstName = firstName,
        lastName = lastName,
        address = null,
        phoneNumber = null,
        guestHistoryNumber = null,
        emailAddress = null
    )
}

fun Booking.mapToBookingWithRooms(): BookingWithRooms {
    val bookingWithRooms = BookingWithRooms()
    bookingWithRooms.booking = mapToBookingEntity()
    bookingWithRooms.rooms = rooms.orEmpty().map { room -> room.mapToRoomEntity(bookingReference) }
    return bookingWithRooms
}

/**
 * Converting List[BookingEntity] to List[Booking]
 * **/
fun List<BookingEntity>.toBooking(): List<Booking> {
    return this.map {
        it.mapToBooking(emptyList())
    }
}