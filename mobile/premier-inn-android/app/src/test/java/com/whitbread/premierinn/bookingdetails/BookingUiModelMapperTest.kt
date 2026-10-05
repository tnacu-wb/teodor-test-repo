package com.whitbread.premierinn.bookingdetails

import com.google.common.truth.Truth.assertThat
import com.whitbread.premierinn.bookingdetails.BookingUiModelMapperTest.TestData.BOOKING_READY_TO_CHECKIN
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.ciol.usecase.IsCheckInOnlineEnabledUseCase
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.threeten.bp.LocalDate
import java.util.Locale
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BookingUiModelMapperTest {

    val stringProvider = mock<StringResourceProvider>()
    private val deviceLocaleProviderMock = mock<DeviceLocaleProvider>()
    private val isCheckInOnlineEnabledUseCase = mock<IsCheckInOnlineEnabledUseCase>()

    @Before
    fun setUp() {
        mock<StringResourceProvider> {
            on { stringProvider.getNights(1) } doReturn ""
            on { stringProvider.getRooms(any(), any()) } doReturn ""
            on { stringProvider.getString(any()) } doReturn ""
            on { stringProvider.getString(any(), any()) } doReturn ""
        }

        mock<DeviceLocaleProvider>() {
            on { deviceLocaleProviderMock.getDeviceLocale() } doReturn Locale.UK
        }
    }

    @Test
    fun testTotalCostFormatting() {
        val mapper = BookingUiModelMapper(
            stringProvider,
            deviceLocaleProviderMock,
            isCheckInOnlineEnabledUseCase
        )
        val booking = BOOKING_READY_TO_CHECKIN.copy(totalCost = PriceDomain(132.5f, GBP))
        val bookingUiModelResult = mapper.apply(booking)
        assertThat(bookingUiModelResult.totalCostFormatted).isEqualTo("£132.50")
    }

    @Test
    fun `Given isCheckInOnlineAvailable is false then showCheckInOnline is false`() {
        val mapper = BookingUiModelMapper(
            stringProvider,
            deviceLocaleProviderMock,
            isCheckInOnlineEnabledUseCase
        )
        val booking = BOOKING_READY_TO_CHECKIN.copy(isCheckInOnlineAvailable = false)
        val bookingUiModelResult = mapper.apply(booking)
        assertThat(bookingUiModelResult.isCheckInOnlineEnabled).isEqualTo(false)
    }

    @Test
    fun `GIVEN Booking Details was accessed, WHEN CIOL feature flag is set to false, THEN isCheckInOnlineFlagEnabled will be false`() {
        val mapper = BookingUiModelMapper(
            stringProvider,
            deviceLocaleProviderMock,
            isCheckInOnlineEnabledUseCase
        )
        mock<IsCheckInOnlineEnabledUseCase> {
            on { isCheckInOnlineEnabledUseCase() } doReturn false
        }

        val bookingUiModelResult = mapper.apply(BOOKING_READY_TO_CHECKIN)

        assertFalse { bookingUiModelResult.isCheckInOnlineFlagEnabled }
    }

    @Test
    fun `GIVEN Booking Details was accessed, WHEN CIOL feature flag is set to true, THEN isCheckInOnlineEnabled will be true`() {
        val mapper = BookingUiModelMapper(
            stringProvider,
            deviceLocaleProviderMock,
            isCheckInOnlineEnabledUseCase
        )
        mock<IsCheckInOnlineEnabledUseCase> {
            on { isCheckInOnlineEnabledUseCase() } doReturn true
        }

        val bookingUiModelResult = mapper.apply(BOOKING_READY_TO_CHECKIN)

        assertTrue { bookingUiModelResult.isCheckInOnlineEnabled }
    }

    object TestData {

        val BOOKING_READY_TO_CHECKIN = Booking(
                bookingReference = "BBGR103024",
                leadGuestFullName = "Mr Christos Nopeppas",
                leadGuestSurname = "Nopeppas",
                arrivalDate = LocalDate.of(2018, 6, 6),
                departureDate = LocalDate.of(2018, 6, 7),
                hotelName = "Alpha Edinburgh City Centre",
                hotelCode = "EDIPRI",
                numberOfRooms = 1,
                prepaidAmount = PriceDomain(145f, GBP),
                totalCost = PriceDomain(145f, GBP),
                balanceOutstanding = null,
                rateType = "FLEX",
                isCheckInOnlineAvailable = true,
                amendRestrictions = AmendRestrictions.createWithDefaults()
                )
    }
}