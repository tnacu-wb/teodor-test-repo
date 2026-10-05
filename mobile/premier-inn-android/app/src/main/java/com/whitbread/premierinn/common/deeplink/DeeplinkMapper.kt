package com.whitbread.premierinn.common.deeplink

import android.os.Bundle
import androidx.core.net.toUri
import com.whitbread.premierinn.common.analytics.CampaignDataModel
import com.whitbread.premierinn.common.deeplink.DeeplinkStatus.FEATURE_DISABLED
import com.whitbread.premierinn.common.deeplink.DeeplinkStatus.KNOWN_DEEPLINK
import com.whitbread.premierinn.common.deeplink.DeeplinkStatus.UNKNOWN_DEEPLINK
import com.whitbread.premierinn.common.deeplink.DeeplinkStatus.WRONG_DEEPLINK
import com.whitbread.premierinn.common.deeplink.model.HotelDetailsDeeplinkModel
import com.whitbread.premierinn.common.deeplink.model.SearchResultDeeplinkModel
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.findbooking.FindBookingInput
import com.whitbread.premierinn.landing.model.LandingDeeplinkModel
import com.whitbread.premierinn.landing.model.PromoCodeInput
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject

//Paths
private const val HOME_ENDPOINT = "home.html"
private const val SRP_ENDPOINT = "search.html"
private const val HDP_ENDPOINT = "/hotels/"
private const val CIOL_ENDPOINT = "check-in-online.html"
private const val APPSFLYER_HOST = "premierinn.onelink.me"
private const val APPSFLYER_HOST_UAT = "premierinnuat.onelink.me"

// Bundle
const val CIOL_BUNDLE = "ciol_bundle"
const val SRP_BUNDLE = "srp_bundle"
const val HDP_BUNDLE = "hdp_bundle"
const val HOME_BUNDLE = "home_bundle"

// Destination
const val HOME_DESTINATION = "home_destination"
const val SRP_DESTINATION = "srp_destination"
const val HDP_DESTINATION = "hdp_destination"
const val CIOL_DESTINATION = "ciol_destination"
const val MY_ACCOUNT_EMPLOYEE_OFFER = "employee_offer"
const val APPSFLYER_CIOL_PAGE_NAME = "check-in-online"
const val APPSFLYER_HDP_PAGE_NAME = "hotel-details-page"
const val APPSFLYER_SRP_PAGE_NAME = "search-results-page"
const val APPSFLYER_HOME_PAGE_NAME = "home"

//Parameters
private const val SRP_SEARCH_TERM_PARAM = "searchModel.searchTerm"
private const val SRP_PLACEID_PARAM = "PLACEID"
private const val BOOKING_REFERENCE_PARAM = "resNo"
private const val ARRIVAL_DATE_PARAM = "arrivalDate"
private const val LAST_NAME_PARAM = "lastName"
private const val APPSFLYER_PAGE_NAME_PARAM = "pageName"
private const val APPSFLYER_SLUG_PARAM = "slug"
private const val APPSFLYER_CODE_TYPE = "promoType"
private const val APPSFLYER_PROMO_CODE = "promoCode"
private val adultList = listOf("ADULT1", "ADULT2", "ADULT3", "ADULT4")
private val childList = listOf("CHILD1", "CHILD2", "CHILD3", "CHILD4")
private val cotList = listOf("COT1", "COT2", "COT3", "COT4")
private val roomTypeList = listOf("INTTYP1", "INTTYP2", "INTTYP3", "INTTYP4")
private const val HOTEL_CODE_PARAM = "INNID"
private const val DAY_PARAM = "ARRdd"
private const val MONTH_PARAM = "ARRmm"
private const val YEAR_PARAM = "ARRyyyy"
private const val NIGHTS_PARAM = "NIGHTS"
private const val ROOMS_PARAM = "ROOMS"
private const val BRAND_PARAM = "BRAND"
private const val CAMPAIGN_ID = "CID" // when implementing other universal deeplink, don't forget to map google and microsoft ids
private const val GOOGLE_ID = "gclid"
private const val MICROSOFT_ID = "msclkid"
private const val ONE_VALUE = "1"
private const val ZERO_VALUE = "0"
private const val EMPTY_STRING = ""

private const val HOTEL_DETAILS_HOTELS_PATH_SEGMENT = "hotels"
private const val HDP_SLUG_SEPARATOR = "/"

