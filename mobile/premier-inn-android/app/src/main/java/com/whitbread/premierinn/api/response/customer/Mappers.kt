package com.whitbread.premierinn.api.response.customer

import com.whitbread.premierinn.data.common.ADDRESS_TYPE_BUSINESS
import com.whitbread.premierinn.data.common.ADDRESS_TYPE_HOME
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.postcodefinder.toParcelable
import com.whitbread.premierinn.roomcriteria.ParcelableRoomCriteria

fun Customer.toParcelable(): ParcelableCustomer {
    return ParcelableCustomer(
        customerAccountID = customerAccountID ?: EMPTY_STRING,
        guestHistoryNumber = guestHistoryNumber,
        fullName = ParcelableFullName(
            title = fullName.title,
            firstName = fullName.firstName,
            lastName = fullName.lastName
        ),
        contact = ParcelableContact(
            email = contact.email,
            mobile = contact.mobile,
            telephone = contact.telephone
        ),
        address = Address(
            line1 = address.line1,
            line2 = address.line2,
            line3 = address.line3,
            line4 = address.line4,
            line5 = address.line5,
            postCode = address.postCode,
            companyName = address.companyName,
            countryCode = address.countryCode
        ).toParcelable(),
        nationality = nationality,
        passport = Passport.builder()
            .number(passport?.number ?: EMPTY_STRING)
            .countryOfIssue(passport?.placeOfIssue ?: EMPTY_STRING)
            .build(),
        paymentCard = paymentCard?.let { card ->
            ParcelablePaymentCard(
                number = card.number,
                cardType = card.cardType,
                holdersFullName = card.holdersFullName,
                expiryDate = card.expiryDate
            )
        },
        carRegistration = carRegistration,
        isBusiness = businessUse,
        bookingPreferences = this.bookingPreferences.toBookingPreferencesParcelable(),
        companyId = companyId,
        guestHistoryCreation = this.guestHistoryCreation,
        totalStays = this.totalStays,
    )
}

fun com.whitbread.premierinn.domain.customer.entity.BookingPreferences?.toBookingPreferencesParcelable(): BookingPreferences? {
    return if (this != null) {
        BookingPreferences(
            mealPreference = this.mealPreference,
            roomCriteriaPreference = this.roomCriteriaPreference.toRoomCriteriaPreferenceParcelable()
        )
    } else null
}

fun RoomCriteria?.toRoomCriteriaPreferenceParcelable(): ParcelableRoomCriteria? {
    return if (this != null) {
        ParcelableRoomCriteria(
            numberOfAdults = this.numberOfAdults,
            numberOfChildren = this.numberOfChildren,
            numberOfInfants = this.numberOfInfants,
            includeCot = this.includeCot,
            roomNumber = this.roomNumber,
            roomType = this.roomType,
            roomId = this.roomId,
            hotelBrand = this.hotelBrand
        )
    } else null
}

fun ParcelableCustomer.toDomain(): Customer {
    return Customer(
        customerAccountID = customerAccountID,
        guestHistoryNumber = guestHistoryNumber,
        guestHistoryCreation = guestHistoryCreation,
        totalStays = totalStays,
        fullName = com.whitbread.premierinn.domain.customer.entity.FullName(
            title = fullName.title,
            firstName = fullName.firstName,
            lastName = fullName.lastName
        ),
        contact = com.whitbread.premierinn.domain.customer.entity.Contact(
            email = contact.email,
            mobile = contact.mobile,
            telephone = contact.telephone
        ),
        address = Address(
            line1 = address.line1,
            line2 = address.line2,
            line3 = address.line3,
            line4 = address.line4,
            line5 = address.line5,
            postCode = address.postcode,
            companyName = address.companyName,
            countryCode = address.countryCode
        ),
        paymentCard = paymentCard?.let { card ->
            PaymentCard(
                number = card.number,
                cardType = card.cardType,
                expiryDate = card.expiryDate,
                holdersFullName = card.holdersFullName
            )
        },
        nationality = nationality,
        passport = com.whitbread.premierinn.domain.common.Passport(
            passport?.number()!!,
            passport.countryOfIssue()
        ),
        carRegistration = carRegistration,
        businessUse = isBusiness,
        bookingPreferences = com.whitbread.premierinn.domain.customer.entity.BookingPreferences(
            mealPreference = bookingPreferences!!.mealPreference,
            roomCriteriaPreference = RoomCriteria(
                numberOfAdults = bookingPreferences.roomCriteriaPreference!!.numberOfAdults,
                numberOfChildren = bookingPreferences.roomCriteriaPreference.numberOfChildren,
                numberOfInfants = bookingPreferences.roomCriteriaPreference.numberOfInfants,
                includeCot = bookingPreferences.roomCriteriaPreference.includeCot,
                roomNumber = bookingPreferences.roomCriteriaPreference.roomNumber,
                roomType = bookingPreferences.roomCriteriaPreference.roomType,
                roomId = bookingPreferences.roomCriteriaPreference.roomId!!,
                hotelBrand = bookingPreferences.roomCriteriaPreference.hotelBrand!!
            )
        ),
        companyId = companyId
    )

}


fun Customer.toContactDetail(): ContactDetail {
    return ContactDetail.builder()
        .address(
            CustomerAddress.builder()
                .companyName(this.company?.requestedCompany?.companyDetails?.companyName)
                .countryCode(this.address.countryCode.orEmpty())
                .line1(this.address.line1)
                .line2(this.address.line2)
                .line3(this.address.line3)
                .line4(this.address.line4)
                .line5(this.address.line5)
                .postCode(this.address.postCode.orEmpty())
                .type(if (this.address.isWorkAddress()) ADDRESS_TYPE_BUSINESS else ADDRESS_TYPE_HOME)
                .build()
        )
        .title(this.fullName.title)
        .lastName(this.fullName.lastName)
        .firstName(this.fullName.firstName)
        .telephone(this.contact.telephone)
        .carRegistration(this.carRegistration)
        .mobile(this.contact.mobile)
        .nationality(this.nationality)
        .email(this.contact.email)
        .passport(
            Passport.builder()
                .countryOfIssue(this.passport?.placeOfIssue)
                .number(this.passport?.number)
                .build()
        )
        .build()
}
