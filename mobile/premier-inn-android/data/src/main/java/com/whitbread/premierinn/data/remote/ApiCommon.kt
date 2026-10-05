package com.whitbread.premierinn.data.remote

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.AddressJsonAdapter
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import org.threeten.bp.LocalDate

const val SINGLE_KEY = "SB"
const val DOUBLE_KEY = "DB"
const val TWIN_KEY = "TWIN"
const val FAMILY_KEY = "FAM"
const val ACCESSIBLE_KEY = "DIS"

class ApiCommon {

    data class Price(@SerializedName("amount") val amount: Float,
                     @SerializedName("currency") val currency: String)

    data class UpsellItem(@SerializedName("postingDate") val postingDate: LocalDate?,
                          @SerializedName("quantity") val quantity: Int,
                          @SerializedName("code") val code: String,
                          @SerializedName("roomId") val roomId: String,
                          @SerializedName("legend") val legend: String?,
                          @SerializedName("category") val category: String?,
                          @SerializedName("unitCost") val unitCost: Price)

    @JsonAdapter(AddressJsonAdapter::class)
    data class Address(@SerializedName("line1") val line1: String?,
                       @SerializedName("line2") val line2: String?,
                       @SerializedName("line3") val line3: String?,
                       @SerializedName("line4") val line4: String?,
                       @SerializedName("line5") val line5: String?,
                       @SerializedName("type") val type: String?,
                       @SerializedName("countryCode") val countryCode: String?,
                       @SerializedName(value = "postCode", alternate = ["postcode"]) val postcode: String?,
                       @SerializedName("companyName") val companyName: String?)

    data class PaymentCard(@SerializedName("cardID") val cardID: String? = EMPTY_STRING_DOMAIN,
                           @SerializedName("cardType") val cardType: String?,
                           @SerializedName("cardNumber") val cardNumber: String?,
                           @SerializedName("expiryDate") val expiryDate: String?,
                           @SerializedName("cardHolderName") val cardHolderName: String?,
                           @SerializedName("cardToken") val cardToken: String? = EMPTY_STRING_DOMAIN,
                           @SerializedName("billingAddress") val billingAddress: Address?)

    data class Passport(@SerializedName("number") val number: String?,
                        @SerializedName("countryOfIssue") val countryOfIssue: String?)
}