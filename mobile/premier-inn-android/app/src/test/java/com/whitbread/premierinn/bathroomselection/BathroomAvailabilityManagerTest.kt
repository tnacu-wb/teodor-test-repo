package com.whitbread.premierinn.bathroomselection

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import com.whitbread.premierinn.amend.toParcelable
import com.whitbread.premierinn.api.response.booking.AlternativeRoom
import com.whitbread.premierinn.api.response.booking.BookingRoom
import com.whitbread.premierinn.api.response.booking.DailyRate
import com.whitbread.premierinn.common.mapper.toBookingPrice
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.hoteldetails.DailyRateInput
import com.whitbread.premierinn.hoteldetails.PriceParcelable
import com.whitbread.premierinn.hoteldetails.AccessibilityFacilities
import com.whitbread.premierinn.common.RoomBooking
import com.whitbread.premierinn.hoteldetails.SelectedHotel
import com.whitbread.premierinn.summary.SummaryInput
import org.junit.Before
import org.threeten.bp.LocalDate

private const val DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX = "AB"
private const val DISABLED_LOWERED_BATH_LETTING_CODE = "ABS"
private const val DISABLED_LOWERED_BATH_TWIN_LETTING_CODE_PREFIX = "IB"
private const val DISABLED_WET_ROOM_LETTING_CODE_PREFIX = "AW"
private const val DISABLED_WET_ROOM_LETTING_CODE_TWIN_PREFIX = "IW"

class BathroomAvailabilityManagerTest {
    private lateinit var defaultBookingRoom: BookingRoom
    private lateinit var defaultAlternativeRoom: AlternativeRoom
    private var listOfMockRoomBooking: ArrayList<RoomBooking> = ArrayList()
    private val defaultAccessibilityFacilities = AccessibilityFacilities(hasWetRooms = true, hasLoweredBaths = true)
    private val hotelInfo = mock<SelectedHotel> {
        on { accessibilityFacilities() } doReturn defaultAccessibilityFacilities
    }
    private val proposedSummaryInput = mock<SummaryInput> {
        on { hotel() } doReturn hotelInfo
        on { roomBookings()} doReturn listOfMockRoomBooking
    }

    @Before
    fun setUp() {
        defaultBookingRoom = BookingRoom(roomNumber = 1,
            type = RoomType.ACCESSIBLE.code,
            lettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX,
            adults = 1,
            children = 0,
            cot = false,
            totalCost = PriceDomain.createWithGBPCurrency(0f).toBookingPrice(),
            cityTax = null,
            alternativeRooms = emptyList(),
            dailyRates = emptyList(),
            status = BookingRoom.Status.ROOM_TYPE_GUARANTEED)

       defaultAlternativeRoom = AlternativeRoom(dailyRates = emptyList(),
            lettingType = null,
            totalCost = PriceDomain.createWithGBPCurrency(0f).toBookingPrice(),
            cityTax = null,
            alternativeType = AlternativeRoom.AlternativeType.ACCESSIBLE,
            numberAvailable = null)

        val dailyRates = getMockDailyRatesInput()

        val room = RoomBooking(
            type = RoomType.ACCESSIBLE.name,
            lettingCode = DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX,
            adults = 1,
            children = 0,
            cot = false,
            roomNumber = 1,
            dailyRates = dailyRates,
            cityTax = PriceDomain.createWithGBPCurrency(0f).toParcelable(),
            infants = 0,
            baseRateAmount = null
        )

        listOfMockRoomBooking.add(room)
    }

    private fun getMockDailyRates(): List<DailyRate> {
        val date1 = LocalDate.of(2021, 11, 17)
        val date2 = LocalDate.of(2021, 11, 18)
        val date3 = LocalDate.of(2021, 11, 19)

        val dailyRates = listOf(
            DailyRate.create(date1, PriceDomain.createWithGBPCurrency(160f).toBookingPrice()),
            DailyRate.create(date2, PriceDomain.createWithGBPCurrency(115f).toBookingPrice()),
            DailyRate.create(date3, PriceDomain.createWithGBPCurrency(81f).toBookingPrice())
        )
        return dailyRates
    }

