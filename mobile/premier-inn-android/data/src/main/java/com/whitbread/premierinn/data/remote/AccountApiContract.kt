package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.common.EMAIL

interface AccountApiContract {
    data class CustomerChangePasswordBody(
        @SerializedName("password") val currentPassword: String, //TODO to be removed after auth0 POST MIGRATION
        @SerializedName("newPassword") val newPassword: String,
        @SerializedName("contactDetail") val contactDetails: CustomerContactDetails?,
        @SerializedName("bookingPreference") val bookingPreference: BookingPreference?,
        @SerializedName("companyId") val companyId: String? = null,
        )

    data class CustomerPersonalDetailsBody(
            @SerializedName("contactDetail") val contactDetails: CustomerContactDetails,
            @SerializedName("bookingPreference") val bookingPreference: BookingPreference?
    )

    data class CustomerPaymentPreferencesBody(
        @SerializedName("paymentPreference") val paymentPreference: PaymentPreference,
        @SerializedName("contactDetail") val contactDetails: CustomerContactDetails?,
        @SerializedName("bookingPreference") val bookingPreference: BookingPreference?
    )

    data class CustomerBookingPreferencesBody(
        @SerializedName("bookingPreference") val bookingPreference: BookingPreference,
        @SerializedName("contactDetail") val contactDetails: CustomerContactDetails?)

    data class CreateCustomerBody(
            @SerializedName("contactDetail") val contactDetails: CustomerContactDetails,
            @SerializedName("password") val password: String,
            @SerializedName("paymentPreference") val paymentPreference: PaymentPreference,
            @SerializedName("bookingPreference") val defaultBookingPreference: BookingPreference
    )

    class CustomerContactDetails(
            @SerializedName("title") val title: String?,
            @SerializedName("firstName") val firstName: String?,
            @SerializedName("lastName") val lastName: String?,
            @SerializedName("email") val email: String?,
            @SerializedName("address") val address: ApiCommon.Address?,
            @SerializedName("telephone") val telephone: String?,
            @SerializedName("mobile") val mobile: String?,
            @SerializedName("nationality") val nationality: String?,
            @SerializedName("carRegistration") val carRegistration: String?,
            @SerializedName("passport") val passport: ApiCommon.Passport?)

    data class CustomerResponse(@SerializedName("sessionId") val sessionId: String?,
                                @SerializedName("customerAccountId") val customerAccountID: String?,
                                @SerializedName("guestHistoryNumber") val guestHistoryNumber: String?,
                                @SerializedName("contactDetail") val contactDetail: CustomerContactDetails?,
                                @SerializedName("businessUse") val businessUse: Boolean?,
                                @SerializedName("bookingPreference") val bookingPreference: BookingPreference?,
                                @SerializedName("paymentPreference") val paymentPreference: PaymentPreference?,
                                @SerializedName("companyId") val companyId: String?,
                                @SerializedName("business") val business: Business?,
                                @SerializedName("guestHistoryCreation") val guestHistoryCreation: String?,
                                @SerializedName("totalStays") val totalStays: Int?)

    data class BookingPreference(
            @SerializedName("roomRequirements") val roomRequirements: RoomRequirements?,
            @SerializedName("foodPreference") val mealPreference: Int?
    )

    data class NewsletterPreferenceEditBody(
            @SerializedName("brandCodes") val brandCodes: String,
            @SerializedName("optIn") val optIn: Boolean,
            @SerializedName("contactChannelValue") val contactChannelValue: String,
            @SerializedName("language") val language: String,
            @SerializedName("country")  val country: String,
            @SerializedName("doubleOptIn") val doubleOptIn: Boolean,
            @SerializedName("contactChannelType") val contactChannelType: String = EMAIL.lowercase(),
            @SerializedName("customer") val customer: NewsLetterCustomerEditBody
    )

    data class NewsLetterCustomerEditBody(
        @SerializedName("countryOfResidence") val countryOfResidence: String,
        @SerializedName("language") val language: String
    )

    data class RoomRequirements(
            @SerializedName("type") val type: String,
            @SerializedName("adults") val adults: Int,
            @SerializedName("children") val children: Int,
            @SerializedName("cotRequired") val cotRequired: Boolean,
            @SerializedName("hotelBrand") val hotelBrand: String?
    )

    data class NewsletterPreferenceResponse(
        @SerializedName("contactChannelId") val contactChannelId: String,
        @SerializedName("permissions") val permissions: List<NewsletterPermission>)

    data class NewsletterPermission(@SerializedName("optIn") val optIn: Boolean)

    data class PaymentPreference(@SerializedName("paymentCard") val paymentCard: ApiCommon.PaymentCard?)

    data class Business(@SerializedName("accessLevel") val accessLevel: String,
                        @SerializedName("customerReferenceAnswer") val customerReferenceAnswer: String?,
                        @SerializedName("centralCard") val centralCard: String?,
                        @SerializedName("employeeId") val employeeId: String)
}