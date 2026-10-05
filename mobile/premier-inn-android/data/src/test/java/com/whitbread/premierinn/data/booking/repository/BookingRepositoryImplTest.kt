@file:Suppress("HasPlatformType")

package com.whitbread.premierinn.data.booking.repository

import com.whitbread.premierinn.businessbooker.data.remote.BusinessAccountApi
import com.whitbread.premierinn.data.booking.dao.BookingDao
import com.whitbread.premierinn.data.booking.mapToBookingEntity
import com.whitbread.premierinn.data.booking.repository.BookingRepositoryImplTest.TestData.EXPECTED_BOOKING_1
import com.whitbread.premierinn.data.booking.repository.BookingRepositoryImplTest.TestData.EXPECTED_BOOKING_2
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.remote.AccountApiContract
import com.whitbread.premierinn.data.remote.ApiThrowable
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.BOOKING_CHANNEL_MOBILE
import com.whitbread.premierinn.domain.common.PriceDomain
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single.just
import junitparams.JUnitParamsRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.threeten.bp.LocalDate
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.util.Locale

@RunWith(JUnitParamsRunner::class)
class BookingRepositoryImplTest {

    private val businessApi = mock<BusinessAccountApi>()
    private val bookingDao = mock<BookingDao>()
    private val simplePersistenceManager = mock<SimplePersistenceManager>()
    private val deviceLocaleProvider: DeviceLocaleProvider = mockk()
    private val logger: ErrorLogger = mockk()

    private lateinit var repo: BookingRepositoryImpl

    @Before
    fun setUp() {
        every { deviceLocaleProvider.getDeviceLocale() } returns Locale.UK
        every { deviceLocaleProvider.getCountryIfRegion(any()) } returns "gb"
        every { deviceLocaleProvider.getBookingChannel() } returns BOOKING_CHANNEL_MOBILE

        repo = BookingRepositoryImpl(
            businessApi,
            bookingDao,
            simplePersistenceManager,
            logger
        )
    }


//    @Test
//    fun `Given a LoggedIn Business User When Calling Stays Then Returns an Expected List of Bookings`() {
//        whenever(businessApi.stays(any(), any(), any(), any(), any(), any())).thenReturn(
//            just(
//                BUSINESS_STAYS
//            )
//        )
//        whenever(businessPersistenceManager.retrieveBusinessSessionId()).thenReturn("BAC12515")
//
//        repo.fetchLinkedAccountBookingsForInnBusiness("user", null, "10", "1").test()
//            .assertValues(listOf(EXPECTED_BUSINESS_BOOKING_1.copy(isBusinessBooking = true)))
//            .assertNoErrors()
//            .assertComplete()
//
//        inOrder(api, businessApi) {
//            verify(businessApi).stays(any(), any(), any(), any(), any(), any())
//            verifyNoMoreInteractions(api, api2, bookingDao)
//        }
//    }

    @Test
    fun `Given a stale booking exists, AND booking is not linked to an account, then it is returned`() {
        whenever(bookingDao.getStaleBookings(any())).thenReturn(
            just(
                listOf(
                    EXPECTED_BOOKING_1.mapToBookingEntity().copy(isLinkedToAccount = false)
                )
            )
        )

        repo.getLocalStaleBookings(0)
            .test()
            .assertValue(EXPECTED_BOOKING_1.copy(isLinkedToAccount = false))
    }

