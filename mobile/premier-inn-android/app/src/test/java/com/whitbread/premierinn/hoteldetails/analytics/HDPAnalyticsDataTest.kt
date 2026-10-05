package com.whitbread.premierinn.hoteldetails.analytics

import com.contentsquare.android.api.model.CustomVar
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_NAME
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ADOBE_CAMPAIGN_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.GOOGLE_ID
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.MICROSOFT_ID
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PUSH_TOKEN
import com.whitbread.premierinn.common.analytics.CampaignDataModel
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.SEARCH_TYPE_MAP
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.SEARCH_TYPE_NEAR_ME
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HDPAnalyticsDataTest {

    private var screenType = "Look to Book"
    private var products = ";BANBRI;;;event90;eVar65=FLEXRATE-FMTRPL-999.00"
    private var placeName = "Bangor·(Gwynedd,·North·Wales)"
    private var nights = "1"
    private var numOfRooms = "1"
    private var searchType = "hotel·specific"
    private var checkIn = "2024-07-11"
    private var checkout = "2024-07-12"
    private var adults = "2"
    private var children = "1"
    private var guests = "3"
    private var leadDays = "28"
    private var roomTypes = "FAM"
    private var results = "10"
    private var startEndDay = "Thu-Fri"
    private var startDay = "Thu"
    private var endDay = "Fri"
    private var event = "1"
    private var promoCode = "PROMO_CODE"
    private var promoName = "App Incentive - Generic Promo"
    private var rateTags = "Flexible Rate"

    private lateinit var hdpAnalyticsData: HDPAnalyticsData

    @Before
    fun setUp() {
        hdpAnalyticsData = HDPAnalyticsData(
            screenType = screenType,
            products = products,
            placeName = placeName,
            nights = nights,
            numOfRooms = numOfRooms,
            searchType = searchType,
            checkIn = checkIn,
            checkout = checkout,
            adults = adults,
            children = children,
            guests = guests,
            leadDays = leadDays,
            roomTypes = roomTypes,
            results = results,
            startEndDay = startEndDay,
            startDay = startDay,
            endDay = endDay,
            event = event,
            promoCode = promoCode,
            promoName = promoName,
            rateTags = rateTags,
            pushToken = "PushToken123",
            campaignModel = CampaignDataModel(campaignId = "CID_TEST", googleId = "GID_TEST", microsoftId = "MID_TEST"),
            trackingCode = "TRACKING_CODE_TEST"
        )
    }

    @Test
    fun `test contextData`() {
        val expectedData = mutableMapOf(
            HDPAnalyticsData.SCREEN_TYPE to screenType,
            HDPAnalyticsData.PRODUCTS to products,
            HDPAnalyticsData.KEY_SEARCH_LOCATION to placeName,
            HDPAnalyticsData.KEY_NIGHTS to nights,
            HDPAnalyticsData.KEY_ROOMS to numOfRooms,
            HDPAnalyticsData.KEY_SEARCH_TYPE to searchType,
            HDPAnalyticsData.KEY_CHECK_IN to checkIn,
            HDPAnalyticsData.KEY_CHECK_OUT to checkout,
            HDPAnalyticsData.KEY_ADULTS to adults,
            HDPAnalyticsData.KEY_CHILDREN to children,
            HDPAnalyticsData.KEY_GUESTS to guests,
            HDPAnalyticsData.KEY_LEAD_DAYS to leadDays,
            HDPAnalyticsData.KEY_ROOM_TYPE to roomTypes,
            HDPAnalyticsData.KEY_NUM_RESULTS to results,
            HDPAnalyticsData.KEY_START_END_DAY to startEndDay,
            HDPAnalyticsData.KEY_START_DAY to startDay,
            HDPAnalyticsData.KEY_END_DAY to endDay,
            HDPAnalyticsData.KEY_EVENT to event,
            PUSH_TOKEN to "PushToken123",
            CIOL_ADOBE_CAMPAIGN_KEY to "CID_TEST",
            GOOGLE_ID to "GID_TEST",
            MICROSOFT_ID to "MID_TEST",
            CIOL_ADOBE_CAMPAIGN_KEY to "TRACKING_CODE_TEST"
        ).apply {
            if (promoCode.isNotEmpty()){
                put(HDPAnalyticsData.KEY_PROMO_CODE, promoCode)
                put(PROMO_NAME, promoName)
            }
            if (rateTags.isNotEmpty()) {
                put(HDPAnalyticsData.KEY_RATE_TAGS, rateTags)
            }
        }

        val actualData = hdpAnalyticsData.contextData()

        assertEquals(expectedData, actualData)
    }

    @Test
    fun `searchType is NearMe`() {
        val data = hdpAnalyticsData.copy(searchType = SEARCH_TYPE_NEAR_ME)

        assertEquals(SEARCH_TYPE_NEAR_ME, data.searchType)
    }

    @Test
    fun `searchType is Map`() {
        val data = hdpAnalyticsData.copy(searchType = SEARCH_TYPE_MAP)

        assertEquals(SEARCH_TYPE_MAP, data.searchType)
    }

    @Test
    fun `test customCSQVars`() {
        val expectedData = listOf(
            CustomVar(5, HDPAnalyticsData.KEY_START_END_DAY, startEndDay),
            CustomVar(3, HDPAnalyticsData.KEY_NIGHTS, nights),
            CustomVar(4, HDPAnalyticsData.KEY_GUESTS, guests)
        )

        val actualData = hdpAnalyticsData.customCSQVars()

        assertEquals(expectedData, actualData)
    }
}