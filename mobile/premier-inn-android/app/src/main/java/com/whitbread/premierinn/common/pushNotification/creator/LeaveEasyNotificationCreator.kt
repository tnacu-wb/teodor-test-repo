package com.whitbread.premierinn.common.pushNotification.creator

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import com.whitbread.premierinn.bookingdetails.BookingDetailsActivity
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.pushNotification.PushNotificationData.LeaveEasy
import com.whitbread.premierinn.common.pushNotification.PushNotificationFactory.Companion.TRACKING_CODE
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.resource.usecase.ForceUpdateRequired
import com.whitbread.premierinn.landing.LandingActivityIntent

class LeaveEasyNotificationCreator(
    rawData: Map<String, String>,
    firebaseLogger: FirebaseLogger,
    analytics: TrackingAnalytics,
    forceUpdateRequired: ForceUpdateRequired) :
    BasePushNotificationCreator(rawData, firebaseLogger, analytics, forceUpdateRequired) {

    override val pushNotificationData = LeaveEasy(
        trackingCode = rawData.getOrDefault(TRACKING_CODE, EMPTY_STRING),
        lastName = rawData.getOrDefault(LAST_NAME, EMPTY_STRING),
        arrivalDate = rawData.getOrDefault(ARRIVAL_DATE, EMPTY_STRING),
        reservationNumber = rawData.getOrDefault(RESERVATION_NUMBER, EMPTY_STRING),
        type = rawData.getOrDefault(TYPE, EMPTY_STRING)
    )

    override fun getPendingIntent(context: Context): PendingIntent? =
        TaskStackBuilder.create(context).run {
            addNextIntent(LandingActivityIntent.create(context))
            if (!isDataMalformed()) {
                addNextIntent(getStartCiolIntent(context))
            }
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }


    override fun getIntent(context: Context): Intent =
        if (isDataMalformed()) LandingActivityIntent.create(context) else getStartCiolIntent(context)

    private fun getStartCiolIntent(context: Context) =
        BookingDetailsActivity.createIntent(
            context,
            pushNotificationData.reservationNumber,
            EMPTY_STRING,
            null,
            null,
            null,
            EMPTY_STRING,
            pushNotificationData.arrivalDate,
            pushNotificationData.lastName,
            true,
            pushNotificationData.type,
            pushNotificationData.trackingCode
        )

    override fun isDataMalformed() =
        pushNotificationData.lastName.isEmpty() ||
                pushNotificationData.reservationNumber.isEmpty() ||
                pushNotificationData.arrivalDate.isEmpty()

    companion object {
        const val LAST_NAME = "lastName"
        const val ARRIVAL_DATE = "arrivalDate"
        const val RESERVATION_NUMBER = "reservationNumber"
        const val TYPE = "type"
    }
}
