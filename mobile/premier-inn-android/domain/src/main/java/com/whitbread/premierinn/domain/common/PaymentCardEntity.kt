package com.whitbread.premierinn.domain.common

data class PaymentCardEntity(
        val billingAddress: Address,
        val businessAccount: BusinessAccount?,
        val cardNumber: String? = null,
        val cardSecurityCode: String? = null,
        val cardType: String? = null,
        val cardholderName: String? = null,
        val expiryDate: String? = null,
        val issueNumber: String? = null,
        val paymentAuthenticationResponse: String? = null,
        val prepaymentRequired: Boolean,
        val startDate: String? = null,
        val useExistingCard: Boolean
    )

        data class BusinessAccount(
            val alcoholAllowed: Boolean,
            val atosPassword: String?,
            val atosUsername: String? = null,
            val breakfastCode: Int?,
            val carParkingAllowed: Boolean,
            val cardNotPresentAuth: Boolean,
            val customerReference: String?,
            val dinnerAllowance: PriceDomain?,
            val otherChargesAllowed: Boolean? = false,
            val purchaseOrder: String?,
            val wifiAccessAllowed: Boolean
        )
