package com.whitbread.premierinn.summary

import com.whitbread.premierinn.amend.toParcelable
import com.whitbread.premierinn.api.response.availability.UpsellItem
import com.whitbread.premierinn.common.RoomBooking
import com.whitbread.premierinn.common.mapper.toBookingPrice
import com.whitbread.premierinn.common.summary.model.SummaryExtrasItem
import com.whitbread.premierinn.common.summary.model.SummaryMealItem
import com.whitbread.premierinn.common.summary.model.SummaryPrice
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.hoteldetails.AccessibilityFacilities
import com.whitbread.premierinn.hoteldetails.DailyRateInput
import com.whitbread.premierinn.hoteldetails.SelectedHotel
import com.whitbread.premierinn.hoteldetails.SelectedRate
import com.whitbread.premierinn.hoteldetails.toPriceParcelable
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem
import com.whitbread.premierinn.summary.models.ParcelableMenuAndAllergyInfo
import org.threeten.bp.LocalDate

fun mockSummaryInput(): SummaryInput {
    return SummaryInput.builder()
        .bookingId("")
        .hotel(mockSelectedHotel())
        .hotelBrand("PI")
        .prepaymentAllowed(false)
        .cityTaxForLeisure(false)
        .cityTaxForBusiness(false)
        .arrivalDateGQ("2025-03-12")
        .departureDateGQ("2025-03-13")
        .specialRequests(listOf(listOf("DBLE")))
        .packageCode(null)
        .packageAmount(null)
        .totalNights(1)
        .menus(listOf(mockParcelableMenuAndAllergyInfo()))
        .allergyInfo(listOf(mockParcelableMenuAndAllergyInfo()))
        .rate(mockSelectedRate())
        .roomBookings(listOf(mockRoomBooking()))
        .accessibleRoomBookings(emptyList())
        .twinRoomBookings(emptyList())
        .upsellItems(listOf(mockUpsellItem()))
        .ancillaryCloseOutItems(emptyList())
        .extrasItems(listOf(mockExtrasItem()))
        .isAlternativeRoom(false)
        .isDinnerAvailableNotPIBA(false)
        .isDinnerAvailablePIBA(false)
        .isLoggedIn(false)
        .isBusinessUser(false)
        .isEmployeeRateSelected(false)
        .selectedRoomClass("ST")
        .dinnerAllowance(0f)
        .basketReference("basketRef123")
        .build()
}

fun mockSummaryInput_loginTrue(): SummaryInput {
    return SummaryInput.builder()
        .bookingId("")
        .hotel(mockSelectedHotel())
        .hotelBrand("PI")
        .prepaymentAllowed(false)
        .cityTaxForLeisure(false)
        .cityTaxForBusiness(false)
        .arrivalDateGQ("2025-03-12")
        .departureDateGQ("2025-03-13")
        .specialRequests(listOf(listOf("DBLE")))
        .packageCode(null)
        .packageAmount(null)
        .totalNights(1)
        .menus(listOf(mockParcelableMenuAndAllergyInfo()))
        .allergyInfo(listOf(mockParcelableMenuAndAllergyInfo()))
        .rate(mockSelectedRate())
        .roomBookings(listOf(mockRoomBooking()))
        .accessibleRoomBookings(emptyList())
        .twinRoomBookings(emptyList())
        .upsellItems(listOf(mockUpsellItem()))
        .ancillaryCloseOutItems(emptyList())
        .extrasItems(listOf(mockExtrasItem()))
        .isAlternativeRoom(false)
        .isDinnerAvailableNotPIBA(false)
        .isDinnerAvailablePIBA(false)
        .isLoggedIn(true)
        .isBusinessUser(false)
        .isEmployeeRateSelected(false)
        .dinnerAllowance(0f)
        .basketReference("basketRef123")
        .selectedRoomClass("ST")
        .build()
}

