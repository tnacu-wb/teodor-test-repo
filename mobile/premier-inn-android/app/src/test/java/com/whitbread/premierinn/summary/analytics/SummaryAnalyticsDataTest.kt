package com.whitbread.premierinn.summary.analytics

import androidx.core.util.Pair
import com.contentsquare.android.api.model.CustomVar
import com.whitbread.premierinn.api.response.availability.UpsellItem
import com.whitbread.premierinn.common.RoomBooking
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ALL_DISCOUNT_CODE_APPLIED
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ALL_RATE_TAGS
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.EVENTS
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_LETTING_TYPE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.KEY_RATE_CODE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PRODUCTS
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_CODE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_NAME
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PUSH_TOKEN
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.SCREEN_TYPE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedArrivalDate
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckInDay
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckInOutDay
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckOutDay
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedDepartureDate
import com.whitbread.premierinn.summary.mockExtrasItem
import com.whitbread.premierinn.summary.mockRoomBooking
import com.whitbread.premierinn.summary.mockUpsellItem
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.LocalDate

@RunWith(MockitoJUnitRunner::class)
class SummaryAnalyticsDataTest {
    private lateinit var upsellItems: List<UpsellItem>
    private lateinit var selectedRooms: List<RoomBooking>
    private lateinit var extrasItems: List<ParcelableExtrasItem>
    private val arrivalDate: LocalDate = LocalDate.of(2026, 2, 9)

    @Before
    fun setUp() {
        upsellItems = listOf(mockUpsellItem())
        selectedRooms = listOf(mockRoomBooking())
        extrasItems = listOf(mockExtrasItem())
    }

    @Test
    fun validContextData() {
        val upsell = upsellItems.first()
        val room = selectedRooms.first()
        val data = SummaryAnalyticsData.builder()
            .hotelCode("LONHMP")
            .prepaid(false)
            .rateCode("FLEX")
            .rateDescription("FLEX-LETTINGTYPE-10.00")
            .rateName("Flex")
            .extrasShown(Pair(upsellItems, extrasItems))
            .checkInDate(arrivalDate)
            .nights(1)
            .rooms(1)
            .adults(room.adults)
            .children(room.children)
            .lettingType(room.lettingCode)
            .selectedRooms(selectedRooms)
            .roomPrice(PriceDomain.createWithGBPCurrency(10f))
            .pushToken("PushToken123")
            .promoCode("promo_code")
            .promoName("promo_name")
            .rateTag("rate_tag")
            .build()

        assertEquals(expectedContextData(upsell, room), data.contextData())
    }

    @Test
    fun validCustomCSQVarsData() {
        val room = selectedRooms.first()
        val data = SummaryAnalyticsData.builder()
            .hotelCode("LONHMP")
            .prepaid(false)
            .rateCode("FLEX")
            .rateDescription("FLEX-LETTINGTYPE-10.00")
            .rateName("Flex")
            .extrasShown(Pair(upsellItems, extrasItems))
            .checkInDate(arrivalDate)
            .nights(1)
            .rooms(1)
            .adults(room.adults)
            .children(room.children)
            .lettingType(room.lettingCode)
            .selectedRooms(selectedRooms)
            .roomPrice(PriceDomain.createWithGBPCurrency(10f))
            .pushToken("PushToken123")
            .build()

        assertEquals(expectedCustomCSQVarsData(room), data.customCSQVars())
    }

    private fun expectedContextData(upsell: UpsellItem, room: RoomBooking): Map<String, String> {
        val departureDate = arrivalDate.plusDays(1)
        val contextData = HashMap<String, String>()
        contextData[SummaryAnalyticsData.KEY_PREPAY] = "Non-Prepay"
        contextData[KEY_RATE_CODE] = "FLEX"
        contextData[SummaryAnalyticsData.KEY_RATE_DESCRIPTION] = "FLEX-LETTINGTYPE-10.00"
        contextData[SummaryAnalyticsData.KEY_RATE_NAME] = "Flex"
        val extrasShownCode = listOf(upsell.code(), extrasItems.first().id).joinToString(",")
        val extrasShownDescription =
            listOf(upsell.legend(), extrasItems.first().name).joinToString(",")
        contextData[SummaryAnalyticsData.KEY_EXTRAS_SHOWN_DESCRIPTION] = extrasShownDescription
        contextData[SummaryAnalyticsData.KEY_EXTRAS_SHOWN_CODE] = extrasShownCode
        contextData[SummaryAnalyticsData.KEY_CHECK_IN_DATE] =
            formattedArrivalDate(arrivalDate).orEmpty()
        contextData[SummaryAnalyticsData.KEY_CHECK_OUT_DATE] =
            formattedDepartureDate(departureDate).orEmpty()
        contextData[SummaryAnalyticsData.KEY_CHECK_IN_DAY] =
            formattedCheckInDay(arrivalDate).orEmpty()
        contextData[SummaryAnalyticsData.KEY_CHECK_OUT_DAY] =
            formattedCheckOutDay(departureDate).orEmpty()
        contextData[SummaryAnalyticsData.KEY_CHECK_IN_OUT_DAY] =
            formattedCheckInOutDay(arrivalDate, departureDate).orEmpty()
        contextData[SummaryAnalyticsData.KEY_NIGHTS] = "1"
        contextData[SummaryAnalyticsData.KEY_ROOMS] = "1"
        contextData[SummaryAnalyticsData.KEY_ADULTS] = room.adults.toString()
        contextData[SummaryAnalyticsData.KEY_CHILDREN] = room.children.toString()
        contextData[PRODUCTS] = ";LONHMP"
        contextData[EVENTS] = "scOpen"
        contextData[SCREEN_TYPE] = Type.BOOKING_FLOW
        contextData[PUSH_TOKEN] = "PushToken123"
        contextData[PROMO_CODE] = "promo_code"
        contextData[PROMO_NAME] = "promo_name"
        contextData[ALL_RATE_TAGS] = "rate_tag"
        contextData[ALL_DISCOUNT_CODE_APPLIED] = "true"
        return contextData
    }

    private fun expectedCustomCSQVarsData(room: RoomBooking): List<CustomVar> {
        return listOf(
            CustomVar(7, KEY_RATE_CODE, "FLEX"),
            CustomVar(19, KEY_LETTING_TYPE, room.lettingCode)
        )
    }
}