class DeeplinkMapper @Inject constructor(
    private val isFeatureOn: IsFeatureOn,
    private val deviceLocaleProvider: DeviceLocaleProvider
) {

    fun mapUrl(urlString: String): DeeplinkContent {
        if (urlString.contains("employeeRates")) {
            return  DeeplinkContent(
                KNOWN_DEEPLINK,
                destination = MY_ACCOUNT_EMPLOYEE_OFFER,
                bundleData = Bundle()
            )
        }
        return try {
            val url = URL(urlString)
            when {
                url.host.contains(APPSFLYER_HOST) || url.host.contains(APPSFLYER_HOST_UAT) -> {
                    val appsFlyerPageName = getValueOfQueryParam(urlString, APPSFLYER_PAGE_NAME_PARAM)
                    when(appsFlyerPageName) {
                        APPSFLYER_CIOL_PAGE_NAME -> DeeplinkContent(
                            KNOWN_DEEPLINK,
                            destination = CIOL_DESTINATION,
                            bundleData = extractCiolBundle(urlString)
                        )
                        APPSFLYER_HDP_PAGE_NAME -> DeeplinkContent(
                            deeplinkStatus = KNOWN_DEEPLINK,
                            destination = HDP_DESTINATION,
                            bundleData = extractHdpBundle(urlString)
                        )
                        APPSFLYER_SRP_PAGE_NAME -> DeeplinkContent(
                            deeplinkStatus = KNOWN_DEEPLINK,
                            destination = SRP_DESTINATION,
                            bundleData = extractSrpBundle(urlString)
                        )
                        APPSFLYER_HOME_PAGE_NAME ->
                            DeeplinkContent(
                                deeplinkStatus = KNOWN_DEEPLINK,
                                destination = HOME_DESTINATION,
                                bundleData = extractHomeBundle(urlString)
                            )
                        else -> DeeplinkContent(UNKNOWN_DEEPLINK, destination = HOME_DESTINATION, Bundle())
                    }

                }
                url.path.endsWith(CIOL_ENDPOINT) -> {
                    if (isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DEEPLINK_CIOL)) {
                        DeeplinkContent(
                            KNOWN_DEEPLINK,
                            destination = CIOL_DESTINATION,
                            bundleData = extractCiolBundle(urlString)
                        )
                    } else {
                        DeeplinkContent(FEATURE_DISABLED, destination = HOME_DESTINATION, Bundle())
                    }
                }
                url.path.endsWith(HOME_ENDPOINT) -> DeeplinkContent(KNOWN_DEEPLINK, destination = HOME_DESTINATION, Bundle())
                url.path.endsWith(SRP_ENDPOINT) -> {
                    if (isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DEEPLINK_SRP)) {
                        DeeplinkContent(
                            deeplinkStatus = KNOWN_DEEPLINK,
                            destination = SRP_DESTINATION,
                            bundleData = extractSrpBundle(urlString)
                        )
                    } else {
                        DeeplinkContent(FEATURE_DISABLED, destination = HOME_DESTINATION, Bundle())
                    }
                }

                url.path.contains(HDP_ENDPOINT) -> {
                    if (isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DEEPLINK_HDP)) {
                        if (matchesDeviceLanguage(urlString)) {
                            DeeplinkContent(
                                deeplinkStatus = KNOWN_DEEPLINK,
                                destination = HDP_DESTINATION,
                                bundleData = extractHdpBundle(urlString)
                            )
                        } else {
                            DeeplinkContent(
                                WRONG_DEEPLINK,
                                destination = HOME_DESTINATION,
                                Bundle()
                            )
                        }
                    } else {
                        DeeplinkContent(FEATURE_DISABLED, destination = HOME_DESTINATION, Bundle())
                    }
                }

                else -> {
                    DeeplinkContent(UNKNOWN_DEEPLINK, destination = HOME_DESTINATION, Bundle())
                }
            }
        } catch (e: MalformedURLException) {
            DeeplinkContent(WRONG_DEEPLINK, destination = HOME_DESTINATION, Bundle())
        }
    }

    private fun matchesDeviceLanguage(urlString: String): Boolean {
        val deviceLanguage = deviceLocaleProvider.getDeviceLanguage()
        return deviceLanguage in urlString.toUri().pathSegments
    }

    private fun extractHdpBundle(urlString: String): Bundle {
        val uri = urlString.toUri()
        val params = uri.queryParameterNames.associateWith { uri.getQueryParameter(it) }
        val bundle = Bundle()
        val appsFlyerSlug = params[APPSFLYER_SLUG_PARAM] ?: EMPTY_STRING
        val uriWithSlug = if (appsFlyerSlug.isNotEmpty()) appsFlyerSlug.toUri() else uri

        val slug = if (uriWithSlug.pathSegments.contains(HOTEL_DETAILS_HOTELS_PATH_SEGMENT)) {
            uriWithSlug.pathSegments
                .drop(uriWithSlug.pathSegments.indexOf(HOTEL_DETAILS_HOTELS_PATH_SEGMENT))
                .joinToString(prefix = HDP_SLUG_SEPARATOR, separator = HDP_SLUG_SEPARATOR)
        } else {
            EMPTY_STRING
        }

        val model = HotelDetailsDeeplinkModel(
                hotelCode = params[HOTEL_CODE_PARAM] ?: EMPTY_STRING,
                day = params[DAY_PARAM]?.toInt() ?: 1,
                month = params[MONTH_PARAM]?.toInt() ?: 1,
                year = params[YEAR_PARAM]?.toInt() ?: 1,
                nights = params[NIGHTS_PARAM]?.toLong() ?: 1,
                adults = mapNumberParam(adultList, params),
                children = mapNumberParam(childList, params),
                infants = mapNumberParam(cotList, params),
                cots = mapCots(cotList, params),
                roomsType = mapRoomType(roomTypeList, params),
                rooms = params[ROOMS_PARAM]?.toInt() ?: 1,
                hotelBrand = params[BRAND_PARAM],
                campaignId = params[CAMPAIGN_ID] ?: EMPTY_STRING,
                slug = slug,
                campaignModel = extractCampaignModel(urlString)
        )
        bundle.putParcelable(HDP_BUNDLE, model)

        return bundle
    }

    private fun mapNumberParam(list: List<String>, params: Map<String, String?>) =
            list.map { key ->
                params[key]?.toIntOrNull() ?: 0
            }

    private fun mapCots(list: List<String>, params: Map<String, String?>) =
            list.map { key ->
                when (params[key]) {
                    ONE_VALUE -> true
                    ZERO_VALUE -> false
                    else -> false
                }
            }

    private fun mapRoomType(list: List<String>, params: Map<String, String?>) =
            list.map { key ->
                params[key] ?: EMPTY_STRING
            }

    private fun extractCiolBundle(urlString: String): Bundle {
        val model = FindBookingInput(
            arrivalDate = getValueOfQueryParam(urlString, ARRIVAL_DATE_PARAM),
            bookingReference = getValueOfQueryParam(urlString, BOOKING_REFERENCE_PARAM),
            lastName = getValueOfQueryParam(urlString, LAST_NAME_PARAM),
            campaignModel = extractCampaignModel(urlString)
        )
        val bundle = Bundle()
        bundle.putParcelable(CIOL_BUNDLE, model)

        return bundle
    }

    private fun extractSrpBundle(urlString: String): Bundle {
        val uri = urlString.toUri()
        val params = uri.queryParameterNames.associateWith { uri.getQueryParameter(it) }
        val adults = mapNumberParam(adultList, params)
        val rooms = params[ROOMS_PARAM]?.toInt() ?: 1

        val model = SearchResultDeeplinkModel(
            placeName = params[SRP_SEARCH_TERM_PARAM] ?: EMPTY_STRING,
            placeId = params[SRP_PLACEID_PARAM] ?: EMPTY_STRING,
            day = params[DAY_PARAM]?.toInt() ?: 0,
            month = params[MONTH_PARAM]?.toInt() ?: 0,
            year = params[YEAR_PARAM]?.toInt() ?: 0,
            nights = params[NIGHTS_PARAM]?.toLong() ?: 1,
            adults = if (adults.any { it != 0 }) adults else listOf(1, 0, 0, 0),
            children = mapNumberParam(childList, params),
            infants = mapNumberParam(cotList, params),
            cots = mapCots(cotList, params),
            roomsType = extractRoomTypesForSrp(rooms, mapRoomType(roomTypeList, params)),
            rooms = rooms,
            campaignModel = extractCampaignModel(urlString)
        )

        val bundle = Bundle()
        bundle.putParcelable(SRP_BUNDLE, model)

        return bundle
    }

    private fun extractRoomTypesForSrp(numberOfRooms: Int, roomTypes: List<String>): List<String> {
        val srpRoomTypes = mutableListOf<String>()
        for (index in 0..< numberOfRooms) {
            val roomType = roomTypes.elementAtOrElse(index) { EMPTY_STRING }
            srpRoomTypes.add(roomType.ifEmpty { RoomType.DOUBLE.code })
        }

        return srpRoomTypes
    }

    private fun extractCampaignModel(urlString: String): CampaignDataModel = CampaignDataModel(
        campaignId = getValueOfQueryParam(urlString, CAMPAIGN_ID),
        googleId = getValueOfQueryParam(urlString, GOOGLE_ID),
        microsoftId = getValueOfQueryParam(urlString, MICROSOFT_ID)
    )

    private fun extractHomeBundle(urlString: String): Bundle {
        val uri = urlString.toUri()
        val params = uri.queryParameterNames.associateWith { uri.getQueryParameter(it) }

        val promoType = params[APPSFLYER_CODE_TYPE]
        val promoCode = params[APPSFLYER_PROMO_CODE]
        val model = PromoCodeInput(
            promoType = promoType,
            promoCode = promoCode
        )

        val homeModel = LandingDeeplinkModel(
            promoCodeInput = model,
            campaignModel = extractCampaignModel(urlString)
        )

        val bundle = Bundle()
        bundle.putParcelable(HOME_BUNDLE, homeModel)

        return bundle
    }

    private fun getValueOfQueryParam(urlString: String, query: String): String {
        val uri = urlString.toUri()
        return uri.queryParameterNames.firstOrNull {
            it.equals(query, ignoreCase = true)
        }?.let { uri.getQueryParameter(it) } ?: EMPTY_STRING
    }
}
