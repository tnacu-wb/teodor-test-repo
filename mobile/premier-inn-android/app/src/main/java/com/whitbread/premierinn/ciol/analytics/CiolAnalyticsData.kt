package com.whitbread.premierinn.ciol.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ACTION_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ADOBE_CAMPAIGN_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ADULTS_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_BOOKING_ID
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_CHECK_IN_DATE_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_CHECK_IN_DAY_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_CHECK_IN_OUT_DAY_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_CHECK_OUT_DATE_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_CHECK_OUT_DAY_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_CHILDREN_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_EXTRAS_DESCRIPTION_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_FLOW_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_NIGHTS_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_RATE_CODE_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_RATE_DESCRIPTION_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_RATE_NAME_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ROOMS_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ERROR_MESSAGE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.GOOGLE_ID
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.MICROSOFT_ID
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PRODUCTS
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PUSH_TOKEN
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.SCREEN_TYPE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.CIOL
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.CIOL_FLOW
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.analytics.CampaignDataModel
import com.whitbread.premierinn.data.common.EMPTY_STRING

const val SEMICOLON = ";"

data class CiolAnalyticsData(
    val data: CiolAnalyticsModel,
    val screenType: String = CIOL_FLOW,
    val isContinueButton: Boolean = false,
    val totalPrice: String = EMPTY_STRING,
    val campaignModel: CampaignDataModel = CampaignDataModel()
) : AnalyticsData {
    override fun contextData(): MutableMap<String, String> {
        return getDataMap().apply {
            if (data.errorMessage.isNotEmpty()) put(ERROR_MESSAGE, data.errorMessage)
            if (data.isDeepLinked) {
                put(CIOL_ADOBE_CAMPAIGN_KEY, campaignModel.campaignId)
                put(GOOGLE_ID, campaignModel.googleId)
                put(MICROSOFT_ID, campaignModel.microsoftId)
            }
            if (isContinueButton) put(CIOL_CONTINUE_BUTTON_KEY, BUTTON_CLICK)
            if (totalPrice.isNotEmpty()) put(PRICE_EXPAND_KEY, totalPrice)
        }
    }

    private fun getDataMap(): MutableMap<String, String> {
        return mutableMapOf(
            CIOL_FLOW_KEY to "true",
            CIOL_BOOKING_ID to data.bookingId,
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
            CIOL_EXTRAS_DESCRIPTION_KEY to data.extraDescription,
            PRODUCTS to SEMICOLON + data.hotelId,
            CIOL_RATE_CODE_KEY to data.rateCode,
            CIOL_RATE_DESCRIPTION_KEY to data.rateDescription,
            CIOL_RATE_NAME_KEY to data.rateName,
            SCREEN_TYPE to screenType,
            PUSH_TOKEN to data.pushToken
        )
    }

    companion object{
        const val REG_CARD_GUEST_DETAILS: String = CIOL + "Guest details"
        const val LEAD_GUEST_DETAILS: String = CIOL + "Lead guest details"
        const val ADDITIONAL_GUEST_DETAILS: String = CIOL + "Additional guest details"

        const val CIOL_REG_CARD_ACTION: String = "CIOL: Reg Cards"
        const val PRICE_BREAKDOWN_ACTION: String = "CIOL: Price Breakdown Payment"

        const val CIOL_CONTINUE_BUTTON_KEY: String = "analyticsData.ciol.btnContinue"
        const val LEAD_GUEST_ADD_BUTTON_KEY: String = "analyticsData.ciol.leadDetailsEdit"
        const val ADDITIONAL_GUEST_ADD_BUTTON_KEY: String = "analyticsData.ciol.additionalDetailsEdit"
        const val GUEST_FIRST_LAST_NAME_EDIT_KEY: String = "analyticsData.ciol.fieldEdit"
        const val GUEST_EDIT_SAVE_KEY: String = "analyticsData.ciol.btnSave"
        const val GUEST_EDIT_DOB_KEY: String = "analyticsData.ciol.dobEdit"
        const val GUEST_EDIT_NATIONALITY_KEY: String = "analyticsData.ciol.nationalityEdit"
        const val PAYMENT_COMPLETE_KEY: String = "analyticsData.ciol.paymentComplete"
        const val BILLING_ADDRESS_KEY: String = "analyticsData.ciol.billingAddress"
        const val PRICE_EXPAND_KEY: String = "analyticsData.ciol.btnExpand"

        const val ERROR_MESSAGE_VALUE: String = "Something went wrong"
        const val BUTTON_CLICK: String = "click"
    }
}
