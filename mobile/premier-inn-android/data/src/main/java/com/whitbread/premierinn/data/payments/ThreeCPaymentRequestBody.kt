package com.whitbread.premierinn.data.payments

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.PAYMENT_CARD_TYPE
import com.whitbread.premierinn.data.common.PAYMENT_PIBA_TYPE
import com.whitbread.premierinn.domain.common.ANDROID_APPS_CHANNEL
import com.whitbread.premierinn.domain.payment.entity.ThreeCpPaymentDetailsEntity
import java.util.UUID

const val BOOKING_PAYMENT_JOURNEY = "BOOKING"
const val BUSINESS_SITE_TYPE = "HOTEL"
const val PAYMENT_CARD_SUB_TYPE = "ECOMM"


data class ThreeCPaymentRequestBody(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("payment") val payment: Payment,
    @SerializedName("booking") val booking: Booking,
    @SerializedName("sessionId") val sessionId: String
) {

    companion object {
        fun createPaymentRequest(
            threeCpPaymentDetailsEntity: ThreeCpPaymentDetailsEntity,
            language: String
        ): ThreeCPaymentRequestBody {
            return ThreeCPaymentRequestBody(
                requestId = UUID.randomUUID().toString(),
                payment = Payment.createPayment(threeCpPaymentDetailsEntity),
                booking = Booking.createBooking(threeCpPaymentDetailsEntity, language),
                sessionId = threeCpPaymentDetailsEntity.sessionId
            )
        }
    }
}


data class Booking(
    @SerializedName("channel") val channel: String,
    @SerializedName("journey") val journey: String,
    @SerializedName("type") val type: String,
    @SerializedName("businessSite") val businessSite: BusinessSite,
    @SerializedName("arrivalDate") val arrivalDate: String,
    @SerializedName("departureDate") val departureDate: String,
    @SerializedName("language") val language: String,
    @SerializedName("leadGuest") val leadGuest: LeadGuest,
    @SerializedName("rooms") val rooms: List<Room>
) {

    companion object {
        fun createBooking(
            threeCpPaymentDetailsEntity: ThreeCpPaymentDetailsEntity,
            language: String
        ): Booking {
            return Booking(
                channel = ANDROID_APPS_CHANNEL,
                journey = BOOKING_PAYMENT_JOURNEY,
                type = threeCpPaymentDetailsEntity.booking.type,
                businessSite = threeCpPaymentDetailsEntity.booking.toPaymentBusinessSite(),
                arrivalDate = threeCpPaymentDetailsEntity.booking.arrivalDate,
                departureDate = threeCpPaymentDetailsEntity.booking.departureDate,
                language = language,
                leadGuest = threeCpPaymentDetailsEntity.booker.toPaymentLeadGuest(),
                rooms = Room.buildRoomsList(threeCpPaymentDetailsEntity)
            )
        }
    }
}

data class BusinessSite(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("location") val location: String,
    @SerializedName("additionalServices") val additionalServices: List<String>
)

data class LeadGuest(
    @SerializedName("name") val name: String,
    @SerializedName("registered") val registered: Boolean,
    @SerializedName("registeredSince") val registeredSince: String,
    @SerializedName("previousBookings") val previousBookings: Int
)

data class Room(
    @SerializedName("type") val type: String,
    @SerializedName("rate") val rate: String,
    @SerializedName("adults") val adults: Long
) {
    companion object {
        fun buildRoomsList(threeCpPaymentDetailsEntity: ThreeCpPaymentDetailsEntity): List<Room> {
            val listOfRooms = mutableListOf<Room>()
            for (roomBooked in threeCpPaymentDetailsEntity.booking.roomBookings) {
                val room = Room(
                    type = roomBooked.type,
                    rate = roomBooked.rate,
                    adults = roomBooked.adults
                )
                listOfRooms.add(room)
            }

            return listOfRooms
        }
    }
}

data class Payment(
    @SerializedName("type") val type: String,
    @SerializedName("subType") val subType: String,
    @SerializedName("environment") val environment: String,
    @SerializedName("card") val card: Card?,
    @SerializedName("cardTypes") val cardTypes: List<String>,
    @SerializedName("amount") val amount: Amount,
    @SerializedName("billing") val billing: Billing
) {
    companion object {
        fun createPayment(threeCpPaymentDetailsEntity: ThreeCpPaymentDetailsEntity): Payment {
            return Payment(
                type = if (threeCpPaymentDetailsEntity.bookingPayment.isBusinessCard)
                    PAYMENT_PIBA_TYPE else PAYMENT_CARD_TYPE,
                subType = PAYMENT_CARD_SUB_TYPE,
                environment = EMPTY_STRING,
                card = threeCpPaymentDetailsEntity.bookingPayment.storedCard?.toPaymentCard(),
                cardTypes = threeCpPaymentDetailsEntity.bookingPayment.storedCard?.let
                { listOf(it.cardType) } ?: emptyList(),
                amount = threeCpPaymentDetailsEntity.booking.toPaymentRequestAmount(),
                billing = threeCpPaymentDetailsEntity.booker.toPaymentRequestBilling(threeCpPaymentDetailsEntity.booking)
            )
        }
    }
}

data class Amount(
    @SerializedName("currency") val currency: String,
    @SerializedName("minorUnits") val minorUnits: Int
)

data class Billing(
    @SerializedName("email") val email: String,
    @SerializedName("address") val address: BillingAddress,
    @SerializedName("title") val title: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("telephone") val telephone: String
)

data class BillingAddress(
        @SerializedName("line1") val line1: String,
        @SerializedName("line2") val line2: String,
        @SerializedName("line3") val line3: String = EMPTY_STRING,
        @SerializedName("line4") val line4: String = EMPTY_STRING,
        @SerializedName("postalCode") val postalCode: String?,
        @SerializedName("countryCode") val countryCode: String,
        @SerializedName("state") val state: String?
)

data class Card(
    @SerializedName("token") val token: String,
    @SerializedName("expiryMonth") val expiryMonth: String,
    @SerializedName("expiryYear") val expiryYear: String
)

