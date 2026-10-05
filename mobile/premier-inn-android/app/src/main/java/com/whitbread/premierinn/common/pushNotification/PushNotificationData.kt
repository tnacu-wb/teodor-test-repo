package com.whitbread.premierinn.common.pushNotification

import com.whitbread.premierinn.data.common.EMPTY_STRING
import java.time.LocalDate

sealed class PushNotificationData(
    open val campaignId: String,
    open val trackingCode: String
) {
    data class Banner(
        override val campaignId: String,
        val title: String,
        val message: String,
        val button: String
    ) : PushNotificationData(campaignId, EMPTY_STRING)

    data class SearchResults(
        override val campaignId: String,
        override val trackingCode: String,
        val locationTitle: String,
        val latitude: String,
        val longitude: String,
        val arrivalDate: LocalDate,
        val nights: String,
    ) : PushNotificationData(campaignId, trackingCode)

    data class HotelDetails(
        override val campaignId: String,
        override val trackingCode: String,
        val code: String,
        val brand: String,
        val arrivalDate: LocalDate,
        val nights: String,
    ) : PushNotificationData(campaignId, trackingCode)

    data class StartCiol(
        override val trackingCode: String,
        val lastName: String,
        val reservationNumber: String,
        val arrivalDate: String,
        val type: String
    ) : PushNotificationData(EMPTY_STRING, trackingCode)

    data class LeaveEasy(
        override val trackingCode: String,
        val lastName: String,
        val reservationNumber: String,
        val arrivalDate: String,
        val type: String
    ) : PushNotificationData(EMPTY_STRING, trackingCode)

    data object Unsupported : PushNotificationData(EMPTY_STRING, EMPTY_STRING)
}
