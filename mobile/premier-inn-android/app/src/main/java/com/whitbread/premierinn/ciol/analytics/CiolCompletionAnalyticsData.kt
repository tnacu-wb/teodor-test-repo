package com.whitbread.premierinn.ciol.analytics

import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.BILLING_ADDRESS_KEY
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.BUTTON_CLICK
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.CIOL_CONTINUE_BUTTON_KEY
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.PAYMENT_COMPLETE_KEY
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.PRICE_BREAKDOWN_ACTION
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.PRICE_EXPAND_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.*
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.CIOL_FLOW
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.data.common.EMPTY_STRING

data class CiolCompletionAnalyticsData(
    val cardType: String,
    val isPrePaid: Boolean,
    val data: CiolAnalyticsModel,
    val completionData: CiolCompletionAnalyticsModel,
    val specialOccasion: String,
    val isPaymentComplete: Boolean?,
    val isContinueButton: Boolean? = null,
    val shouldDisplayDetailedAddress: Boolean? = null,
    val totalPrice: String = EMPTY_STRING,
    val shouldShowUpsellsRevenueChange: Boolean = false
) : AnalyticsData {
    override fun contextData(): MutableMap<String, String> {
        val dataMap = mutableMapOf(
            CIOL_FLOW_KEY to "true",
            CIOL_BOOKING_ID to data.bookingId,
            CIOL_CARD_TYPE_KEY to cardType,
            CIOL_PREPAY_KEY to isPrePaid.toString(),
            CIOL_REVENUE_KEY to completionData.revenue,
            CIOL_NIGHTS_CHANGE_KEY to "0",
            CIOL_ROOMS_CHANGE_KEY to "0",
            CIOL_ROOMS_TYPE_CHANGE_KEY to "false",
            CIOL_ROOM_REVENUE_CHANGE_KEY to "0",
            CIOL_TOTAL_REVENUE_CHANGE_KEY to completionData.totalRevenueChange,
            CIOL_ACTION_KEY to data.action,
            CIOL_CHECK_IN_DATE_KEY to data.checkInDate,
            CIOL_CHECK_IN_DAY_KEY to data.checkInDay,
            CIOL_CHECK_OUT_DATE_KEY to data.checkOutDate,
            CIOL_CHECK_OUT_DAY_KEY to data.checkOutDay,
            CIOL_CHECK_IN_OUT_DAY_KEY to data.checkInOutDay,
            CIOL_NIGHTS_KEY to data.noNights,
            CIOL_ROOMS_KEY to data.noRooms,
            CIOL_ADULTS_KEY to data.noAdults,
            CIOL_CHILDREN_KEY to data.noChildren,
            CIOL_EXTRAS_DESCRIPTION_KEY to completionData.extraDescription,
            CIOL_EXTRAS_CODE_KEY to completionData.extraCode,
            CIOL_RATE_CODE_KEY to data.rateCode,
            CIOL_RATE_DESCRIPTION_KEY to data.rateDescription,
            CIOL_RATE_NAME_KEY to data.rateName,
            CIOL_SPECIAL_OCCASION_KEY to specialOccasion,
            PRODUCTS to SEMICOLON + data.hotelId,
            SCREEN_TYPE to CIOL_FLOW
        )
        return dataMap.apply {
            if (isPaymentComplete != null) put(PAYMENT_COMPLETE_KEY, isPaymentComplete.toString())
            if (isContinueButton != null) put(CIOL_CONTINUE_BUTTON_KEY, BUTTON_CLICK)
            if (shouldDisplayDetailedAddress != null) put(BILLING_ADDRESS_KEY, shouldDisplayDetailedAddress.toString())
            if (totalPrice.isNotEmpty()) {
                put(PRICE_EXPAND_KEY, totalPrice)
                put(CIOL_ACTION_KEY, PRICE_BREAKDOWN_ACTION)
            }
            if (shouldShowUpsellsRevenueChange) {
                put(CIOL_WIFI_REVENUE_CHANGE_KEY, completionData.wifiRevenueChange)
                put(CIOL_LCO_REVENUE_CHANGE_KEY, completionData.lcoRevenueChange)
                put(CIOL_ECI_REVENUE_CHANGE_KEY, completionData.eciRevenueChange)
                put(CIOL_FOOD_REVENUE_CHANGE_KEY, completionData.foodRevenueChange)
            }
        }
    }
}
