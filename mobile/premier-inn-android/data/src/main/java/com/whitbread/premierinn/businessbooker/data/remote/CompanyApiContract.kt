package com.whitbread.premierinn.businessbooker.data.remote

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.ApiCommon

class CompanyApiContract {

    data class CompanyDetailsResponse(
            @SerializedName("requestedCompany") val requestedCompany: Company,
            @SerializedName("companyCellCodes") val companyCellCodes: List<CellCode>,
            @SerializedName("allowCentralCreditCard") val allowCentralCreditCard: Boolean)

    data class Company(@SerializedName("companyDetails") val companyDetails: CompanyDetails,
                       @SerializedName("paymentDetails") val paymentDetails: PaymentDetails,
                       @SerializedName("bookingAllowances") val bookingAllowances: BookingAllowances,
                       @SerializedName("companyManagementDetails") val companyManagementDetails: CompanyManagementDetails?)

    data class CompanyDetails(@SerializedName("companyName") val companyName: String,
                              @SerializedName("alternateCompanyName") val alternateCompanyName: String)

    data class PaymentDetails(@SerializedName("paymentCards") val paymentCards: List<PaymentCards>?,
                              @SerializedName("allowIndividualCards") val allowIndividualCards: Boolean)

    data class PaymentCards(@SerializedName("cardId") val cardId: String,
                            @SerializedName("cardLabel") val cardLabel: String,
                            @SerializedName("cardType") val cardType: String,
                            @SerializedName("nameOnCard") val nameOnCard: String,
                            @SerializedName("cardNumber") val cardNumber: String,
                            @SerializedName("expiryDate") val expiryDate: String,
                            @SerializedName("cardNotPresentRequired") val cardNotPresentRequired: Boolean)

    data class BookingAllowances(@SerializedName("maxDinnerBudgets") val maxDinnerBudgets: PriceCapLocations?,
                                 @SerializedName("upsellItemsAllowed") val upsellItemsAllowed: List<String>,
                                 @SerializedName("allowAlcohol") val allowAlcohol: Boolean,
                                 @SerializedName("allowCarParking") val allowCarParking: Boolean,
                                 @SerializedName("allowPremierSaverRates") val allowPremierSaverRates: Boolean,
                                 @SerializedName("allowIndividualCards") val allowIndividualCards: Boolean)

    data class PriceCapLocations(@SerializedName("uKWide") val uKWide: ApiCommon.Price?,
                                 @SerializedName("greaterLondon") val greaterLondon: ApiCommon.Price?,
                                 @SerializedName("ireland") val ireland: ApiCommon.Price?)

    data class CompanyManagementDetails(@SerializedName("purchaseOrderManagement") val purchaseOrderManagement: ManagementInformationQuestion?,
                                        @SerializedName("customerReferenceManagement") val customerReferenceManagement: ManagementInformationQuestion?,
                                        @SerializedName("userDefinedManagement") val userDefinedManagement: List<ManagementInformationQuestion?>?)

    data class ManagementInformationQuestion(@SerializedName("questionId") val questionId: String?,
                                             @SerializedName("label") val label: String?,
                                             @SerializedName("mandatory") val mandatory: Boolean?,
                                             @SerializedName("managementHeader") val managementHeader: String?,
                                             @SerializedName("location") val location: String?,
                                             @SerializedName("active") val active: Boolean? = false,
                                             @SerializedName("managementInformationAnswer") val managementInformationAnswer: ManagementInformationAnswer?)

    data class ManagementInformationAnswer(@SerializedName("answerType") val answerType: String?,
                                           @SerializedName("answers") val answers: List<String>?)

    data class CellCode(@SerializedName("type") val type: String,
                        @SerializedName("description") val description: String)
}