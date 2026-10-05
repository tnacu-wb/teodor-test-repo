package com.whitbread.premierinn.domain.graphql.requestBodyModels

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN

data class SaveCardRequestBody (
    val initiateSaveCardRequest: InitiateSaveCardRequest
)

data class InitiateSaveCardRequest(
    val requestId: String,
    val billingAddress: SaveCardBillingAddress,
    val cardDetails: SaveCardDetails,
    val environment: String,
    val country: String?,
    val language: String
)

data class SaveCardBillingAddress(
    val line1: String,
    val line2: String? = EMPTY_STRING_DOMAIN,
    val line3: String? = EMPTY_STRING_DOMAIN,
    val line4: String? = EMPTY_STRING_DOMAIN,
    val postCode: String,
    val countryCode: String,
    val companyName: String? = EMPTY_STRING_DOMAIN,
    val type: String
    )

data class SaveCardDetails(
    val cardType: String,
    val cnpRequired: Boolean,
    val memorableWord: String?= EMPTY_STRING_DOMAIN,
)
