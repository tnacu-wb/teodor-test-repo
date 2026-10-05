package com.whitbread.premierinn.businessbooker.domain.company

import com.whitbread.premierinn.domain.common.PriceDomain

data class Company(
        val requestedCompany: RequestCompany,
        val companyCellCodes: List<CellCode>,
        val allowCentralCreditCard: Boolean)

data class RequestCompany(val companyDetails: CompanyDetails,
                          val paymentDetails: PaymentDetails?,
                          val bookingAllowances: BookingAllowances,
                          val companyManagementDetails: CompanyManagementDetails?)

data class CompanyDetails(val companyName: String,
                          val alternateCompanyName: String)

data class PaymentDetails(val paymentCards: List<PaymentCard>,
                          val allowIndividualCards: Boolean)

data class PaymentCard(val cardId: String,
                       val cardLabel: String,
                       val cardType: String,
                       val nameOnCard: String,
                       val cardNumber: String,
                       val expiryDate: String,
                       val cardNotPresentRequired: Boolean)

data class BookingAllowances(val maxDinnerBudgets: PriceCapLocations?,
                             val upsellItemsAllowed: List<String>,
                             val allowAlcohol: Boolean,
                             val allowCarParking: Boolean,
                             val allowPremierSaverRates: Boolean,
                             val allowIndividualCards: Boolean)

data class PriceCapLocations(val uKWide: PriceDomain?,
                             val greaterLondon: PriceDomain?,
                             val ireland: PriceDomain?)

data class CompanyManagementDetails(val purchaseOrderManagement: ManagementInformationQuestion?,
                                    val customerReferenceManagement: ManagementInformationQuestion?,
                                    val userDefinedManagement: List<ManagementInformationQuestion?>?)

data class ManagementInformationQuestion(
    val questionId: String?,
    val label: String?,
    val mandatory: Boolean?,
    val managementHeader: String?,
    val location: String?,
    val active: Boolean = false,
    val managementInformationAnswer: ManagementInformationAnswer?,
    val questionType: QuestionType
)

data class ManagementInformationAnswer(val answerType: String?,
                                       val answers: List<String>?)

data class CellCode(val type: String,
                    val description: String)

enum class QuestionType{
    CUSTOMER_REF,
    PURCHASE_ORDER,
    USER_DEFINED
}