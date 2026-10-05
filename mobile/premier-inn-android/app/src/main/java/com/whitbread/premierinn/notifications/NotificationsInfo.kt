package com.whitbread.premierinn.notifications

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationsInfo(
    val notificationTitle: String,
    val notificationBody: String,
    val notificationCTA: String
) : Parcelable