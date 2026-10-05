package com.whitbread.premierinn.common.pushNotification.creator

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.pushNotification.PushNotificationData.Unsupported
import com.whitbread.premierinn.domain.resource.usecase.ForceUpdateRequired
import com.whitbread.premierinn.landing.LandingActivityIntent
import java.util.Collections

class PushNotificationUnsupportedCreator(firebaseLogger: FirebaseLogger,
                                         analytics: TrackingAnalytics,
                                         forceUpdateRequired: ForceUpdateRequired
) : BasePushNotificationCreator(Collections.emptyMap(), firebaseLogger, analytics, forceUpdateRequired) {

    override val pushNotificationData = Unsupported

    override fun getPendingIntent(context: Context) : PendingIntent? = TaskStackBuilder.create(context).run {
        addNextIntent(LandingActivityIntent.create(context))
        getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun getIntent(context: Context): Intent = LandingActivityIntent.create(context)
    override fun isDataMalformed() = false
}
