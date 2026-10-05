package com.whitbread.premierinn.domain.graphql.requestBodyModels

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN

data class InitiatePaymentRequestBody (
    val basketReference: String,
    val createPaymentCriteria: CreatePaymentCriteria
)

data class CreatePaymentCriteria (
    val booking: Booking,
    val payment: Payment,
    val requestId: String,
    val charityPackageCode: String,
    val hotelId: String,
    val isCiol: Boolean = false,
    val companyQuestionAndAnswerDetails: CompanyQuestionAndAnswerDetails? = null
)

data class Booking (
    val businessSite: BusinessSite,
    val channel: String,
    val journey: String,
    val language: String,
    val rooms: List<Rooms>,
    val type: String,
    val leadGuest: LeadGuestBooking,
    val arrivalDate: String,
    val departureDate: String, //TODO Not required - test if needed
)

data class BusinessSite (
    val identifier: String,
    val name: String,
    val type: String,
    val location: String
)

data class Rooms (
    val adultsNumber: Int,
    val rate: String,
    val type: String
)

// Booking rateType -> Rooms rate
/**
 * bookingConfirmation.reservationByIdList[0].roomStay.ratePlanCode -> Rooms rate
 * bookingConfirmation.reservationByIdList[0].roomStay.roomType
 */

data class Payment (
    val billing: Billing,
    val environment: String,
    val subType: String,
    val type: String,
    val businessItems: BusinessItems?,
    val pibaCardPresent: Boolean,
    val paypalNonce: String?,
    val paypalDeviceData: String?,
    val card: PaymentCardDetailsDomain?
)

data class BusinessItems(
    val purchaseOrderNumber: String = EMPTY_STRING_DOMAIN,
    val customReferenceNumber: String = EMPTY_STRING_DOMAIN,
    val businessAllowances: List<BusinessAllowances>
)

data class BusinessAllowances(
    val budget: Float,
    val allowance: String,
    val isAuthorised: Boolean
)

data class LeadGuestBooking (
    val name: String,
    val registered: Boolean,
    val previousBookings: Int?,
    val registeredSince: String?
)

data class Billing (
    val address: Address,
    val email: String,
    val firstName: String,
    val lastName: String,
    val title: String
)

data class Address (
        val country: String?,
        val addressLine1: String,
        val addressLine2: String? = EMPTY_STRING_DOMAIN,
        val addressLine3: String? = EMPTY_STRING_DOMAIN,
        val addressLine4: String? = EMPTY_STRING_DOMAIN,
        val postalCode: String
)

data class PaymentCardDetailsDomain(
    val cardType: String,
    val cardholderName: String,
    val cnpRequired: Boolean,
    val expiryMonth: String,
    val expiryYear: String,
    val logoUrl: String,
    val token: String,
    val type: String
)

data class CompanyQuestionAndAnswerDetails(
    val userDefinedQuestionAndAnswers: List<UserDefinedQuestionAndAnswer> = emptyList()
)

data class UserDefinedQuestionAndAnswer(
    val question: String,
    val answer: String
)