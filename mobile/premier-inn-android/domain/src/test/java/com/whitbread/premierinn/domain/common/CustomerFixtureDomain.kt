package com.whitbread.premierinn.domain.common

import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Contact
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.FullName
import com.whitbread.premierinn.domain.customer.entity.PaymentCard

object CustomerFixtureDomain {
    @JvmOverloads
    fun aCustomer(
            id: String = "CUSTOMER01",
            fullName: FullName = FullName(title = "Mr", firstName = "Richie", lastName = "Rich"),
            address: Address = Address(line1 = "Money Lane", countryCode = "GB", postCode = "N1 7BD"),
            contact: Contact = Contact(email = "rr@rich.com", mobile = "05648383833"),
            nationality: String = "GB",
            passport: Passport = Passport("8998877788", "GB"),
            business: Boolean = false,
            bookingPreferences: BookingPreferences = BookingPreferences(mealPreference = 11,
                    roomCriteriaPreference = RoomCriteria(numberOfAdults = 2, numberOfChildren = 1,
                            numberOfInfants = 0, includeCot = true, roomType = RoomType.FAMILY)),
            paymentCard: PaymentCard = PaymentCard(number = "************1111",
                    expiryDate = "06/23",
                    holdersFullName = "Android Bot",
                    cardType = ""),
            carRegistration: String = "GBVHYI9889",
            customerID: String = "234234234",): Customer {
        return Customer(
                id,
                fullName,
                contact,
                address,
                nationality,
                passport,
                business,
                bookingPreferences,
                paymentCard,
                carRegistration,
                customerID)
    }
}