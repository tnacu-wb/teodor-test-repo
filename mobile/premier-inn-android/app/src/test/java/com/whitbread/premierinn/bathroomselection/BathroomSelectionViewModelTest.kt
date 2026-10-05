package com.whitbread.premierinn.bathroomselection

import org.mockito.kotlin.*
import com.whitbread.premierinn.api.response.booking.BookingRoom
import com.whitbread.premierinn.common.mapper.toBookingPrice
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.bathroomselection.entity.BathroomType.LOWERED_BATH
import com.whitbread.premierinn.domain.bathroomselection.entity.BathroomType.WET_ROOM
import com.whitbread.premierinn.domain.bathroomselection.entity.RoomBathroomChoices
import com.whitbread.premierinn.domain.common.LettingType
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.hoteldetails.AccessibilityFacilities
import com.whitbread.premierinn.hoteldetails.SelectedHotel
import com.whitbread.premierinn.summary.SummaryInput
import com.whitbread.premierinn.utils.RxJavaTestRule
import org.junit.Rule
import java.util.*

class BathroomSelectionViewModelTest {

    private val DISABLED_LOWERED_BATH_LETTING_CODE = LettingType("AB")
    private val DISABLED_LOWERED_BATH_TWIN_LETTING_CODE = LettingType("IB")
    private val DISABLED_WET_ROOM_LETTING_CODE = LettingType("AW")
    private val DISABLED_WET_ROOM_LETTING_CODE_TWIN = LettingType("IW")

    @get:Rule
    val rxRule = RxJavaTestRule()

    private val stringProvider = mock<BathroomSelectionStringProvider> {
        on { getAdults(any()) } doReturn "1 Adult"
        on { bathroomTitle(any()) } doReturn "Bathroom name"
        on { bathroomDescription(any()) } doReturn "Bathroom description"
        on { noMoreAvailableMessage(eq(WET_ROOM), any(), any()) } doReturn "NO_WETROOMS_AVAILABLE"
        on { noMoreAvailableMessage(eq(LOWERED_BATH), any(), any()) } doReturn "NO_LOWEREDBATHS_AVAILABLE"
        on { hotelDoesNotOfferBathroomMessage(eq(WET_ROOM)) } doReturn "HOTEL_NO_WETROOMS"
        on { hotelDoesNotOfferBathroomMessage(eq(LOWERED_BATH)) } doReturn "HOTEL_NO_LOWERED_BATHS"
        on { roomSizeDescription(any()) } doReturn "ROOM_SIZE_DESCRIPTION"
    }
    private val bathroomAvailabilityManager = mock<BathroomAvailabilityManager>()
    private val defaultAccessibilityFacilities = AccessibilityFacilities(hasWetRooms = true, hasLoweredBaths = true)
    private val hotelInfo = mock<SelectedHotel> {
        on { accessibilityFacilities() } doReturn defaultAccessibilityFacilities
    }
    private val proposedSummaryInput = mock<SummaryInput> {
        on { hotel() } doReturn hotelInfo
    }

    private val deviceLocaleProvider = mock<DeviceLocaleProvider>() {
        on {this.getDeviceLocale()} doReturn Locale.UK
    }

    private val roomBathroomChoicesLoweredBath = RoomBathroomChoices(1, DISABLED_LOWERED_BATH_LETTING_CODE, listOf(DISABLED_LOWERED_BATH_LETTING_CODE))

