package com.whitbread.premierinn.businessbooker.data.common

import com.whitbread.premierinn.businessbooker.data.remote.CompanyApiContract
import com.whitbread.premierinn.businessbooker.domain.company.BookingAllowances
import com.whitbread.premierinn.businessbooker.domain.company.CellCode
import com.whitbread.premierinn.businessbooker.domain.company.Company
import com.whitbread.premierinn.businessbooker.domain.company.CompanyDetails
import com.whitbread.premierinn.businessbooker.domain.company.CompanyManagementDetails
import com.whitbread.premierinn.businessbooker.domain.company.ManagementInformationAnswer
import com.whitbread.premierinn.businessbooker.domain.company.ManagementInformationQuestion
import com.whitbread.premierinn.businessbooker.domain.company.PaymentCard
import com.whitbread.premierinn.businessbooker.domain.company.PaymentDetails
import com.whitbread.premierinn.businessbooker.domain.company.PriceCapLocations
import com.whitbread.premierinn.businessbooker.domain.company.QuestionType
import com.whitbread.premierinn.businessbooker.domain.company.RequestCompany
import com.whitbread.premierinn.data.remote.ApiCommon
import com.whitbread.premierinn.domain.common.PriceDomain

fun CompanyApiContract.CompanyDetailsResponse.toDomain(): Company {
    return Company(
            requestedCompany = requestedCompany.toDomain(),
            companyCellCodes = companyCellCodes.toCellCodesDomain(),
            allowCentralCreditCard = allowCentralCreditCard
    )
}

fun CompanyApiContract.Company.toDomain(): RequestCompany {
    return RequestCompany(
            companyDetails = companyDetails.toDomain(),
            paymentDetails = paymentDetails.toPaymentsDetailsDomain(),
            bookingAllowances = bookingAllowances.toBookingAllowancesDomain(),
            companyManagementDetails = companyManagementDetails?.toCompanyManagementDetailsDomain()
    )
}

fun CompanyApiContract.CompanyDetails.toDomain(): CompanyDetails {
    return CompanyDetails(companyName = companyName, alternateCompanyName = alternateCompanyName)
}

fun CompanyApiContract.PaymentDetails.toPaymentsDetailsDomain(): PaymentDetails {
    return PaymentDetails(paymentCards = paymentCards?.toPaymentCardsDomain() ?: emptyList(),
                          allowIndividualCards = allowIndividualCards)
}

fun List<CompanyApiContract.PaymentCards>.toPaymentCardsDomain(): List<PaymentCard> {
    return this.map { it.toPaymentCardDomain() }
}

fun CompanyApiContract.PaymentCards.toPaymentCardDomain(): PaymentCard {
    return PaymentCard(cardId = cardId, cardLabel = cardLabel, cardType = cardType, nameOnCard = nameOnCard,
        cardNumber = cardNumber, expiryDate = expiryDate, cardNotPresentRequired = cardNotPresentRequired)
}

fun CompanyApiContract.CompanyManagementDetails.toCompanyManagementDetailsDomain(): CompanyManagementDetails? {
    return CompanyManagementDetails(
        purchaseOrderManagement = purchaseOrderManagement?.toManagementInformationQuestionDomain(
            QuestionType.PURCHASE_ORDER
        ),
        customerReferenceManagement = customerReferenceManagement?.toManagementInformationQuestionDomain(
            QuestionType.CUSTOMER_REF
        ),
        userDefinedManagement = userDefinedManagement?.toManagementInformationQuestionsDomain()
    )
}

fun List<CompanyApiContract.ManagementInformationQuestion?>.toManagementInformationQuestionsDomain(): List<ManagementInformationQuestion?> {
    return this.map { it?.toManagementInformationQuestionDomain(QuestionType.USER_DEFINED) }
}

fun CompanyApiContract.ManagementInformationQuestion.toManagementInformationQuestionDomain(
    questionType: QuestionType
): ManagementInformationQuestion {
    return ManagementInformationQuestion(
        questionId = questionId,
        label = label,
        mandatory = mandatory,
        managementHeader = managementHeader,
        location = location,
        active = active ?: false,
        managementInformationAnswer = managementInformationAnswer?.toManagementInformationAnswerDomain(),
        questionType = questionType
    )
}

fun CompanyApiContract.ManagementInformationAnswer.toManagementInformationAnswerDomain(): ManagementInformationAnswer {
    return ManagementInformationAnswer(answerType = answerType, answers = answers ?: emptyList())
}

fun CompanyApiContract.BookingAllowances.toBookingAllowancesDomain(): BookingAllowances {
    return BookingAllowances(maxDinnerBudgets = maxDinnerBudgets?.toPriceCapLocationsDomain(),
            upsellItemsAllowed = upsellItemsAllowed.map { it },
            allowAlcohol = allowAlcohol,
            allowCarParking = allowCarParking,
            allowPremierSaverRates = allowPremierSaverRates,
            allowIndividualCards = allowIndividualCards)
}

fun CompanyApiContract.PriceCapLocations.toPriceCapLocationsDomain(): PriceCapLocations {
    return PriceCapLocations(uKWide = uKWide?.toDomain(),
            greaterLondon = greaterLondon?.toDomain(),
            ireland = ireland?.toDomain()
    )
}

fun List<CompanyApiContract.CellCode>.toCellCodesDomain(): List<CellCode> {
    return this.map { it.toCellCodeDomain() }
}

fun CompanyApiContract.CellCode.toCellCodeDomain(): CellCode {
    return CellCode(type = type, description = description)
}

fun ApiCommon.Price.toDomain(): PriceDomain {
    return PriceDomain(
        amount = amount,
        currency = currency
    )
}