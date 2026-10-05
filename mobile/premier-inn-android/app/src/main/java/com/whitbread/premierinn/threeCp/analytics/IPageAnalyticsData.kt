package com.whitbread.premierinn.threeCp.analytics

import com.whitbread.premierinn.common.PaymentTimingChoice
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PUSH_TOKEN
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.reviewbooking.analytics.ThreeCpPaymentAnalyticData
import java.util.Locale
import java.util.TimeZone

class IPageAnalyticsData(
    private val product: String = EMPTY_STRING,
    private val userID: String? = null,
    private val environment: String = EMPTY_STRING,
    private val userLoginStatus: Boolean = false,
    private val screenType: String = EMPTY_STRING,
    private val isBusinessUser: Boolean = false,
    private val sessionID: String = EMPTY_STRING,
    private val templateID: String = EMPTY_STRING,
    private val paymentCardSelected: String = EMPTY_STRING,
    private val paymentTimeChoice: String = EMPTY_STRING,
    private val loadTime: String = EMPTY_STRING,
    private val pushToken : String = EMPTY_STRING,
    private val hasPollingStarted: Boolean = false
) : AnalyticsData {

    companion object {
        const val KEY_IPAGE_TEMPLATE_ID = "analyticsData.bf.paymentTemplateID"
        const val KEY_IPAGE_SESSION_ID = "analyticsData.bf.paymentSessionID"
        const val KEY_PAYMENT_CARD_SELECTED = "analyticsData.bf.paymentCardSelected"
        const val KEY_PAYMENT_TIMING = "analyticsData.bf.paymentTakenNow"
        const val KEY_IPAGE_PAYMENT_LOAD_TIME = "analyticsData.bf.paymentLoadTime"
        const val KEY_IPAGE_PAYMENT_DETAILS_SUBMITTED = "analyticsData.conf.paymentDetailsSubmitted"
    }

    override fun contextData(): MutableMap<String, String> {
        val contextData = HashMap<String, String>()
        val paymentTakenNow = paymentTimeChoice == PaymentTimingChoice.PAY_NOW.name

        contextData[ThreeCpPaymentAnalyticData.KEY_PAYMENT_PRODUCT] = ";$product"
        userID?.let { guestHistoryNumber ->
            contextData[ThreeCpPaymentAnalyticData.KEY_PAYMENT_USER_ID] = guestHistoryNumber
        }
        contextData[ThreeCpPaymentAnalyticData.KEY_PAYMENT_ENVIRONMENT] = if (environment == "stage") AnalyticsConstants.Value.VARIANT_DEBUG else AnalyticsConstants.Value.VARIANT_PRODUCTION
        contextData[ThreeCpPaymentAnalyticData.KEY_PAYMENT_USER_LOGIN_STATUS] = if (userLoginStatus) AnalyticsConstants.Value.LOGGED_IN else AnalyticsConstants.Value.LOGGED_OUT
        contextData[ThreeCpPaymentAnalyticData.KEY_PAYMENT_TIME_ZONE] = TimeZone.getDefault().id
        contextData[ThreeCpPaymentAnalyticData.KEY_PAYMENT_LANGUAGE] = Locale.getDefault().language
        contextData[ThreeCpPaymentAnalyticData.KEY_PAYMENT_SCREEN_TYPE] = screenType
        contextData[ThreeCpPaymentAnalyticData.KEY_PAYMENT_USER_TYPE] = if (isBusinessUser) AnalyticsConstants.Value.USER_BUSINESS_TYPE else AnalyticsConstants.Value.USER_LEISURE_TYPE
        contextData[KEY_IPAGE_TEMPLATE_ID] = templateID
        contextData[KEY_IPAGE_SESSION_ID] = sessionID
        contextData[KEY_IPAGE_PAYMENT_LOAD_TIME] = loadTime
        contextData[KEY_PAYMENT_CARD_SELECTED] = paymentCardSelected
        contextData[KEY_PAYMENT_TIMING] = paymentTakenNow.toString()
        contextData[AnalyticsConstants.Key.EVENTS] = "scCheckout"
        contextData[PUSH_TOKEN] = pushToken
        if (hasPollingStarted) {
            contextData[KEY_IPAGE_PAYMENT_DETAILS_SUBMITTED] = hasPollingStarted.toString()
        }

        return contextData
    }

}