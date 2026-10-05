package com.whitbread.premierinn.domain.customer.entity

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.businessbooker.domain.company.Company
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.Passport
import com.whitbread.premierinn.domain.common.RoomCriteria

private const val BUSINESS_CARD = "AT"
private const val BUSINESS_CARD_EURO = "BD"


data class Customer @JvmOverloads constructor(
        @SerializedName("a") val guestHistoryNumber: String = EMPTY_STRING_DOMAIN, //aka guestHistoryNumber
        @SerializedName("b") val fullName: FullName,
        @SerializedName("c") val contact: Contact,
        @SerializedName("d") val address: Address,
        @SerializedName("e") val nationality: String? = null,
        @SerializedName("f") val passport: Passport? = null,
        @SerializedName("g") val businessUse: Boolean = false,
        @SerializedName("h") val bookingPreferences: BookingPreferences,
        @SerializedName("i") val paymentCard: PaymentCard? = null,
        @SerializedName("j") val carRegistration: String? = null,
        @SerializedName("k") val companyId: String? = null, //available only for business users
        @SerializedName("l") val business: Business? = null, //available only for business users
        @SerializedName("m") val company: Company? = null, //available only for business users
        @SerializedName("n") val guestHistoryCreation: String? = null,
        @SerializedName("o") val totalStays: Int = 0,
        @SerializedName("p") val sessionId: String? = EMPTY_STRING_DOMAIN,
        @SerializedName("q") val customerAccountID: String? = EMPTY_STRING_DOMAIN, //user id / customer id
        @SerializedName("r") val operaCompanyID: String? = EMPTY_STRING_DOMAIN //available only for business users
) {
    companion object {
        @JvmStatic
        fun emptyCustomer(countryCode: String): Customer {
            return Customer(
                    EMPTY_STRING_DOMAIN, FullName.EMPTY, Contact.EMPTY,
                    Address(
                            EMPTY_STRING_DOMAIN, countryCode = countryCode, postCode = EMPTY_STRING_DOMAIN,
                            companyName = EMPTY_STRING_DOMAIN
                    ), bookingPreferences = BookingPreferences.EMPTY

            )
        }
    }

    fun hasPaymentCardDetails(): Boolean = paymentCard != null && paymentCard != PaymentCard.EMPTY
}

data class PaymentCard(
    @SerializedName("a") val number: String,
    @SerializedName("b") val cardType: String,
    @SerializedName("c") val holdersFullName: String,
    @SerializedName("d") val expiryDate: String = EMPTY_STRING_DOMAIN,
    @SerializedName("e") val cardID: String? = null
    ) {
    companion object {
        val EMPTY = PaymentCard(
                number = EMPTY_STRING_DOMAIN,
                cardType = EMPTY_STRING_DOMAIN,
                cardID = null,
                holdersFullName = EMPTY_STRING_DOMAIN,
                expiryDate = EMPTY_STRING_DOMAIN
        )
    }

    fun isBusinessCard(): Boolean = cardType == BUSINESS_CARD
}

data class BookingPreferences(
    @SerializedName("a") val mealPreference: Int?,
    @SerializedName("b") val roomCriteriaPreference: RoomCriteria
) {
    companion object {
        val EMPTY = BookingPreferences(null, RoomCriteria.createWithDefaults(roomNumber = 1))
    }
}

data class Contact(
    @SerializedName("a") val email: String,
    @SerializedName("b") val mobile: String? = null,
    @SerializedName("c") val telephone: String? = null
) {
    companion object {
        val EMPTY = Contact(EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
    }
}

data class FullName(
    @SerializedName("a") val title: String,
    @SerializedName("b") val firstName: String,
    @SerializedName("c") val lastName: String
) {

    companion object {
        val EMPTY = FullName(EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
    }
}

data class Business(
        val accessLevel: AccessLevel,
        val customerReferenceAnswer: String? = null,
        val centralCard: String? = null,
        val employeeId: String
)

enum class AccessLevel { STAYER, SELF, BOOKER, SUPER, UNKNOWN }