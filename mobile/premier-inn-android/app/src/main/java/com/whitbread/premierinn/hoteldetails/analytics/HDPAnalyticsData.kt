package com.whitbread.premierinn.hoteldetails.analytics

import com.contentsquare.android.api.model.CustomVar
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_NAME
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PUSH_TOKEN
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ADOBE_CAMPAIGN_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.GOOGLE_ID
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.MICROSOFT_ID
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.analytics.CampaignDataModel

data class HDPAnalyticsData(
    val screenType: String,
    val products: String,
    val placeName: String,
    val nights: String,
    val numOfRooms: String,
    val searchType: String,
    val checkIn: String,
    val checkout: String,
    val adults: String,
    val children: String,
    val guests: String,
    val leadDays: String,
    val roomTypes: String,
    val results: String,
    val startEndDay: String,
    val startDay: String,
    val endDay: String,
    val event: String,
    val pushToken: String,
    val promoCode: String?,
    val promoName: String?,
    val rateTags: String?,
    val campaignModel: CampaignDataModel,
    val trackingCode: String?
) : AnalyticsData {
    companion object {
        const val SCREEN_TYPE = "analyticsData.all.screenType"
        const val PRODUCTS = "&&products"
        const val KEY_SEARCH_LOCATION = "analyticsData.search.searchLocation"
        const val KEY_NIGHTS = "analyticsData.search.nights"
        const val KEY_ROOMS = "analyticsData.search.rooms"
        const val KEY_SEARCH_TYPE = "analyticsData.search.searchType"
        const val KEY_CHECK_IN = "analyticsData.search.check-in"
        const val KEY_CHECK_OUT = "analyticsData.search.check-out"
        const val KEY_ADULTS = "analyticsData.search.adults"
        const val KEY_CHILDREN = "analyticsData.search.children"
        const val KEY_GUESTS = "analyticsData.search.guests"
        const val KEY_LEAD_DAYS = "analyticsData.search.leadDays"
        const val KEY_ROOM_TYPE = "analyticsData.search.roomType"//when multiples concatenated as "Double:Double:Family"
        const val KEY_NUM_RESULTS = "analyticsData.search.numResults"
        const val KEY_START_END_DAY = "analyticsData.search.startEndDay"
        const val KEY_START_DAY = "analyticsData.search.startDay"
        const val KEY_END_DAY = "analyticsData.search.endDay"
        const val KEY_EVENT = "analyticsData.search.event.event1"
        const val KEY_PROMO_CODE = "analyticsData.search.promoCode"
        const val KEY_RATE_TAGS = "analyticsData.search.rateTags"
    }

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf(
            SCREEN_TYPE to screenType,
            PRODUCTS to products,
            KEY_SEARCH_LOCATION to placeName,
            KEY_NIGHTS to nights,
            KEY_ROOMS to numOfRooms,
            KEY_SEARCH_TYPE to searchType,
            KEY_CHECK_IN to checkIn,
            KEY_CHECK_OUT to checkout,
            KEY_ADULTS to adults,
            KEY_CHILDREN to children,
            KEY_GUESTS to guests,
            KEY_LEAD_DAYS to leadDays,
            KEY_ROOM_TYPE to roomTypes,
            KEY_NUM_RESULTS to results,
            KEY_START_END_DAY to startEndDay,
            KEY_START_DAY to startDay,
            KEY_END_DAY to endDay,
            KEY_EVENT to event,
            PUSH_TOKEN to pushToken
        ).apply {
            if (!promoCode.isNullOrEmpty()) {
                put(KEY_PROMO_CODE, promoCode)
                put(PROMO_NAME, promoName!!)
            }

            if (!rateTags.isNullOrEmpty()) {
                put(KEY_RATE_TAGS, rateTags)
            }

            if (campaignModel.campaignId.isNotEmpty()) {
                put(CIOL_ADOBE_CAMPAIGN_KEY, campaignModel.campaignId)
            }

            if (campaignModel.googleId.isNotEmpty()) {
                put(GOOGLE_ID, campaignModel.googleId)
            }

            if (campaignModel.microsoftId.isNotEmpty()) {
                put(MICROSOFT_ID, campaignModel.microsoftId)
            }

            if (!trackingCode.isNullOrEmpty()) {
                put(CIOL_ADOBE_CAMPAIGN_KEY, trackingCode)
            }
        }
    }

    override fun customCSQVars(): List<CustomVar> {
        return listOf(
            CustomVar(5, KEY_START_END_DAY, startEndDay),
            CustomVar(3, KEY_NIGHTS, nights),
            CustomVar(4, KEY_GUESTS, guests)
        )
    }
}