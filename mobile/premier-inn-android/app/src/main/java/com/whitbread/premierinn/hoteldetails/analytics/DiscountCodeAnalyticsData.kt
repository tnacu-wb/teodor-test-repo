package com.whitbread.premierinn.hoteldetails.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsData

data class DiscountCodeAnalyticsData(
    private val promoCode: String?,
    private val promoName: String?,
    private val discountCodeApplied: Boolean,
    private val errorMessage: String? = null
) : AnalyticsData {
    override fun contextData(): MutableMap<String, String> = mutableMapOf<String, String>().apply {
        promoCode?.takeIf { it.isNotEmpty() }
            ?.let { put(AnalyticsConstants.Key.SEARCH_PROMO_CODE, it) }
        promoName?.takeIf { it.isNotEmpty() }?.let { put(AnalyticsConstants.Key.SEARCH_PROMO_NAME, it) }
        put(AnalyticsConstants.Key.DISCOUNT_CODE_APPLIED, discountCodeApplied.toString())
        errorMessage?.takeIf { it.isNotEmpty() }
            ?.let { put(AnalyticsConstants.Key.ERROR_MESSAGE, it) }
    }
}