    private fun getMockDailyRatesInput(): List<DailyRateInput> {
        val date1 = LocalDate.of(2021, 11, 17)
        val date2 = LocalDate.of(2021, 11, 18)
        val date3 = LocalDate.of(2021, 11, 19)

        val dailyRates = listOf(
            DailyRateInput(date1, PriceParcelable(160f, GBP)),
            DailyRateInput(date2, PriceParcelable(115f, GBP)),
            DailyRateInput(date3, PriceParcelable(81f, GBP)))
        return dailyRates
    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN room has no alternatives THEN room selections has the only option selected AND bathroom choice availability has no unselected options`() {
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, listOf(defaultBookingRoom))
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue {
//                    it.bathroomAvailability.size == 1 && it.bathroomAvailability[it.bathroomAvailability.keys.first()] == 0
//                }
//                .assertValue {
//                    it.roomSelections.size == 1
//                            && it.roomSelections[0].roomId == 1
//                            && it.roomSelections[0].selectedLettingType ==  LettingType(DISABLED_WET_ROOM_LETTING_CODE_PREFIX)
//                }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN multiple rooms with no alternatives AND rooms have same bathroom type THEN room selection have the correct bathroom type AND bathroom choice availability has no unselected options`() {
//        val bookingRooms = listOf(defaultBookingRoom, defaultBookingRoom.copy(roomNumber = 2))
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, bookingRooms)
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue { it.roomSelections.size == 2 && it.roomSelections.all { it.selectedLettingType ==  LettingType(DISABLED_WET_ROOM_LETTING_CODE_PREFIX)} }
//                .assertValue { it.bathroomAvailability.none { it.value > 0 }}
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN 2 rooms AND both are compatible with the same alternative THEN total unselected availability for this alternative should be 2 AND both rooms are marked as compatible`() {
//        val alternativeRoom = defaultAlternativeRoom.copy(lettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX, numberAvailable = 2)
//        val bookingRoom = defaultBookingRoom.copy(
//                lettingType = DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX,
//                alternativeRooms = listOf(alternativeRoom))
//
//        val bookingRooms = listOf(bookingRoom, bookingRoom.copy(roomNumber = 2))
//
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, bookingRooms)
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue {
//                    it.bathroomAvailability[LettingType(DISABLED_WET_ROOM_LETTING_CODE_PREFIX).roomTypeCode] == 2
//                }
//                .assertValue {
//                    it.bathroomAvailability[LettingType(DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX).roomTypeCode] == 0
//                }
//                .assertValue {
//                    it.roomSelections.all { it.selectedLettingType.code ==  DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX}
//                }
//                .assertValue {
//                    it.roomSelections.all { it.compatibleBathrooms.contains(LettingType(DISABLED_WET_ROOM_LETTING_CODE_PREFIX))}
//                }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN 2 rooms AND both are compatible with the same alternative AND number available for the alternative room is 1 THEN the alternative should have 1 unselected`() {
//        val alternativeRoom = defaultAlternativeRoom.copy(lettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX, numberAvailable = 1)
//        val bookingRoom = defaultBookingRoom.copy(
//                lettingType = DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX,
//                alternativeRooms = listOf(alternativeRoom))
//
//        val bookingRooms = listOf(bookingRoom, bookingRoom.copy(roomNumber = 2))
//
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, bookingRooms)
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue { it.bathroomAvailability[LettingType(DISABLED_WET_ROOM_LETTING_CODE_PREFIX).roomTypeCode] == 1 }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN a lowered bath is available for a room THEN the room is assigned to lowered bath on initialisation`() {
//        val alternativeRoom = defaultAlternativeRoom.copy(lettingType = DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX, numberAvailable = 1)
//        val bookingRoom = defaultBookingRoom.copy(
//                lettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX,
//                alternativeRooms = listOf(alternativeRoom))
//
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, listOf(bookingRoom))
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue {
//                    it.roomSelections[0].selectedLettingType == LettingType(DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX) }
//                .assertValue { it.bathroomAvailability[LettingType(DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX).roomTypeCode] == 0 }
//                .assertValue { it.bathroomAvailability[LettingType(DISABLED_WET_ROOM_LETTING_CODE_PREFIX).roomTypeCode] == 1 }
//
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN 2 rooms AND only 1 lowered bath is available THEN 1 room is assigned a lowered bath AND another is assigned a wet room`() {
//        val alternativeRoom = defaultAlternativeRoom.copy(lettingType = DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX, numberAvailable = 1)
//        val bookingRoom = defaultBookingRoom.copy(
//                lettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX,
//                alternativeRooms = listOf(alternativeRoom))
//
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, listOf(bookingRoom, bookingRoom.copy(roomNumber = 2)))
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue { it.roomSelections[0].roomId == 1 && it.roomSelections[0].selectedLettingType == LettingType(DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX) }
//                .assertValue { it.roomSelections[1].roomId == 2 && it.roomSelections[1].selectedLettingType == LettingType(DISABLED_WET_ROOM_LETTING_CODE_PREFIX) }
//                .assertValue { it.bathroomAvailability[LettingType(DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX).roomTypeCode] == 0 }
//                .assertValue { it.bathroomAvailability[LettingType(DISABLED_WET_ROOM_LETTING_CODE_PREFIX).roomTypeCode] == 1 }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN 1 room AND room is compatible with Lowered and Wet room WHEN I select a wet room THEN the manager assigns wet bathroom to the room`() {
//        val alternativeRoom = defaultAlternativeRoom.copy(lettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX, numberAvailable = 1)
//        val bookingRoom = defaultBookingRoom.copy(
//                lettingType = DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX,
//                alternativeRooms = listOf(alternativeRoom))
//
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, listOf(bookingRoom))
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.selectBathroomType(1, BathroomType.WET_ROOM)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue { it.roomSelections[0].selectedLettingType.bathroomType == BathroomType.WET_ROOM }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN 1 room  WHEN I select a lowered bath room THEN the selected roombooking is updated`() {
//        val alternativeRoom = defaultAlternativeRoom.copy(lettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX, numberAvailable = 1)
//        val bookingRoom = defaultBookingRoom.copy(
//            lettingType = DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX,
//            dailyRates = getMockDailyRates(),
//            alternativeRooms = listOf(alternativeRoom))
//
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, listOf(bookingRoom))
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.selectBathroomType(1, BathroomType.LOWERED_BATH)
//
//        val actualList = bathroomAvailabilityManager.listOfSelectedRoomBookings()
//        val expectedList = listOfMockRoomBooking
//        assertEquals(actualList,expectedList)
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN 1 room AND room is compatible with Lowered and Wet room WHEN I select a wet room THEN the manager updates the availability counts`() {
//        val alternativeRoom = defaultAlternativeRoom.copy(lettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX, numberAvailable = 1)
//        val bookingRoom = defaultBookingRoom.copy(
//                lettingType = DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX,
//                alternativeRooms = listOf(alternativeRoom))
//
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, listOf(bookingRoom))
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.selectBathroomType(1, BathroomType.WET_ROOM)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue { it.bathroomAvailability[LettingType(DISABLED_WET_ROOM_LETTING_CODE_PREFIX).roomTypeCode] == 0 }
//                .assertValue { it.bathroomAvailability[LettingType(DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX).roomTypeCode] == 1 }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN 1 room AND room is compatible with Lowered and Wet room WHEN I select a wet room AND then I select a lowered bath THEN the manager assigns lowered bath to the room`() {
//        val alternativeRoom = defaultAlternativeRoom.copy(lettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX, numberAvailable = 1)
//        val bookingRoom = defaultBookingRoom.copy(
//                lettingType = DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX,
//                alternativeRooms = listOf(alternativeRoom))
//
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, listOf(bookingRoom))
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.selectBathroomType(1, BathroomType.WET_ROOM)
//        bathroomAvailabilityManager.selectBathroomType(1, BathroomType.LOWERED_BATH)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue { it.roomSelections[0].selectedLettingType.bathroomType == BathroomType.LOWERED_BATH }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN 1 room AND room is not compatible with lowered bath AND selection to lowered bath is attempted THEN selections remain unchanged`() {
//        val bookingRoom = defaultBookingRoom.copy(
//                lettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX,
//                alternativeRooms = emptyList())
//
//
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, listOf(bookingRoom))
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.selectBathroomType(1, BathroomType.LOWERED_BATH)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue { it.roomSelections[0].selectedLettingType.bathroomType == BathroomType.WET_ROOM }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `GIVEN 2 rooms AND no more availability remains for lowered bath AND selection to lowered bath is attempted THEN selections remain unchanged`() {
//        val alternativeRoom = defaultAlternativeRoom.copy(lettingType = DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX, numberAvailable = 1)
//        val bookingRoom = defaultBookingRoom.copy(
//                lettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX,
//                alternativeRooms = listOf(alternativeRoom))
//
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, listOf(bookingRoom, bookingRoom.copy(roomNumber = 2)))
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.selectBathroomType(2, BathroomType.LOWERED_BATH)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue { it.roomSelections[1].selectedLettingType.bathroomType == BathroomType.WET_ROOM }
//    }

    //TODO: Convert to Opera for BathroomSelectionInput
//    @Test
//    fun `Selected rooms are subtracted from total available rooms`() {
//        val baseLettingType = DISABLED_WET_ROOM_LETTING_CODE_PREFIX
//        val alternativeRoom = defaultAlternativeRoom.copy(lettingType = baseLettingType, numberAvailable = 4)
//        val secondAlternativeRoom = defaultAlternativeRoom.copy(lettingType = "$baseLettingType+C", numberAvailable = 4)
//        val bookingRoom = defaultBookingRoom.copy(lettingType = baseLettingType + "S", alternativeRooms = listOf(alternativeRoom, secondAlternativeRoom))
//
//        val bathroomSelectionInput = BathroomSelectionInput(proposedSummaryInput, listOf(bookingRoom, bookingRoom.copy(roomNumber = 2)))
//        val bathroomAvailabilityManager = BathroomAvailabilityManager(bathroomSelectionInput)
//
//        bathroomAvailabilityManager.bathroomAvailabilityObservable.test()
//                .assertValue { it.bathroomAvailability[RoomTypeCode(baseLettingType)] == 2 }
//    }

}
