package com.whitbread.premierinn.common.pushNotification.creator

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.pushNotification.PushNotificationData.Banner
import com.whitbread.premierinn.domain.resource.usecase.ForceUpdateRequired
import com.whitbread.premierinn.landing.LandingActivityIntent
import com.whitbread.premierinn.notifications.NotificationsActivity
import com.whitbread.premierinn.notifications.NotificationsInfo
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

class PushNotificationBannerCreator(
    rawData: Map<String, String>,
    firebaseLogger: FirebaseLogger,
    analytics: TrackingAnalytics,
    forceUpdateRequired: ForceUpdateRequired) :
    BasePushNotificationCreator(rawData, firebaseLogger, analytics, forceUpdateRequired) {

    override val pushNotificationData = Banner(
        campaignId = rawData.getOrDefault(NOTIFICATION_CAMPAIGN_ID_KEY, ""),
        title = rawData.getOrDefault(BANNER_TITLE_KEY, ""),
        message = concatenateMessages(rawData),
        button = rawData.getOrDefault(BANNER_BUTTON_KEY, "")
    )

    override fun getPendingIntent(context: Context): PendingIntent? {
        return TaskStackBuilder.create(context).run {
            addNextIntent(LandingActivityIntent.create(context))
            if (!isDataMalformed()) {
                addNextIntent(getNotificationIntent(context))
            }
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    override fun getIntent(context: Context): Intent = if (isDataMalformed()) LandingActivityIntent.create(context) else getNotificationIntent(context)

    override fun isDataMalformed() = pushNotificationData.title.isEmpty()
                || pushNotificationData.message.isEmpty()
                || pushNotificationData.button.isEmpty()


    private fun getNotificationIntent(context: Context) =
        NotificationsActivity.createIntent(
            context,
            pushNotificationData.toNotificationsInfo(),
            true
        )

    private fun Banner.toNotificationsInfo() = NotificationsInfo(
        notificationTitle = title,
        notificationBody = message,
        notificationCTA = button
    )

    private fun concatenateMessages(rawData: Map<String, String>) = buildString {
        rawData
            .filterKeys { key -> BANNER_MESSAGE_KEY in key }
            .toSortedMap()
            .values.forEach { message ->
                append(message)
                append(MESSAGE_DELIMITER)
            }
    }

    companion object {
        private const val BANNER_TITLE_KEY = "title"
        private const val BANNER_MESSAGE_KEY = "message"
        private const val BANNER_BUTTON_KEY = "button"

        private const val MESSAGE_DELIMITER = "\n\n"
    }
}
