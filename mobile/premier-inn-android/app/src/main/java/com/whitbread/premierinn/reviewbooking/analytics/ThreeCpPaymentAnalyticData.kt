package com.whitbread.premierinn.reviewbooking.analytics

import com.whitbread.premierinn.common.PaymentTimingChoice
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.data.common.EMPTY_STRING
import java.util.Locale
import java.util.TimeZone


class ThreeCpPaymentAnalyticData @JvmOverloads constructor(
    private val product: String = EMPTY_STRING,
    private val userID: String? = null,
    private val environment: String = EMPTY_STRING,
    private val userLoginStatus: Boolean = false,
    private val screenType: String = EMPTY_STRING,
    private val isBusinessUser: Boolean = false,
    private val sessionID: String = EMPTY_STRING,
    private val templateID: String = EMPTY_STRING,
    private val paymentCards: Int = 0,
    private val paymentCardTypes: List<String> = emptyList(),
    private val paymentCardSelected: String = EMPTY_STRING,
    private val paymentTimeChoice: String = EMPTY_STRING,
    private val paymentErrorCode: String? = null,
    private val paymentErrorMsg: String? = null,
    private val hasPollingStarted: Boolean = false,
    private val paymentMethod: String = EMPTY_STRING,
    private val isPaymentOutage: Boolean = false
): AnalyticsData {

    companion object {
        const val KEY_PAYMENT_PRODUCT = "&&products"
        const val KEY_PAYMENT_USER_ID = "analyticsData.all.userID"
        const val KEY_PAYMENT_ENVIRONMENT = "analyticsData.all.environment"
        const val KEY_PAYMENT_USER_LOGIN_STATUS = "analyticsData.all.userLogin"
        const val KEY_PAYMENT_TIME_ZONE = "analyticsData.all.timeZone"
        const val KEY_PAYMENT_LANGUAGE = "analyticsData.all.language"
        const val KEY_PAYMENT_SCREEN_TYPE = "analyticsData.all.screenType"
        const val KEY_PAYMENT_USER_TYPE = "analyticsData.bf.userType"
        const val KEY_PAYMENT_TEMPLATE_ID = "analyticsData.bf.paymentTemplateID"
        const val KEY_PAYMENT_SESSION_ID = "analyticsData.bf.paymentSessionID"
        const val KEY_PAYMENT_CARDS = "analyticsData.bf.paymentCards"
        const val KEY_PAYMENT_CARD_TYPES = "analyticsData.bf.paymentCardTypes"
        const val KEY_PAYMENT_CARD_SELECTED = "analyticsData.bf.paymentCardSelected"
        const val KEY_PAYMENT_TIMING = "analyticsData.bf.paymentTakenNow"
        const val KEY_PAYMENT_ERROR_MSG = "analyticsData.error.message"
        const val KEY_PAYMENT_ERROR_CODE = "analyticsData.error.code"
        const val KEY_IPAGE_POLLING_BOOKING_SUCCESS = "analyticsData.conf.pollingBookingStatusSuccessful"
        const val KEY_PAYMENT_METHOD = "analyticsData.bf.paymentMethod" //for paypal and google pay
        const val KEY_PAYMENT_OUTAGE = "analyticsData.bf.paymentOutage" //for reserve without card fallback
    }

    override fun contextData(): MutableMap<String, String> {
        val contextData = HashMap<String, String>()
        val paymentTakenNow = paymentTimeChoice == PaymentTimingChoice.PAY_NOW.name

        contextData[KEY_PAYMENT_PRODUCT] = ";$product"
        userID?.let { customerAccountID ->
            contextData[KEY_PAYMENT_USER_ID] = customerAccountID
        }
        contextData[KEY_PAYMENT_ENVIRONMENT] = if (environment == "stage") AnalyticsConstants.Value.VARIANT_DEBUG else AnalyticsConstants.Value.VARIANT_PRODUCTION
        contextData[KEY_PAYMENT_USER_LOGIN_STATUS] = if (userLoginStatus) AnalyticsConstants.Value.LOGGED_IN else AnalyticsConstants.Value.LOGGED_OUT
        contextData[KEY_PAYMENT_TIME_ZONE] = TimeZone.getDefault().id
        contextData[KEY_PAYMENT_LANGUAGE] = Locale.getDefault().language
        contextData[KEY_PAYMENT_SCREEN_TYPE] = screenType
        contextData[KEY_PAYMENT_USER_TYPE] = if (isBusinessUser) AnalyticsConstants.Value.USER_BUSINESS_TYPE else AnalyticsConstants.Value.USER_LEISURE_TYPE
        contextData[KEY_PAYMENT_TEMPLATE_ID] = templateID
        contextData[KEY_PAYMENT_SESSION_ID] = sessionID
        contextData[KEY_PAYMENT_CARDS] = paymentCards.toString()
        contextData[KEY_PAYMENT_CARD_TYPES] = paymentCardTypes.joinToString(",")
        contextData[KEY_PAYMENT_CARD_SELECTED] = paymentCardSelected
        contextData[KEY_PAYMENT_TIMING] = paymentTakenNow.toString()
        contextData[KEY_PAYMENT_METHOD] = paymentMethod
        if (isPaymentOutage) {
            contextData[KEY_PAYMENT_OUTAGE] = isPaymentOutage.toString()
        }

        paymentErrorCode?.let { errorCode ->
            contextData[KEY_PAYMENT_ERROR_CODE] = errorCode
        }

        paymentErrorMsg?.let { errorMsg ->
            contextData[KEY_PAYMENT_ERROR_MSG] = errorMsg
        }

        if (hasPollingStarted) {
            contextData[KEY_IPAGE_POLLING_BOOKING_SUCCESS] = hasPollingStarted.toString()
        }
        return contextData
    }
}