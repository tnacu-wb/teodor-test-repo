package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface PaymentMethodsGraphQLContract {

    data class PaymentMethodsData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("paymentMethods") val paymentMethods: List<PaymentMethod>?,
        @SerializedName("donations") val donations: Donations?,
        @SerializedName("bookingConfirmation") val bookingConfirmation: BookingConfirmationGraphQLContract.BookingConfirmation?
    )

    data class PaymentMethod(
        @SerializedName("clientToken") val clientToken: String?,
        @SerializedName("enabled") val enabled: Boolean,
        @SerializedName("name") val name: String,
        @SerializedName("order") val order: Int,
        @SerializedName("logoSrc") val logoSrc: String?,
        @SerializedName("paymentOptions") val paymentOptions: List<PaymentOption>,
        @SerializedName("reasons") val reasons: List<String>,
        @SerializedName("type") val type: String,
        @SerializedName("subType") val subType: String?,
        @SerializedName("acceptedCardTypes") val acceptedCardTypes: List<AcceptedCardType>?,
        @SerializedName("card") val card: Card?,
        @SerializedName("cnpOptionAvailable") val cnpOptionAvailable: Boolean,
        @SerializedName("cnpPreSelected") val cnpPreSelected: Boolean
    )

    data class PaymentOption(
        @SerializedName("enabled") val enabled: Boolean,
        @SerializedName("order") val order: Int,
        @SerializedName("type") val type: String
    )

    data class AcceptedCardType(
        @SerializedName("logoSrc") val logoSrc: String,
        @SerializedName("name") val name: String,
        @SerializedName("type") val type: String
    )

    data class Card(
        @SerializedName("token") val token: String,
        @SerializedName("expiryMonth") val expiryMonth: String,
        @SerializedName("expiryYear") val expiryYear: String,
        @SerializedName("type") val type: String,
        @SerializedName("logoSrc") val logoSrc: String,
        @SerializedName("cardHolderName") val cardHolderName: String?,
        @SerializedName("cardType") val cardType: String,
        @SerializedName("cnpRequired") val cnpRequired: Boolean
    )

    data class Donations(
            @SerializedName("description") val description: String,
            @SerializedName("imageSrc") val imageSrc: String,
            @SerializedName("informationBox") val informationBox: String,
            @SerializedName("name") val name: String,
            @SerializedName("donationPackages") val donationPackages: List<DonationPackage>
    )

    data class DonationPackage(
            @SerializedName("code") val code: String,
            @SerializedName("currency") val currency: String,
            @SerializedName("unitPrice") val unitPrice: Double
    )
}
