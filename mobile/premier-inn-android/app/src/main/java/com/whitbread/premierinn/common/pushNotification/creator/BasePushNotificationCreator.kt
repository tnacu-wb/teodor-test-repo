package com.whitbread.premierinn.common.pushNotification.creator

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.adobe.marketing.mobile.CampaignClassic
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action.CIOL_ADOBE_PUSH_ACTION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action.LEAVE_EASY_ADOBE_PUSH_ACTION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ADOBE_CAMPAIGN_KEY
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.FirebaseParams
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.analytics.analyticsDataOf
import com.whitbread.premierinn.common.pushNotification.PUSH_NOTIFICATIONS_FEATURE_TAG
import com.whitbread.premierinn.common.pushNotification.PushNotificationData
import com.whitbread.premierinn.common.pushNotification.PushNotificationFactory.Companion.NOTIFICATION_TYPE_KEY
import com.whitbread.premierinn.common.pushNotification.PushNotificationFactory.Companion.TRACKING_CODE
import com.whitbread.premierinn.common.pushNotification.analytics.FcmNotificationData
import com.whitbread.premierinn.common.pushNotification.utils.PushType
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.resource.usecase.ForceUpdateRequired
import java.util.concurrent.TimeUnit

private const val FORCE_UPDATE_TIMEOUT_IN_MS = 3000L

abstract class BasePushNotificationCreator(
    private val rawData: Map<String, String>,
    private val firebaseLogger: FirebaseLogger,
    private val trackingAnalytics: TrackingAnalytics,
   private val forceUpdateRequired: ForceUpdateRequired){

    init {
        logAnalytics()
    }

    fun intent(context: Context): Intent = canNavigate().let { if (it) getIntent(context) else Intent() }

    fun pendingIntent(context: Context): PendingIntent? = canNavigate().let {
        if (it) getPendingIntent(context)
        else PendingIntent.getActivity(
            context,
            0,
            Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun canNavigate(): Boolean {
        runCatching {
            return forceUpdateRequired.execute().map { !it }
                .timeout(FORCE_UPDATE_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
                .onErrorReturn { true }
                .blockingGet()
        }.getOrElse { error ->
            Log.w(
                PUSH_NOTIFICATIONS_FEATURE_TAG,
                "Error while checking force update: ${error.message}"
            )
            return true
        }
    }

    private fun logAnalytics() {
        if (rawData.isNotEmpty()) {
            val params = FirebaseParams()

            rawData.forEach { (key, value) ->
                params.putString(key, value)
            }

            val type = rawData[NOTIFICATION_TYPE_KEY]
            val trackingCode = rawData[TRACKING_CODE] ?: EMPTY_STRING
            when(type) {
                PushType.START_CIOL.type ->
                    trackingAnalytics.track(CIOL_ADOBE_PUSH_ACTION,analyticsDataOf(CIOL_ADOBE_CAMPAIGN_KEY to trackingCode))
                PushType.LEAVE_EASY.type ->
                    trackingAnalytics.track(LEAVE_EASY_ADOBE_PUSH_ACTION,analyticsDataOf(CIOL_ADOBE_CAMPAIGN_KEY to trackingCode))
            }
            firebaseLogger.logEvent(PUSH_NOTIFICATION_OPENED, params)
            trackingAnalytics.trackAction(PUSH_NOTIFICATION_OPENED, FcmNotificationData(rawData))
            CampaignClassic.trackNotificationClick(rawData)
        }
    }

    protected abstract val pushNotificationData: PushNotificationData

    protected abstract fun getPendingIntent(context: Context): PendingIntent?
    protected abstract fun getIntent(context: Context): Intent
    protected abstract fun isDataMalformed(): Boolean

    companion object {
        const val NOTIFICATION_CAMPAIGN_ID_KEY = "CID"
        const val PUSH_NOTIFICATION_OPENED = "PUSH_APP_UK_BAU_DR_PROS_AND"
    }
}
