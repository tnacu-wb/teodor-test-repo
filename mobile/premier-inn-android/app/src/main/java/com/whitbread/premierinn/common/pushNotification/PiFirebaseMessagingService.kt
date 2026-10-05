package com.whitbread.premierinn.common.pushNotification

import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.whitbread.premierinn.BuildConfig
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.resource.usecase.ForceUpdateRequired
import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

const val PUSH_NOTIFICATIONS_FEATURE_TAG = "PushNotifications"

/**
 * Strategy: Force Propagate Remote Config updates in real time - aka ForceUpdate as a UseCase
 * https://firebase.google.com/docs/remote-config/propagate-updates-realtime
 */
@AndroidEntryPoint
class PiFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var persistenceManager: SimplePersistenceManager

    @Inject
    lateinit var pushNotificationFactory: PushNotificationFactory

    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate() {
        super.onCreate()
        compositeDisposable = CompositeDisposable()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let { notification ->
            message.data.let { data ->
                if (data.containsKey(KEY_CONFIG_STATE) && "STALE" == data[KEY_CONFIG_STATE]) {
                    persistenceManager
                        .setRemoteConfigAsStale(true)
                }
                processData(notification, data)
            }
        }
    }

    override fun onNewToken(p0: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.FCM_TOPIC_REMOTE_CONFIG_PURGE)
    }

    private fun processData(notification: RemoteMessage.Notification, data: Map<String, String>) {
        showNotification(notification.title, notification.body,
            pushNotificationFactory
                .createPendingIntent(this, data)
        )
    }

    private fun showNotification(
        notificationTitle: String?,
        notificationBody: String?,
        pendingIntent: PendingIntent?
    ) {
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
                .setContentTitle(notificationTitle)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notificationBody))
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(this, R.color.premier_inn_purple))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)

        NotificationManagerCompat.from(this)
            .notify((System.currentTimeMillis() / 1000).toInt(), notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {
        private const val KEY_CONFIG_STATE = "CONFIG_STATE"
        const val NOTIFICATIONS_CHANNEL_ID = "notifications.channel.id"
    }
}
