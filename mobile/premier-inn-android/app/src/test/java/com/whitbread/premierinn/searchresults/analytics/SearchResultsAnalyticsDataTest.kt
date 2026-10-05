package com.whitbread.premierinn.searchresults.analytics

import com.contentsquare.android.api.model.CustomVar
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ADOBE_CAMPAIGN_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.GOOGLE_ID
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.MICROSOFT_ID
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PUSH_TOKEN
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type
import com.whitbread.premierinn.common.analytics.CampaignDataModel
import com.whitbread.premierinn.common.utils.DateUtils.getLeadDays
import com.whitbread.premierinn.domain.graphql.srp.entity.CoordinatesDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.FacilityItemDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.LowestRoomRateDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.MessagingFlagDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.ShortHotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.ShortHotelInformationDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.SingleHotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.ThumbnailImageDomain
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.EVAR_RESULT_POSITION
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_ADULTS
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_CHECK_IN
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_CHECK_OUT
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_CHILDREN
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_END_DAY
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_EVENT
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_GUESTS
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_LEAD_DAYS
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_NIGHTS
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_NUM_RESULTS
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_ROOMS
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_ROOM_TYPE
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_SEARCH_LOCATION
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_SEARCH_TYPE
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_START_DAY
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.KEY_START_END_DAY
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.SEARCH_TYPE_HOTEL_SPECIFIC
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.SEARCH_TYPE_LOCATION
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.SEARCH_TYPE_NEAR_ME
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

class SearchResultsAnalyticsDataTest {

    private lateinit var availabilitiesOpera: List<SingleHotelAvailabilityDomain>

    @Before
    fun setUp() {
        availabilitiesOpera = listOf(
            createMockSingleHotelAvailability(
                hotelId = "BURTHY",
                distance = 0.74f,
                netTotal = 29.50f,
                available = true,
                position = 1
            ),
            createMockSingleHotelAvailability(
                hotelId = "MANTGI",
                distance = 4.19f,
                netTotal = 33.50f,
                available = true,
                position = 2,
                messagingText = "New rooms"
            )
        )
    }

    @Test
    fun `contextDataWithProductString using Opera GraphQL`() {
        val data = SearchResultsAnalyticsData.builder()
            .screenType(Type.LOOK_TO_BOOK)
            .searchResultsOpera(availabilitiesOpera)
            .placeName("place name")
            .nights(1)
            .numRooms(3)
            .arrivalDate(LocalDate.of(2018, 5, 22))
            .departureDate(LocalDate.of(2018, 5, 28))
            .numAdults(3)
            .numChildren(2)
            .totalNumOfGuests(5)
            .roomTypes(listOf("Double", "Family"))
            .inMapView(false)
            .pushToken("PushToken123")
            .campaignModel(CampaignDataModel("CID_TEST", "GID_TEST", "MID_TEST"))
            .build()

        assertEquals(expectedContextData(), data.contextData())
    }

    @Test
    fun `mapContextDataDoesntIncludeListOrder using Opera GraphQL`() {
        val data = SearchResultsAnalyticsData.builder()
            .screenType(Type.LOOK_TO_BOOK)
            .searchResultsOpera(availabilitiesOpera)
            .placeName("place name")
            .nights(1)
            .numRooms(3)
            .arrivalDate(LocalDate.of(2018, 4, 22))
            .departureDate(LocalDate.of(2018, 4, 28))
            .numAdults(3)
            .numChildren(2)
            .totalNumOfGuests(5)
            .roomTypes(listOf("Double", "Family"))
            .inMapView(true)
            .pushToken("PushToken123")
            .campaignModel(CampaignDataModel("CID_TEST", "GID_TEST", "MID_TEST"))
            .build()

        val productString = data.contextData()["&&products"].toString()
        assertFalse(productString.contains(String.format(EVAR_RESULT_POSITION, "")))
    }

    @Test
    fun `nonMapContextDataIncludesListOrder using Opera GraphQL`() {
        val data = SearchResultsAnalyticsData.builder()
            .screenType(Type.LOOK_TO_BOOK)
            .searchResultsOpera(availabilitiesOpera)
            .placeName("place name")
            .nights(1)
            .numRooms(3)
            .arrivalDate(LocalDate.of(2018, 4, 22))
            .departureDate(LocalDate.of(2018, 4, 28))
            .numAdults(3)
            .numChildren(2)
            .totalNumOfGuests(5)
            .roomTypes(listOf("Double", "Family"))
            .inMapView(false)
            .pushToken("PushToken123")
            .campaignModel(CampaignDataModel("CID_TEST", "GID_TEST", "MID_TEST"))
            .build()

        val productString = data.contextData()["&&products"].toString()
        assertTrue(productString.contains(String.format(EVAR_RESULT_POSITION, "")))
    }

    @Test
    fun `searchTypeIsNearMe using Opera GraphQL`() {
        val data = SearchResultsAnalyticsData.builder()
            .screenType(Type.LOOK_TO_BOOK)
            .searchResultsOpera(availabilitiesOpera)
            .placeName("My Location")
            .nights(1)
            .numRooms(3)
            .arrivalDate(LocalDate.of(2018, 4, 22))
            .departureDate(LocalDate.of(2018, 4, 28))
            .numAdults(3)
            .numChildren(2)
            .totalNumOfGuests(5)
            .roomTypes(listOf("Double", "Family"))
            .inMapView(false)
            .pushToken("PushToken123")
            .campaignModel(CampaignDataModel("CID_TEST", "GID_TEST", "MID_TEST"))
            .build()

        assertEquals(SEARCH_TYPE_NEAR_ME, data.searchType())
    }