fun mockSummaryInput_businessUser(): SummaryInput {
    return SummaryInput.builder()
        .bookingId("")
        .hotel(mockSelectedHotel())
        .hotelBrand("PI")
        .prepaymentAllowed(false)
        .cityTaxForLeisure(false)
        .cityTaxForBusiness(false)
        .arrivalDateGQ("2025-03-12")
        .departureDateGQ("2025-03-13")
        .specialRequests(listOf(listOf("DBLE")))
        .packageCode(null)
        .packageAmount(null)
        .totalNights(1)
        .menus(listOf(mockParcelableMenuAndAllergyInfo()))
        .allergyInfo(listOf(mockParcelableMenuAndAllergyInfo()))
        .rate(mockSelectedRate())
        .roomBookings(listOf(mockRoomBooking()))
        .accessibleRoomBookings(emptyList())
        .twinRoomBookings(emptyList())
        .upsellItems(listOf(mockUpsellItem()))
        .ancillaryCloseOutItems(emptyList())
        .extrasItems(listOf(mockExtrasItem()))
        .isAlternativeRoom(false)
        .isDinnerAvailableNotPIBA(false)
        .isDinnerAvailablePIBA(false)
        .isLoggedIn(true)
        .isBusinessUser(true)
        .isEmployeeRateSelected(false)
        .dinnerAllowance(10f)
        .basketReference("basketRef123")
        .selectedRoomClass("ST")
        .build()
}

fun mockSummaryInputFreeBreakfastPromo(): SummaryInput {
    return SummaryInput.builder()
        .bookingId("")
        .hotel(mockSelectedHotel())
        .hotelBrand("PI")
        .prepaymentAllowed(false)
        .cityTaxForLeisure(false)
        .cityTaxForBusiness(false)
        .arrivalDateGQ("2025-03-12")
        .departureDateGQ("2025-03-13")
        .specialRequests(listOf(listOf("DBLE")))
        .packageCode(null)
        .packageAmount(null)
        .totalNights(1)
        .menus(listOf(mockParcelableMenuAndAllergyInfo()))
        .allergyInfo(listOf(mockParcelableMenuAndAllergyInfo()))
        .rate(mockSelectedRate())
        .roomBookings(listOf(mockRoomBooking()))
        .accessibleRoomBookings(emptyList())
        .twinRoomBookings(emptyList())
        .upsellItems(listOf(mockNegativeUpsellItem()))
        .ancillaryCloseOutItems(emptyList())
        .extrasItems(listOf(mockExtrasItem()))
        .isAlternativeRoom(false)
        .isDinnerAvailableNotPIBA(false)
        .isDinnerAvailablePIBA(false)
        .isLoggedIn(false)
        .isBusinessUser(false)
        .isEmployeeRateSelected(false)
        .dinnerAllowance(0f)
        .selectedRoomClass("ST")
        .promotionCode("FREECODE").build()
}

fun mockSummaryInputFreeBreakfastPromoWithPositiveUpsell(): SummaryInput {
    return mockSummaryInputFreeBreakfastPromo().toBuilder().upsellItems(listOf(mockUpsellItem()))
        .build()
}

fun mockSummaryInputFreeBreakfastPromoWithDiffPromoCode(): SummaryInput {
    return mockSummaryInputFreeBreakfastPromo().toBuilder().promotionCode("BFFREE").build()
}

fun mockSelectedHotel(): SelectedHotel {
    return SelectedHotel.builder()
        .code("HEAPTI")
        .name("London Heathrow Airport (M4/J4)")
        .imageReference("/content/dam/pi/websites/hotelimages/gb/en/H/HEAPTI/HEAPTI 1.jpg")
        .isHub(false)
        .address("Shepiston Lane, Middlesex, UB3 1RW")
        .accessibilityFacilities(mockAccessibilityFacilities())
        .build()
}

fun mockAccessibilityFacilities(): AccessibilityFacilities {
    return AccessibilityFacilities(hasWetRooms = false, hasLoweredBaths = false)
}

fun mockSelectedRate(): SelectedRate {
    return SelectedRate.builder()
        .code("FLEXRATE")
        .classification("FLEXRATE")
        .rateType("FLEXRATE")
        .rateName("Flex")
        .description("Some Description")
        .bookingRules(null)
        .prepaymentRequired(false)
        .guaranteeRequired(false)
        .cardFeeApplies(false)
        .build()
}

fun mockUpsellItem(): UpsellItem {
    return UpsellItem.builder()
        .code("BFADBF")
        .legend("Premier Inn Breakfast")
        .price(PriceDomain.createWithGBPCurrency(11.99f).toBookingPrice())
        .foodUpsell(true)
        .availableForChildren(true)
        .freeBreakfastOption(true)
        .freeBreakfastTrigger(true)
        .freeBreakfastCode("BFCHDF")
        .shortDescription("Some description")
        .build()
}

