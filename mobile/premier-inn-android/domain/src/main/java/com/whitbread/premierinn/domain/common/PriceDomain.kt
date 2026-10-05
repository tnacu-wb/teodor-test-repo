package com.whitbread.premierinn.domain.common

const val GBP_LABEL = "GBP"
data class PriceDomain(val amount: Float, val currency: String) {
    companion object {
        var maxDonation = 3f
        var minDonation = 0.3f
        fun createDefault(): PriceDomain {
            return PriceDomain(0f, GBP_LABEL)
        }
        fun createWithGBPCurrency(amount: Float): PriceDomain {
            return PriceDomain(amount, GBP_LABEL)
        }
    }
}