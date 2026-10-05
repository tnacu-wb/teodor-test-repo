package com.whitbread.premierinn.common.pushNotification.creator

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.pushNotification.PushNotificationData.SearchResults
import com.whitbread.premierinn.common.pushNotification.PushNotificationFactory.Companion.TRACKING_CODE
import com.whitbread.premierinn.common.pushNotification.utils.PushDateUtils
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.resource.usecase.ForceUpdateRequired
import com.whitbread.premierinn.domain.search.repository.SearchItemRepository
import com.whitbread.premierinn.landing.LandingActivityIntent
import com.whitbread.premierinn.searchresults.SearchResultsInput
import com.whitbread.premierinn.searchresults.createSearchResultIntent

private const val SEARCH_LOCATION_TITLE_KEY = "location_title"
private const val SEARCH_LOCATION_LATITUDE_KEY = "lat"
private const val SEARCH_LOCATION_LONGITUDE_KEY = "long"
private const val SEARCH_ARRIVAL_DATE_KEY = "arrival_date"
private const val SEARCH_ARRIVAL_NIGHTS_KEY = "nights"

private const val ARRIVAL_DATE_FORMAT = "dd/MM/yyyy"

private const val DEFAULT_COORDINATE = "0.0"
private const val DEFAULT_NIGHTS = "1"
private const val EMPTY_STRING = ""

class PushNotificationSearchResultsCreator(
    rawData: Map<String, String>,
    firebaseLogger: FirebaseLogger,
    analytics: TrackingAnalytics,
    forceUpdateRequired: ForceUpdateRequired,
    val appConfiguration: AppConfiguration,
    val deviceLocaleProvider: DeviceLocaleProvider,
    val searchItemRepository: SearchItemRepository
) :
    BasePushNotificationCreator(rawData, firebaseLogger, analytics, forceUpdateRequired) {

    override val pushNotificationData = SearchResults(
        campaignId = rawData.getOrDefault(NOTIFICATION_CAMPAIGN_ID_KEY, EMPTY_STRING),
        trackingCode = rawData.getOrDefault(TRACKING_CODE, EMPTY_STRING),
        locationTitle = rawData.getOrDefault(SEARCH_LOCATION_TITLE_KEY, EMPTY_STRING),
        latitude = rawData.getOrDefault(SEARCH_LOCATION_LATITUDE_KEY, DEFAULT_COORDINATE),
        longitude = rawData.getOrDefault(SEARCH_LOCATION_LONGITUDE_KEY, DEFAULT_COORDINATE),
        arrivalDate = PushDateUtils.formatDate(rawData.getOrDefault(SEARCH_ARRIVAL_DATE_KEY, EMPTY_STRING),
            ARRIVAL_DATE_FORMAT),
        nights = rawData.getOrDefault(SEARCH_ARRIVAL_NIGHTS_KEY, DEFAULT_NIGHTS)
    )

    override fun getPendingIntent(context: Context): PendingIntent? =
        TaskStackBuilder.create(context).run {
            addNextIntent(LandingActivityIntent.create(context))
            if (!isDataMalformed()) {
                addNextIntent(getSearchIntent(context))
            }
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

    override fun getIntent(context: Context) = if (isDataMalformed()) LandingActivityIntent.create(context) else getSearchIntent(context)

    override fun isDataMalformed() = pushNotificationData.run {
        locationTitle.isEmpty()
            || !isIntegerValue(nights)
            || !isLocationValid(latitude, longitude, locationTitle)
    }

    private fun isDoubleValue(value: String) = value.toDoubleOrNull() != null

    private fun isIntegerValue(value: String) = value.toIntOrNull() != null

    private fun isLocationValid(latitude: String, longitude: String, locationTitle: String) =
        (isDoubleValue(latitude) && isDoubleValue(longitude) && hasValidCoordinates())
            || getTopDestinations().firstOrNull { it.name == locationTitle } != null

    private fun getSearchIntent(context: Context): Intent = context.createSearchResultIntent(
        input = pushNotificationData.toSearchPayLoad(),
        country = deviceLocaleProvider.getDeviceLocale().country.lowercase(),
        language = deviceLocaleProvider.getDeviceLanguage()
    )

    private fun SearchResults.toSearchPayLoad(): SearchResultsInput {
        val resultsInputArrivalDate = org.threeten.bp.LocalDate.of(
            pushNotificationData.arrivalDate.year,
            pushNotificationData.arrivalDate.monthValue,
            pushNotificationData.arrivalDate.dayOfMonth
        )

        val builder = SearchResultsInput.builderWithDefaults()
            .placeName(locationTitle)
            .arrivalDate(resultsInputArrivalDate)
            .departureDate(resultsInputArrivalDate.plusDays(nights.toLong()))
            .trackingCode(pushNotificationData.trackingCode)

        if (hasValidCoordinates()) {
            builder
                .latitude(latitude.toFloat())
                .longitude(longitude.toFloat())
        } else {
            getTopDestinations().firstOrNull { it.name == locationTitle }?.let { topDestination ->
                val location = topDestination.location
                builder
                    .latitude(location?.latitude?.toFloat() ?: DEFAULT_COORDINATE.toFloat())
                    .longitude(location?.longitude?.toFloat() ?: DEFAULT_COORDINATE.toFloat())
            }
        }

        return builder.build()
    }

    private fun hasValidCoordinates() =
        pushNotificationData.latitude.toDouble() != DEFAULT_COORDINATE.toDouble() && pushNotificationData.longitude.toDouble() != DEFAULT_COORDINATE.toDouble()

    private fun getTopDestinations() = searchItemRepository.getTopDestinations().blockingGet()
}