fun mockNegativeUpsellItem(): UpsellItem {
    return UpsellItem.builder()
        .code("BFADBF")
        .legend("Premier Inn Breakfast")
        .price(PriceDomain.createWithGBPCurrency(-11.99f).toBookingPrice())
        .foodUpsell(true)
        .availableForChildren(true)
        .freeBreakfastOption(true)
        .freeBreakfastTrigger(true)
        .freeBreakfastCode("BFCHDF")
        .shortDescription("Some description")
        .build()
}

fun mockExtrasItem(): ParcelableExtrasItem {
    return ParcelableExtrasItem(
        id = "HSCKIN",
        name = "Early check-in",
        price = 10.00,
        description = "Need to check in earlier?",
        imageSrc = "",
        currency = "GBP",
        order = 1,
        available = 22
    )
}

fun mockRoomBooking(): RoomBooking {
    return RoomBooking(
        adults = 2,
        children = 0,
        infants = 0,
        cot = false,
        type = "DB",
        lettingCode = "PPLDBL",
        dailyRates = listOf(
            DailyRateInput(
                LocalDate.of(2025, 3, 12),
                PriceDomain.createWithGBPCurrency(60f).toPriceParcelable()
            ),
        ),
        cityTax = PriceDomain.createWithGBPCurrency(0f).toParcelable(),
        roomNumber = 0,
        baseRateAmount = null
    )
}

fun mockParcelableMenuAndAllergyInfo(): ParcelableMenuAndAllergyInfo {
    return ParcelableMenuAndAllergyInfo(
        name = "Some Thing",
        menuOrAllergyInfoSrc = "/zip-breakfast.pdf"
    )
}

fun createTestRoomList_initial(): List<SummaryRoomItem> = listOf(
    SummaryRoomItem(
        roomNumber = "1",
        roomType = "",
        adults = 2,
        children = 0,
        meals = listOf(
            SummaryMealItem(
                id = "BFADBF",
                name = "Premier Inn Breakfast",
                price = SummaryPrice(amount = 11.99F, formattedPrice = "£11.99"),
                description = "Some description",
                kidsEatFree = true,
                counter = 0
            )
        ),
        extras = listOf(
            SummaryExtrasItem(
                id = "HSCKIN",
                name = "Early check-in",
                price = SummaryPrice(amount = 10.00F, formattedPrice = "£10.00"),
                description = "Need to check in earlier?",
                selected = false
            )
        )
    )
)

fun createTestRoomList_extrasToggled(): List<SummaryRoomItem> = listOf(
    SummaryRoomItem(
        roomNumber = "1",
        roomType = "",
        adults = 2,
        children = 0,
        meals = listOf(
            SummaryMealItem(
                id = "BFADBF",
                name = "Premier Inn Breakfast",
                price = SummaryPrice(amount = 11.99F, formattedPrice = "£11.99"),
                description = "Some description",
                kidsEatFree = true,
                counter = 0
            )
        ),
        extras = listOf(
            SummaryExtrasItem(
                id = "HSCKIN",
                name = "Early check-in",
                price = SummaryPrice(amount = 10.00F, formattedPrice = "£10.00"),
                description = "Need to check in earlier?",
                selected = true
            )
        )
    )
)

fun createTestRoomList_mealsInc(): List<SummaryRoomItem> = listOf(
    SummaryRoomItem(
        roomNumber = "1",
        roomType = "",
        adults = 2,
        children = 0,
        meals = listOf(
            SummaryMealItem(
                id = "BFADBF",
                name = "Premier Inn Breakfast",
                price = SummaryPrice(amount = 11.99F, formattedPrice = "£11.99"),
                description = "Some description",
                kidsEatFree = true,
                counter = 1
            )
        ),
        extras = listOf(
            SummaryExtrasItem(
                id = "HSCKIN",
                name = "Early check-in",
                price = SummaryPrice(amount = 10.00F, formattedPrice = "£10.00"),
                description = "Need to check in earlier?",
                selected = false
            )
        )
    )
)

// Extensions
fun Float.addCurrency(): String = "£%.2f".format(this)