    @Test
    fun `Given a stale booking exists, AND booking IS linked to an account, then it is not returned`() {
        whenever(bookingDao.getStaleBookings(any())).thenReturn(
            just(
                listOf(
                    EXPECTED_BOOKING_1.mapToBookingEntity().copy(isLinkedToAccount = true)
                )
            )
        )

        repo.getLocalStaleBookings(0)
            .test()
            .assertNoValues()
    }

//    @Test
//    @Parameters(method = "emptyStaysOrMissing")
//    fun `Given a LoggedIn Business User When Calling Stays Then No List of Bookings`(emptyResponse: AccountApiContract.StaysResponse) {
//        whenever(businessApi.stays(any(), any(), any(), any(), any(), any())).thenReturn(
//            just(
//                emptyResponse
//            )
//        )
//        whenever(businessPersistenceManager.retrieveBusinessSessionId()).thenReturn("BAC12515")
//
//        repo.fetchLinkedAccountBookingsForInnBusiness("business@whitbread.com", null, "10", "1")
//            .test()
//            .assertValues(emptyList())
//            .assertNoErrors()
//            .assertComplete()
//
//        verify(businessApi).stays(any(), any(), any(), any(), any(), any())
//        verifyNoMoreInteractions(api, businessApi, bookingDao)
//    }
//
//    @Test
//    @Parameters(method = "apiUnAuthorizedErrors")
//    fun `Given a LoggedIn Business User When Calling Stays And any Http ApiThrowable occurs Then that Error is Returned`(
//        apiError: ApiThrowable
//    ) {
//        whenever(
//            businessApi.stays(
//                any(),
//                any(),
//                any(),
//                any(),
//                any(),
//                any()
//            )
//        ).thenReturn(Single.error(apiError))
//        whenever(businessPersistenceManager.retrieveBusinessSessionId()).thenReturn("BAC12515")
//
//        repo.fetchLinkedAccountBookingsForInnBusiness("business@whitbread.com", null, "10", "1")
//            .test()
//            .assertError { it == UnAuthorizedCustomerError }
//            .assertNotComplete()
//
//        verify(businessApi).stays(any(), any(), any(), any(), any(), any())
//        verifyNoMoreInteractions(businessApi, bookingDao)
//    }

//    @Test
//    @Parameters(method = "apiAnyNonAuthorizedErrors")
//    fun `Given a LoggedIn Business User When Calling Stays And Any other error other than occurs Then UnAuthorizedCustomerError that Error is Returned`(
//        apiError: ApiThrowable
//    ) {
//        whenever(
//            businessApi.stays(
//                any(),
//                any(),
//                any(),
//                any(),
//                any(),
//                any()
//            )
//        ).thenReturn(Single.error(apiError))
//        whenever(businessPersistenceManager.retrieveBusinessSessionId()).thenReturn("BAC12515")
//
//
//        repo.fetchLinkedAccountBookingsForInnBusiness("business@whitbread.com", null, "10", "1")
//            .test()
//            .assertError { it == apiError }
//            .assertNotComplete()
//
//        verify(businessApi).stays(any(), any(), any(), any(), any(), any())
//        verifyNoMoreInteractions(businessApi, bookingDao)
//    }

    @Test
    fun `Given a Booking List When Store is executed Then Success`() {
        repo.store(listOf(EXPECTED_BOOKING_1, EXPECTED_BOOKING_2)).test()
            .assertComplete()
            .assertNoErrors()

        verify(bookingDao).insertOrReplace(EXPECTED_BOOKING_1.mapToBookingEntity())
        verify(bookingDao).insertOrReplace(EXPECTED_BOOKING_2.mapToBookingEntity())
    }

    @Test
    fun `Given no Bookings in Repo When IsEmpty Then Returns True`() {
        whenever(bookingDao.count()).thenReturn(0)

        repo.isEmpty().test()
            .assertValue(TRUE)
            .assertNoErrors()
    }

    @Test
    fun `Given no Bookings in Repo When IsEmpty Then Returns False`() {
        whenever(bookingDao.count()).thenReturn(3)

        repo.isEmpty().test()
            .assertValue(FALSE)
            .assertNoErrors()
    }

    object TestData {

        val EXPECTED_BOOKING_1 = Booking(
            bookingReference = "BBGR103024",
            leadGuestFullName = "Mr Christos Nopeppas",
            arrivalDate = LocalDate.of(2018, 7, 6),
            departureDate = LocalDate.of(2018, 7, 7),
            hotelName = "Alpha Edinburgh City Centre (Princes Street)",
            hotelCode = "EDIPRI",
            numberOfRooms = 1,
            totalCost = PriceDomain(145f, "GBP"),
            balanceOutstanding = null,
            rateType = "A",
            isLinkedToAccount = true,
            leadGuestSurname = "Nopeppas",
            amendRestrictions = AmendRestrictions(
                nights = false,
                rooms = false,
                guestNames = false,
                upsell = false,
                restricted = false
            )
        )

        val EXPECTED_BOOKING_2 = Booking(
            bookingReference = "AYHR128567",
            leadGuestFullName = "Mr John Smith",
            arrivalDate = LocalDate.of(2018, 6, 15),
            departureDate = LocalDate.of(2018, 6, 16),
            hotelName = "Alpha London Kensington (Olympia)",
            hotelCode = "LONOLY",
            numberOfRooms = 1,
            isCancelled = true,
            totalCost = PriceDomain(90f, "GBP"),
            balanceOutstanding = null,
            rateType = "A",
            isLinkedToAccount = true,
            leadGuestSurname = "Smith",
            amendRestrictions = AmendRestrictions(
                nights = false,
                rooms = false,
                guestNames = false,
                upsell = false,
                restricted = false
            )
        )
    }
}