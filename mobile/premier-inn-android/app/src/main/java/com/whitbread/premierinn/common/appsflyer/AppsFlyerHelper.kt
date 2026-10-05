package com.whitbread.premierinn.common.appsflyer

import android.content.Context
import com.appsflyer.AFInAppEventParameterName.*
import com.appsflyer.AppsFlyerLib
import com.whitbread.premierinn.BuildConfig
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerConversionListener

object AppsFlyerHelper {

    private var appsFlyer: AppsFlyerLib = AppsFlyerLib.getInstance()
    private var isInitialized: Boolean = false

    fun initAppsFlyer(context: Context, conversionListener: AppsFlyerConversionListener) {
        appsFlyer.init(BuildConfig.APPSFLYER_KEY, conversionListener, context)
        if (BuildConfig.STAGING) {
            appsFlyer.setDebugLog(true)
        }
        appsFlyer.start(context)
        isInitialized = true
    }

    fun getAppsFlyerLibInstance(): AppsFlyerLib = appsFlyer

    fun isInitialized(): Boolean = isInitialized

    fun logBookingConfirmationEvent(
        context: Context,
        params: AppsFlyerBookingConfirmationParams
    ) {
        params.let {
            appsFlyer.logEvent(
                context,
                AFInAppEventType.PURCHASE,
                mapOf(
                    ORDER_ID to it.bookingReference,
                    REVENUE to it.revenue,
                    CONTENT_ID to it.hotel,
                    CURRENCY to it.currency,
                    CONTENT_TYPE to it.hotelCode
                )
            )
        }
    }
}