    @Test
    fun `searchTypeIsHotelSpecific using Opera GraphQL`() {
        val data = SearchResultsAnalyticsData.builder()
            .screenType(Type.LOOK_TO_BOOK)
            .searchResultsOpera(availabilitiesOpera)
            .placeName("place name")
            .nights(1)
            .numRooms(3)
            .arrivalDate(LocalDate.of(2018, 4, 22))
            .departureDate(LocalDate.of(2018, 4, 28))
            .numAdults(3)
            .numChildren(2)
            .totalNumOfGuests(5)
            .roomTypes(listOf("Double", "Family"))
            .searchedHotelCode("LONCIT")
            ?.inMapView(false)
            ?.pushToken("PushToken123")
            ?.campaignModel(CampaignDataModel("CID_TEST", "GID_TEST", "MID_TEST"))
            ?.build()

        assertEquals(SEARCH_TYPE_HOTEL_SPECIFIC, data?.searchType())
    }

    @Test
    fun `searchTypeIsLocation using Opera GraphQL`() {
        val data = SearchResultsAnalyticsData.builder()
            .screenType(Type.LOOK_TO_BOOK)
            .searchResultsOpera(availabilitiesOpera)
            .placeName("place name")
            .nights(1)
            .numRooms(3)
            .arrivalDate(LocalDate.of(2018, 4, 22))
            .departureDate(LocalDate.of(2018, 4, 28))
            .numAdults(3)
            .numChildren(2)
            .totalNumOfGuests(5)
            .roomTypes(listOf("Double", "Family"))
            .inMapView(false)
            .pushToken("PushToken123")
            .campaignModel(CampaignDataModel("CID_TEST", "GID_TEST", "MID_TEST"))
            .build()

        assertEquals(SEARCH_TYPE_LOCATION, data.searchType())
    }

    @Test
    fun `testCustomCSQVarsData using Opera GraphQL`() {
        val data = SearchResultsAnalyticsData.builder()
            .screenType(Type.LOOK_TO_BOOK)
            .searchResultsOpera(availabilitiesOpera)
            .placeName("place name")
            .nights(1)
            .numRooms(3)
            .arrivalDate(LocalDate.of(2018, 5, 22))
            .departureDate(LocalDate.of(2018, 5, 28))
            .numAdults(3)
            .numChildren(2)
            .totalNumOfGuests(5)
            .roomTypes(listOf("Double", "Family"))
            .pushToken("PushToken123")
            .campaignModel(CampaignDataModel("CID_TEST", "GID_TEST", "MID_TEST"))
            .inMapView(false)
            .build()

        assertEquals(expectedCustomCSQVarsData(), data.customCSQVars())
    }

    private fun expectedContextData(): Map<String, String> {
        val productsString =
            ";BURTHY;;;event90;eVar62=29.50|eVar50=0.74|eVar55=1,;MANTGI;;;event90;eVar62=" +
                    "33.50|eVar50=4.19|eVar55=2|eVar51=New rooms"

        return mutableMapOf(
            AnalyticsConstants.Key.PRODUCTS to productsString,
            AnalyticsConstants.Key.SCREEN_TYPE to "And:${Type.LOOK_TO_BOOK}",
            KEY_SEARCH_LOCATION to "place name",
            KEY_NIGHTS to "1",
            KEY_ROOMS to "3",
            KEY_SEARCH_TYPE to SEARCH_TYPE_LOCATION,
            KEY_CHECK_IN to "22/05/2018",
            KEY_CHECK_OUT to "28/05/2018",
            KEY_ADULTS to "3",
            KEY_CHILDREN to "2",
            KEY_GUESTS to "5",
            KEY_LEAD_DAYS to getLeadDays(LocalDate.of(2018, 5, 22), LocalDate.now()).toString(),
            KEY_ROOM_TYPE to "Double:Family",
            KEY_NUM_RESULTS to "2",
            KEY_START_END_DAY to "Tue-Mon",
            KEY_START_DAY to "Tue",
            KEY_END_DAY to "Mon",
            KEY_EVENT to "1",
            PUSH_TOKEN to "PushToken123",
            CIOL_ADOBE_CAMPAIGN_KEY to "CID_TEST",
            GOOGLE_ID to "GID_TEST",
            MICROSOFT_ID to "MID_TEST"
        )
    }

    private fun expectedCustomCSQVarsData(): List<CustomVar> {
        return listOf(
            CustomVar(5, KEY_START_END_DAY, "Tue-Mon"),
            CustomVar(3, KEY_NIGHTS, "1"),
            CustomVar(4, KEY_GUESTS, "5")
        )
    }

    private fun createMockSingleHotelAvailability(
        hotelId: String,
        distance: Float,
        netTotal: Float,
        available: Boolean,
        @Suppress("UNUSED_PARAMETER") position: Int,
        messagingText: String = ""
    ): SingleHotelAvailabilityDomain {
        return SingleHotelAvailabilityDomain(
            hotelId = hotelId,
            name = "Mock Hotel $hotelId",
            hotelAvailability = ShortHotelAvailabilityDomain(
                distance = distance,
                lowestRoomRate = LowestRoomRateDomain(
                    netTotal = netTotal,
                    currencyCode = "GBP"
                ),
                available = available,
                limitedAvailability = false,
                pmsSource = "OPERA",
                cellCode = "CELL$hotelId"
            ),
            hotelInformation = ShortHotelInformationDomain(
                brand = "PI",
                thumbnailImages = listOf(
                    ThumbnailImageDomain(imageSrc = "/images/$hotelId.jpg")
                ),
                hotelFacilities = listOf(
                    FacilityItemDomain(code = "WIFI", description = "Free WiFi")
                ),
                messagingFlag = MessagingFlagDomain(text = messagingText, color = ""),
                coordinates = CoordinatesDomain(latitude = 51.5074f, longitude = -0.1278f)
            )
        )
    }
}