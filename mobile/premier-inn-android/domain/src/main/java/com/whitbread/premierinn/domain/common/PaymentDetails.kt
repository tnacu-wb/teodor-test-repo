package com.whitbread.premierinn.domain.common

data class PaymentDetails(val billingAddress: Address? = null,
                          val cardType: String? = EMPTY_STRING_DOMAIN,
                          val cardNumber: String? = EMPTY_STRING_DOMAIN,
                          val cardSecurityCode: String? = EMPTY_STRING_DOMAIN,
                          val holdersFullName: String? = EMPTY_STRING_DOMAIN,
                          val expiryDate: String? = EMPTY_STRING_DOMAIN,
                          val issueNumber: String? = EMPTY_STRING_DOMAIN,
                          val startDate: String? = EMPTY_STRING_DOMAIN,
                          val prepaymentRequired: Boolean = false,
                          val useExistingCard: Boolean = false)