    private val defaultBookingRoom = BookingRoom(roomNumber = 1,
            dailyRates = emptyList(),
            type = RoomType.ACCESSIBLE.code,
            adults = 1,
            children = 0,
            cot = false,
            totalCost = PriceDomain.createWithGBPCurrency(0f).toBookingPrice(),
            cityTax = null,
            alternativeRooms = emptyList(),
            lettingType = DISABLED_WET_ROOM_LETTING_CODE.code,
            status = BookingRoom.Status.ALTERNATIVE_ROOM_TYPE_OFFERED)

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun testSingleRoomBothOptionsDisplayed() {
//        val bathroomAvailabilityBoth = mapOf(Pair(DISABLED_LOWERED_BATH_LETTING_CODE.roomTypeCode, 1),
//                Pair(DISABLED_WET_ROOM_LETTING_CODE.roomTypeCode, 1))
//
//        val roomBathroomChoicesBoth = roomBathroomChoicesLoweredBath.copy(
//                compatibleBathrooms = listOf(
//                        DISABLED_LOWERED_BATH_LETTING_CODE,
//                       DISABLED_WET_ROOM_LETTING_CODE)
//        )
//
//        `when`(bathroomAvailabilityManager.bathroomAvailabilityObservable).thenReturn(Flowable.just(
//                BathroomSelectionInfo(bathroomAvailabilityBoth, listOf(roomBathroomChoicesBoth))))
//
//        val input = BathroomSelectionInput(proposedSummaryInput, listOf(defaultBookingRoom))
//
//        val viewModel = BathroomSelectionViewModel(input, bathroomAvailabilityManager,
//                stringProvider, deviceLocaleProvider,
//            )
//
//        viewModel.states()
//                .test()
//                .assertValue { it.accessibleRoomChoices.size == 1}
//                .assertValue { it.accessibleRoomChoices[0].secondBathroomOption != null}
//                .assertValue { it.accessibleRoomChoices[0].firstBathroomOption.bathroomType == WET_ROOM }
//                .assertValue { it.accessibleRoomChoices[0].secondBathroomOption!!.bathroomType == LOWERED_BATH }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun roomOnlyCompatibleOptionDisplayed() {
//        val bathroomAvailabilityBoth = mapOf(Pair(DISABLED_LOWERED_BATH_LETTING_CODE.roomTypeCode, 1),
//                Pair(DISABLED_WET_ROOM_LETTING_CODE.roomTypeCode, 1))
//
//        `when`(bathroomAvailabilityManager.bathroomAvailabilityObservable).thenReturn(Flowable.just(
//                BathroomSelectionInfo(bathroomAvailabilityBoth, listOf(roomBathroomChoicesLoweredBath))))
//
//        val input = BathroomSelectionInput(proposedSummaryInput, listOf(defaultBookingRoom))
//
//        val viewModel = BathroomSelectionViewModel(input, bathroomAvailabilityManager,
//                stringProvider, deviceLocaleProvider,
//        )
//
//        viewModel.states().test()
//                .assertValue { it.accessibleRoomChoices.size == 1}
//                .assertValue { it.accessibleRoomChoices[0].secondBathroomOption == null}
//                .assertValue { it.accessibleRoomChoices[0].firstBathroomOption.bathroomType == LOWERED_BATH }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun roomOptionSelectionShownWhenChanged() {
//        val bathroomAvailabilityBoth = mapOf(Pair(DISABLED_LOWERED_BATH_LETTING_CODE.roomTypeCode, 1),
//                Pair(DISABLED_WET_ROOM_LETTING_CODE.roomTypeCode, 1))
//
//        val roomBathroomChoicesBoth = roomBathroomChoicesLoweredBath.copy(
//                compatibleBathrooms = listOf(
//                        DISABLED_LOWERED_BATH_LETTING_CODE,
//                        DISABLED_WET_ROOM_LETTING_CODE)
//        )
//        val firstState = BathroomSelectionInfo(bathroomAvailabilityBoth, listOf(roomBathroomChoicesBoth))
//        val secondState = BathroomSelectionInfo(bathroomAvailabilityBoth, listOf(roomBathroomChoicesBoth.copy(selectedLettingType = DISABLED_WET_ROOM_LETTING_CODE)))
//
//        `when`(bathroomAvailabilityManager.bathroomAvailabilityObservable).thenReturn(Flowable.just(firstState, secondState))
//
//        val input = BathroomSelectionInput(proposedSummaryInput, listOf(defaultBookingRoom))
//
//        val viewModel = BathroomSelectionViewModel(input, bathroomAvailabilityManager,
//                stringProvider, deviceLocaleProvider,
//        )
//
//        viewModel.states()
//                .map { it.accessibleRoomChoices[0].toBathroomList() }
//                .test()
//                .assertValue { it.find { it.bathroomType == WET_ROOM }!!.isBathroomSelected }
//                .assertValue { !it.find { it.bathroomType == LOWERED_BATH }!!.isBathroomSelected }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN one room AND there is no alternative wet room available THEN a warning message is shown on the lowered bath option`() {
//        val bathroomAvailabilityNoWetrooms = mapOf(Pair(DISABLED_LOWERED_BATH_LETTING_CODE.roomTypeCode, 1),
//                Pair(DISABLED_WET_ROOM_LETTING_CODE.roomTypeCode, 0))
//
//        val roomChoices = roomBathroomChoicesLoweredBath.copy(
//                selectedLettingType = DISABLED_LOWERED_BATH_LETTING_CODE,
//                compatibleBathrooms = listOf(
//                        DISABLED_LOWERED_BATH_LETTING_CODE,
//                        DISABLED_WET_ROOM_LETTING_CODE)
//        )
//
//        `when`(bathroomAvailabilityManager.bathroomAvailabilityObservable)
//                .thenReturn(Flowable.just(
//                        BathroomSelectionInfo(bathroomAvailabilityNoWetrooms, listOf(roomChoices))))
//
//        val input = BathroomSelectionInput(proposedSummaryInput, listOf(defaultBookingRoom))
//
//        val viewModel = BathroomSelectionViewModel(input, bathroomAvailabilityManager,
//                stringProvider, deviceLocaleProvider,
//        )
//
//        viewModel.states()
//                .map { it.accessibleRoomChoices[0].toBathroomList() }
//                .test()
//                .assertValue { it[0].warningMessage == "NO_WETROOMS_AVAILABLE"}
//                .assertValue { it.find { it.bathroomType == WET_ROOM } == null}
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `Hotel does not offer wet rooms`() {
//        val loweredBathAvailability = mapOf(Pair(DISABLED_LOWERED_BATH_LETTING_CODE.roomTypeCode, 1))
//
//        `when`(bathroomAvailabilityManager.bathroomAvailabilityObservable).thenReturn(Flowable.just(
//                BathroomSelectionInfo(loweredBathAvailability, listOf(roomBathroomChoicesLoweredBath))))
//
//        val input = BathroomSelectionInput(summaryInputNoWetRooms(), listOf(defaultBookingRoom))
//
//        val viewModel = BathroomSelectionViewModel(input, bathroomAvailabilityManager,
//                stringProvider, deviceLocaleProvider,
//        )
//
//        viewModel.states().test()
//                .assertValue { it.accessibleRoomChoices.size == 1}
//                .assertValue { it.accessibleRoomChoices[0].secondBathroomOption == null}
//                .assertValue { it.accessibleRoomChoices[0].firstBathroomOption.warningMessage == "HOTEL_NO_WETROOMS" }
//    }

    private fun summaryInputNoWetRooms(): SummaryInput {
        val hotelAccessibilityFacilities = AccessibilityFacilities(hasWetRooms = false, hasLoweredBaths = true)
        val hotelInfo = mock<SelectedHotel> {
            on { accessibilityFacilities() } doReturn hotelAccessibilityFacilities
        }
        return mock {
            on { hotel() } doReturn hotelInfo
        }
    }

    private fun AccessibleRoomChoices.toBathroomList(): List<BathroomOption> {
        secondBathroomOption?.let {
            return listOf(firstBathroomOption, it)
        } ?: run {
            return listOf(firstBathroomOption)
        }
    }
}
