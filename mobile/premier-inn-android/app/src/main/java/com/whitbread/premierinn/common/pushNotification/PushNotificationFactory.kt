package com.whitbread.premierinn.common.pushNotification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.pushNotification.creator.BasePushNotificationCreator
import com.whitbread.premierinn.common.pushNotification.creator.LeaveEasyNotificationCreator
import com.whitbread.premierinn.common.pushNotification.creator.PushNotificationBannerCreator
import com.whitbread.premierinn.common.pushNotification.creator.PushNotificationHotelDetailsCreator
import com.whitbread.premierinn.common.pushNotification.creator.PushNotificationSearchResultsCreator
import com.whitbread.premierinn.common.pushNotification.creator.PushNotificationUnsupportedCreator
import com.whitbread.premierinn.common.pushNotification.creator.StartCiolNotificationCreator
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.resource.usecase.ForceUpdateRequired
import com.whitbread.premierinn.domain.search.repository.SearchItemRepository

class PushNotificationFactory(
    private val firebaseLogger: FirebaseLogger,
    private val trackingAnalytics: TrackingAnalytics,
    private val forceUpdateRequired: ForceUpdateRequired,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val appConfiguration: AppConfiguration,
    private val searchItemRepository: SearchItemRepository) {

    private fun createCreator(data: Map<String, String>): BasePushNotificationCreator {
        val type = data.getOrDefault(NOTIFICATION_TYPE_KEY, NOTIFICATION_TYPE_UNSUPPORTED)

        return when (type) {
            NOTIFICATION_TYPE_BANNER -> PushNotificationBannerCreator(
                data,
                firebaseLogger,
                trackingAnalytics,
                forceUpdateRequired
            )
            NOTIFICATION_TYPE_SEARCH -> PushNotificationSearchResultsCreator(
                data,
                firebaseLogger,
                trackingAnalytics,
                forceUpdateRequired,
                appConfiguration,
                deviceLocaleProvider,
                searchItemRepository
            )
            NOTIFICATION_TYPE_HOTEL_DETAILS -> PushNotificationHotelDetailsCreator(
                data,
                firebaseLogger,
                trackingAnalytics,
                forceUpdateRequired
            )
            NOTIFICATION_TYPE_START_CIOL -> StartCiolNotificationCreator(
                data,
                firebaseLogger,
                trackingAnalytics,
                forceUpdateRequired
            )
            NOTIFICATION_TYPE_LEAVE_EASY -> LeaveEasyNotificationCreator(
                data,
                firebaseLogger,
                trackingAnalytics,
                forceUpdateRequired
            )
            else -> PushNotificationUnsupportedCreator(
                firebaseLogger,
                trackingAnalytics,
                forceUpdateRequired
            )
        }
    }

    fun createPendingIntent(context: Context, data: Map<String, String>): PendingIntent? =
        createCreator(data).pendingIntent(context)

    fun createIntent(context: Context, data: Map<String, String>): Intent =
        createCreator(data).intent(context)

    companion object {
        const val NOTIFICATION_TYPE_KEY = "type"
        const val TRACKING_CODE = "trackingCode"
        private const val NOTIFICATION_TYPE_BANNER = "banner"
        private const val NOTIFICATION_TYPE_HOTEL_DETAILS = "hotel_details"
        private const val NOTIFICATION_TYPE_SEARCH = "location_search"
        private const val NOTIFICATION_TYPE_UNSUPPORTED = "unsupported"
        private const val NOTIFICATION_TYPE_START_CIOL = "ciol"
        private const val NOTIFICATION_TYPE_LEAVE_EASY= "leaveEasy"
    }
}
