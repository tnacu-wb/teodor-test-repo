package com.whitbread.premierinn.common.pushNotification.creator

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.pushNotification.PushNotificationData.HotelDetails
import com.whitbread.premierinn.common.pushNotification.PushNotificationFactory.Companion.TRACKING_CODE
import com.whitbread.premierinn.common.pushNotification.utils.PushDateUtils
import com.whitbread.premierinn.domain.resource.usecase.ForceUpdateRequired
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity
import com.whitbread.premierinn.hoteldetails.HotelDetailsInput
import com.whitbread.premierinn.landing.LandingActivityIntent
import com.whitbread.premierinn.searchresults.SearchResultsInput

class PushNotificationHotelDetailsCreator(
    rawData: Map<String, String>,
    firebaseLogger: FirebaseLogger,
    analytics: TrackingAnalytics,
    forceUpdateRequired: ForceUpdateRequired) :
    BasePushNotificationCreator(rawData, firebaseLogger, analytics, forceUpdateRequired) {

    override val pushNotificationData = HotelDetails(
        campaignId = rawData.getOrDefault(NOTIFICATION_CAMPAIGN_ID_KEY, EMPTY_STRING),
        trackingCode = rawData.getOrDefault(TRACKING_CODE, EMPTY_STRING),
        code = rawData.getOrDefault(HOTEL_CODE, EMPTY_STRING),
        brand = rawData.getOrDefault(HOTEL_BRAND, EMPTY_STRING),
        arrivalDate = PushDateUtils.formatDate(
            rawData.getOrDefault(ARRIVAL_DATE, EMPTY_STRING),
            ARRIVAL_DATE_FORMAT
        ),
        nights = rawData.getOrDefault(NIGHTS, DEFAULT_NIGHTS)
    )

    override fun getPendingIntent(context: Context): PendingIntent? =
        TaskStackBuilder.create(context).run {
            addNextIntent(LandingActivityIntent.create(context))
            if (!isDataMalformed()) {
                addNextIntent(getHotelDetailsIntent(context))
            }
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }


    override fun getIntent(context: Context) = if (isDataMalformed()) LandingActivityIntent.create(context) else getHotelDetailsIntent(context)

    override fun isDataMalformed() = !(pushNotificationData.brand.equals(BRAND_PI, ignoreCase = true)
                || pushNotificationData.brand.equals(BRAND_PID, ignoreCase = true)
                || pushNotificationData.brand.equals(BRAND_HUB, ignoreCase = true)
                || pushNotificationData.brand.equals(BRAND_ZIP, ignoreCase = true))
                || pushNotificationData.brand.isEmpty()
                || pushNotificationData.code.isEmpty()
                || !isIntegerValue(pushNotificationData.nights)

    private fun isIntegerValue(value:String) = value.toIntOrNull() != null

    private fun getHotelDetailsIntent(context: Context): Intent =
         HotelDetailsActivity.createIntent(
            context,
            HotelDetailsInput.fromNotifications(
                pushNotificationData.code,
                pushNotificationData.brand,
                pushNotificationData.trackingCode,
                getSearchResultsInput()

            ).build()
        )

    private fun getSearchResultsInput(): SearchResultsInput {

        val arrivalDate = org.threeten.bp.LocalDate.of(
            pushNotificationData.arrivalDate.year,
            pushNotificationData.arrivalDate.monthValue,
            pushNotificationData.arrivalDate.dayOfMonth
        )

        return SearchResultsInput.builderWithDefaults()
            .arrivalDate(arrivalDate)
            .departureDate(arrivalDate.plusDays(pushNotificationData.nights.toLong()))
            .placeName(EMPTY_STRING)
            .latitude(DEFAULT_LAT)
            .longitude(DEFAULT_LNG)
            .build()
    }

    companion object {
        const val ARRIVAL_DATE_FORMAT = "dd/MM/yyyy"
        const val EMPTY_STRING = ""
        const val DEFAULT_NIGHTS = "1"
        const val HOTEL_CODE = "hotelCode"
        const val HOTEL_BRAND = "hotelBrand"
        const val ARRIVAL_DATE = "arrivalDate"
        const val BRAND_PI = "PI"
        const val BRAND_PID = "PID"
        const val BRAND_HUB = "HUB"
        const val BRAND_ZIP = "ZIP"
        const val NIGHTS = "nights"
        const val DEFAULT_LAT = 0f
        const val DEFAULT_LNG = 0f
    }
}
