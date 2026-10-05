package com.whitbread.premierinn.data.reservation

import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl
import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.booking.dao.BookingDao
import com.whitbread.premierinn.data.booking.entity.PriceEntity
import com.whitbread.premierinn.data.common.DatabaseTransactionRunner
import com.whitbread.premierinn.data.common.ErrorLogger
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl
import com.whitbread.premierinn.data.remote.AmendReservationApiResponse
import com.whitbread.premierinn.data.remote.ApiError
import com.whitbread.premierinn.data.remote.ApiThrowable
import com.whitbread.premierinn.data.remote.ReservationApi
import com.whitbread.premierinn.data.remote.ReservationApiContract
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PackagesGraphQLContract
import com.whitbread.premierinn.data.reservation.AmendedReservationRepositoryImplTest.TestData.RESERVATION_RESPONSE
import com.whitbread.premierinn.data.reservation.AmendedReservationRepositoryImplTest.TestData.UPDATED_AMEND_RESERVATION_RESPONSE
import com.whitbread.premierinn.data.reservation.dao.AmendedReservationDao
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationWithDetails
import com.whitbread.premierinn.data.reservation.entity.BaseReservationEntity
import com.whitbread.premierinn.data.roombreakdown.RoomBreakdownDao
import com.whitbread.premierinn.data.roombreakdown.RoomBreakdownEntity
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaDao
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaEntity
import com.whitbread.premierinn.data.roomguest.RoomGuestDao
import com.whitbread.premierinn.data.roomguest.RoomGuestEntity
import com.whitbread.premierinn.data.roomupsell.RoomUpsellDao
import com.whitbread.premierinn.data.roomupsell.RoomUpsellEntity
import com.whitbread.premierinn.data.upsellavailable.UpsellItemAvailableDao
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.data.utils.TestTransactionRunner
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.BOOKING_CHANNEL_MOBILE
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.PaymentDetails
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomBreakdown
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.countries.repository.CountriesRepository
import com.whitbread.premierinn.domain.graphql.amend.repository.GraphQLAmendRepository
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.Locale

class AmendedReservationRepositoryImplTest {
    lateinit var repository: AmendedReservationRepository
    lateinit var reservationMock: Reservation
    lateinit var paymentDetailsMock: PaymentDetails
    lateinit var amendedReservationWithDetailsMock: AmendedReservationWithDetails

    private val simplePersistenceManagerImpl: SimplePersistenceManagerImpl = mockk()
    private val businessPersistenceManagerImpl: BusinessPersistenceManagerImpl = mockk()
    private val amendedReservationDao: AmendedReservationDao = mockk()
    private val roomCriteriaDao: RoomCriteriaDao = mockk()
    private val upsellItemAvailableDao: UpsellItemAvailableDao = mockk()
    private val roomGuestDao: RoomGuestDao = mockk()
    private val upsellDao: RoomUpsellDao = mockk()
    private val bookingDao: BookingDao = mockk()
    private val roomBreakdownDao: RoomBreakdownDao = mockk()
    private val reservationApi: ReservationApi = mockk()
    private val transaction: DatabaseTransactionRunner = TestTransactionRunner
    private val errorLogger: ErrorLogger = mockk()
    private val deviceLocaleProvider:DeviceLocaleProvider = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val jsonVariableForBookingConf: JSONObject = mockk(relaxed = true)
    private val jsonVariableForBookingConfAndAmendSummary: JSONObject = mockk(relaxed = true)
    private val fileDataProvider: FileDataProvider = mockk()
    private val graphQlApi: WBGraphQLServicesApi = mockk()
    private val graphQLAmendRepository: GraphQLAmendRepository = mockk()
    private val bookingRepository: BookingRepository = mockk()
    private val countriesRepository: CountriesRepository = mockk()

    val BOOKING_CONFIRMATION_AND_MANAGE_BOOKING_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/booking_confirmation_manage_booking_success_gql.json"),
        BookingConfirmationGraphQLContract.BookingConfirmationData::class.java)
    private val PACKAGES_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/packages_success_gql.json"),
        PackagesGraphQLContract.PackagesData::class.java)
    val BOOKING_INFO_QUERY_STRING = "Booking info query string"

    @Before
    fun setUp() {
        every { deviceLocaleProvider.getDeviceLocale() } returns Locale.UK
        every { deviceLocaleProvider.getDeviceLanguage() } returns LANGUAGE_ENGLISH
        every { deviceLocaleProvider.getBookingChannel() } returns BOOKING_CHANNEL_MOBILE
        every { deviceLocaleProvider.getCountryIfRegion(any()) } returns "gb"

        repository = AmendedReservationRepositoryImpl(
            simplePersistenceManagerImpl, businessPersistenceManagerImpl, amendedReservationDao,
            roomCriteriaDao, upsellItemAvailableDao, roomGuestDao, upsellDao, bookingDao,
            roomBreakdownDao, transaction, reservationApi, deviceLocaleProvider, errorLogger,
            graphQLAmendRepository, fileDataProvider, jsonObject, jsonVariableForBookingConf,
            jsonVariableForBookingConfAndAmendSummary, graphQlApi, bookingRepository, countriesRepository)

        amendedReservationWithDetailsMock = AmendedReservationWithDetails(reservationEntity =
        AmendedReservationEntity(reservation =
        BaseReservationEntity(bookingReference = "BBER418952",
                arrivalDate = LocalDate.now(),
                departureDate = LocalDate.now(),
                hotelCode = "LONBLA",
                cancelable = true),
                basketReference = "WVP6LliPX5jZHBfg"), roomCriteria = listOf(
                RoomCriteriaEntity(id = 381,
                        roomId = "DBS, 1",
                        roomType = RoomType.DOUBLE,
                        numberOfAdults = 1,
                        numberOfChildren = 0,
                        cot = false,
                        reservationReference = null,
                        amendedReservationReference = "BBER418952")), roomGuests = listOf(
                RoomGuestEntity(id = 3, roomId = "DBS, 1", title = "Ms", firstName = "Audeat",
                        lastName = "Luci", guestHistoryNumber = "G49182609", reservationReference = null,
                        amendedReservationReference = "BBER418952")), roomUpsells = listOf(
                RoomUpsellEntity(id = 7, roomId = "DBS, 1",
                        legend = "Premier Inn Breakfast", code = "11", category = "F", quantity = 1,
                        postingDate = LocalDate.now(), unitCost = PriceEntity(amount = 10.5f,
                        currency = "GBP"), reservationReference = null, amendedReservationReference = "BBER418952"),
                RoomUpsellEntity(id = 8, roomId = "DBS, 1", legend = "Premier Inn Breakfast",
                        code = "11", category = "F", quantity = 1, postingDate = LocalDate.now(),
                        unitCost = PriceEntity(amount = 10.5f, currency = "GBP"), reservationReference = null, amendedReservationReference = "BBER418952"),
                RoomUpsellEntity(id = 9, roomId = "DBS, 1", legend = "Premier Inn Breakfast",
                        code = "11", category = "F", quantity = 1, postingDate = LocalDate.now(),
                        unitCost = PriceEntity(amount = 10.5f, currency = "GBP"),
                        reservationReference = null, amendedReservationReference = "BBER418952")),
                roomBreakdown = listOf(RoomBreakdownEntity(id = 666, roomId = "DBS, 1", unitCost = PriceEntity(amount = 100.00f, currency = GBP))))


        reservationMock = Reservation("BBER418952",
                LocalDate.now(), LocalDate.now(), "LONBLA",
                listOf(RoomCriteria(1, 0, 0, false, RoomType.DOUBLE, 1)),
                listOf(Guest(1, "", "Lady", "Audeat", "Luci", "G49182609", Address("null", null,
                        null, null, null, "N1 7BD", null, null), "audeatluci@test.com",
                        "07411375090")),
                emptyList(),
                false,
                emptyList())

        paymentDetailsMock = PaymentDetails(
                billingAddress = Address(line1 = "120 Holborn"),
                cardType = "VI",
                cardNumber = "************1111",
                cardSecurityCode = "123",
                holdersFullName = "Nil",
                expiryDate = "12/22",
                issueNumber = "",
                startDate = "",
                prepaymentRequired = true,
                useExistingCard = true)

        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns BOOKING_INFO_QUERY_STRING
        every { jsonObject.toString() } returns BOOKING_INFO_QUERY_STRING
        every { deviceLocaleProvider.getDeviceLocale() } returns Locale.UK
        every { graphQlApi.bookingConfirmationGraphQL(any(), any()) } returns Single.just(BOOKING_CONFIRMATION_AND_MANAGE_BOOKING_SUCCESS)
        every { graphQlApi.packagesGraphQL(any()) } returns Single.just(PACKAGES_SUCCESS)
    }

    @Test
    fun `when booking dates are reduced then upsells are reduced`() {
        val amendedDates = Pair(LocalDate.of(2020, 8, 20), LocalDate.of(2020, 9, 22))

        every { simplePersistenceManagerImpl.getReservation() } returns fakeOriginalReservation(
            LocalDate.of(2020, 8, 15), LocalDate.of(2020, 8, 18)
        )

        repository.updateReservationDatesAndUpsells(
            reservationId = "THISISNIL",
            dates = amendedDates,
            updatedReservation = reservationMock
        ).test()

        verify { upsellDao.removeRoomUpsell("THISISNIL", "DBS,1") }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 21))) }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 22))) }
    }

    @Test
    fun `when booking dates are increased beyond original booking then upsells are not increased`() {
        val amendedDates = Pair(LocalDate.of(2020, 8, 20), LocalDate.of(2020, 9, 24))

        every { simplePersistenceManagerImpl.getReservation() } returns fakeOriginalReservation(
            LocalDate.of(2020, 8, 15), LocalDate.of(2020, 8, 18)
        )

        repository.updateReservationDatesAndUpsells(
            reservationId = "THISISNIL",
            dates = amendedDates,
            updatedReservation = reservationMock
        ).test()

        verify { upsellDao.removeRoomUpsell("THISISNIL", "DBS,1") }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 21))) }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 22))) }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 23))) }
    }

    @Test
    fun `when booking date range is same then upsells remain unchanged`() {
        val amendedDates = Pair(LocalDate.of(2020, 8, 20), LocalDate.of(2020, 9, 23))

        every { simplePersistenceManagerImpl.getReservation() } returns fakeOriginalReservation(
            LocalDate.of(2020, 8, 15), LocalDate.of(2020, 8, 18)
        )

        repository.updateReservationDatesAndUpsells(
            reservationId = "THISISNIL",
            dates = amendedDates,
            updatedReservation = reservationMock
        ).test()

        verify { upsellDao.removeRoomUpsell("THISISNIL", "DBS,1") }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 21))) }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 22))) }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 23))) }
    }

    @Test
    fun `when original upsells are less than original and amended booking dates then upsells remain unchanged`() {
        val amendedDates = Pair(LocalDate.of(2020, 8, 20), LocalDate.of(2020, 9, 24))

        every { simplePersistenceManagerImpl.getReservation() } returns fakeOriginalReservation(
            LocalDate.of(2020, 8, 15), LocalDate.of(2020, 8, 20)
        )

        repository.updateReservationDatesAndUpsells(
            reservationId = "THISISNIL",
            dates = amendedDates,
            updatedReservation = reservationMock
        ).test()

        verify { upsellDao.removeRoomUpsell("THISISNIL", "DBS,1") }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 21))) }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 22))) }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 23))) }
    }

    @Test
    fun `when original reservation is null then there is no db interaction`() {
        val amendedDates = Pair(LocalDate.of(2020, 8, 20), LocalDate.of(2020, 9, 24))

        every { simplePersistenceManagerImpl.getReservation() } returns null

        repository.updateReservationDatesAndUpsells(
            reservationId = "THISISNIL",
            dates = amendedDates,
            updatedReservation = reservationMock
        ).test()

        verify(exactly = 0) { upsellDao.removeRoomUpsell("THISISNIL", "DBS,1") }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 21))) }
    }

    @Test
    fun `when original reservation upsell list is empty then there is no db interaction`() {
        val amendedDates = Pair(LocalDate.of(2020, 8, 20), LocalDate.of(2020, 9, 24))

        every { simplePersistenceManagerImpl.getReservation() } returns fakeOriginalReservation(
            LocalDate.of(2020, 8, 15), LocalDate.of(2020, 8, 20)
        ).copy(upsells = emptyList())

        repository.updateReservationDatesAndUpsells(
            reservationId = "THISISNIL",
            dates = amendedDates,
            updatedReservation = reservationMock
        ).test()

        verify(exactly = 0) { upsellDao.removeRoomUpsell("THISISNIL", "DBS,1") }
        verify(exactly = 0) { upsellDao.insert(fakeRoomUpsellEntity(LocalDate.of(2020, 8, 21))) }
    }

    @Test
    fun `when there is no error in db queries then Completable completes with no error`() {
        val amendedDates = Pair(LocalDate.of(2020, 8, 20),
                LocalDate.of(2020, 9, 22))

        every { simplePersistenceManagerImpl.getReservation() } returns fakeOriginalReservation(
                LocalDate.of(2020, 8, 15), LocalDate.of(2020, 8, 18))
        every { upsellDao.removeRoomUpsell(any(), any()) } returns Unit
        every { upsellDao.insert(any()) } returns 1L
        every { amendedReservationDao.updateDates(any(), any(), any()) } returns 1
        every { amendedReservationDao.getAmendedReservationWithRelations("THISISNIL") } returns Single.just(amendedReservationWithDetailsMock)
        every { amendedReservationDao.insert(any()) } returns 1
        every { roomGuestDao.insert(any()) } returns 1
        every { roomCriteriaDao.insert(any()) } returns 1

        repository.updateReservationDatesAndUpsells(reservationId = "THISISNIL", dates = amendedDates, updatedReservation = reservationMock).test()
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    fun `should update booking in db when update booking is called`() {
        val reservationId = "AUR123"
        val arrival = LocalDate.of(2020, 8, 30)
        val departure = LocalDate.of(2020, 9, 3)
        val surname  = "samdan"

        repository.updateBooking(reservationId = reservationId, arrival = arrival, departure = departure, leadGuestSurname = surname).test()

        verify { bookingDao.updateBooking(reservationId, arrival, departure, surname) }
    }

    //TODO: Fix this test
//    @Test
//    fun `should insert reservation to db on trigger success`() {
//        every { reservationApi.getReservation(any(), any(), any(), any(), any(), any()) } returns Single.just(RESERVATION_RESPONSE)
//        every { simplePersistenceManagerImpl.storeAmendReservationID(any()) } just Runs
//
//        repository.trigger(reservationReference = "BKF123", surname = "Smith", arrivalDate = LocalDate.now()).test()
//
//        verify {
//            amendedReservationDao.insert(any())
////            roomCriteriaDao.insert(any())
////            roomGuestDao.insert(any())
//        }
//
//    }

// TODO: Work on this test

//    @Test
//    fun `should insert reservation to db for opera on trigger success`() {
//        every { simplePersistenceManagerImpl.storeAmendReservationID(any()) } just Runs
//
//        repository.bookingConfirmationAndSaveBookingDetailsinDbAndSP(
//            tempBasketReference = "AJK-5479a5b0-bca9-4097-8ca1-358b3504ef16",
//            bookingRef = "AWM7812236",
//            country = "gb", language = "en",
//            hotelName = EMPTY_STRING_DOMAIN,
//            CancelInformationRequestBody(
//                basketReference = "AWM7812236",
//                hotelId = "BIRTOB",
//                userDateTime = "2023-09-22T16:13:11.017+01:00",
//                token = "tF3Fkb0VG5nY1veTo4rrtByxeRtuZypKWMgSefcwbt4LXDphr5ow70bqWRQ",
//                bookingChannel = BookingChannelDetails(
//                    channel = "PI",
//                    language = "en",
//                    subchannel = "MOBILE"
//                )
//            ), "2023-10-05", "2023-10-06", false).test()
//
//        verify {
//            amendedReservationDao.insert(any())
////            roomCriteriaDao.insert(any())
////            roomGuestDao.insert(any())
//        }
//    }

    @Test
    fun `should submit reservation on submit success`() {
        every { reservationApi.getReservation(any(), any(), any(), any(), any(), any()) } returns Single.just(RESERVATION_RESPONSE)
        every { simplePersistenceManagerImpl.getAmendReservationID() } returns "WVP6LliPX5jZHBfg"
        every { simplePersistenceManagerImpl.storePendingAmendId(any()) } just Runs

        every { reservationApi.updateAmendReservation(any(), any(), any(), any(), any(), any(), any(),any()) } returns
                Single.just(UPDATED_AMEND_RESERVATION_RESPONSE)

        val testSubmit =
            reservationApi.updateAmendReservation(reservationMock.toAmendRequest(
                    listOf(), paymentDetailsMock, "123", "en"),
                "BBER270436", LocalDate.now(), sessionId = "WVP6LliPX5jZHBfg",
                bookingChannel = BOOKING_CHANNEL_MOBILE, country = "gb", language = "en").test()

        testSubmit.assertNoErrors()
    }

    @Test
    fun `sessionID failures do not fail on submit`() {
        every { reservationApi.getReservation(any(), any(), any(), any(), any(), any()) } returns Single.just(RESERVATION_RESPONSE)
        every { simplePersistenceManagerImpl.storeAmendReservationID(any()) } just Runs
        every { simplePersistenceManagerImpl.getAmendReservationID() } returns
                "WVP6LliPX5jZHBfgHAHAHA"
        every { simplePersistenceManagerImpl.storePendingAmendId(any()) } just Runs

        every { reservationApi.updateAmendReservation(any(), any(), any(), any(), any(), any(), any(), any()) } returns
                Single.error(ApiThrowable.Http(httpCode = 400, apiErrorBody = ApiError(100,
                        listOf("SessionID error"))))

        val testSubmitError =
            reservationApi.updateAmendReservation(reservationMock.toAmendRequest(
                    listOf(), paymentDetailsMock, "123", deviceLocaleProvider.getDeviceLocale().language),
                "BBER270436", LocalDate.now(), sessionId = "WVP6LliPX5jZHBfg", bookingChannel = BOOKING_CHANNEL_MOBILE, country = "gb", language = "en").test()

        testSubmitError.assertError(ApiThrowable.Http(httpCode = 400, apiErrorBody = ApiError(100,
                listOf("SessionID error"))))

        every { reservationApi.getReservation(any(), any(), any(), any(), any(), any()) } returns Single.just(RESERVATION_RESPONSE)
        every { simplePersistenceManagerImpl.storeAmendReservationID(any()) } just Runs
        every { simplePersistenceManagerImpl.getAmendReservationID() } returns
                "WVP6LliPX5jZHBfg"
        every { reservationApi.updateAmendReservation(any(), any(), any(), any(), any(), any(), any(), any()) } returns
                Single.just(UPDATED_AMEND_RESERVATION_RESPONSE)

        val testSubmit = repository.submit(reservationId = "BBER270436",
                arrivalDate = LocalDate.now(),
                surname = "Luci",
                updatedReservation = reservationMock,
                paymentDetails = paymentDetailsMock,
                cardSecurityCode = "123",
                availableUpsells = listOf()).test()

        testSubmit.assertNoErrors()
    }

    @Test
    fun `should throw error exception on submit`() {
        every { reservationApi.getReservation(any(), any(), any(), any(), any(), any()) } returns Single.just(RESERVATION_RESPONSE)
        every { simplePersistenceManagerImpl.getAmendReservationID() } returns "WVP6LliPX5jZHBfg"

        every { reservationApi.updateAmendReservation(any(), any(), any(), any(), any(), any(), any(), any()) } returns
                Single.error(ApiThrowable.Http(httpCode = 500, apiErrorBody = ApiError(500, listOf
                ("Error"))))

        val testSubmit = reservationApi.updateAmendReservation(
            reservationMock.toAmendRequest(listOf(), paymentDetailsMock, "123", deviceLocaleProvider.getDeviceLocale().language),
            "BBER270436", LocalDate.now(), sessionId = "WVP6LliPX5jZHBfg", bookingChannel = BOOKING_CHANNEL_MOBILE, country = "gb", language = "en").test()

        testSubmit.assertError(ApiThrowable.Http(httpCode = 500, apiErrorBody = ApiError(500,
                listOf("Error"))))
    }

    @Test
    fun `should update reservation to db on updateReservation success`() {
        every { amendedReservationDao.getAmendedReservationWithRelations(any()) } returns Single.just(amendedReservationWithDetailsMock)
        every { amendedReservationDao.updateDates(any(), any(), any()) } returns 1
        every { amendedReservationDao.insert(any()) } returns 200L
        every { roomGuestDao.insert(any()) } returns 200L
        every { roomCriteriaDao.insert(any()) } returns 200L

        repository.updateReservation(reservationId = "BKF123", updatedReservation = reservationMock).test()

        verify {
            amendedReservationDao.insert(any())
            roomGuestDao.insert(any())
            roomCriteriaDao.insert(any())
        }
    }

    object TestData {
        val RESERVATION_RESPONSE = GsonFactory.create().fromJson(FileUtils.loadFileFromResource("api/ms-responses/reservation/hotels/reservation_ADCR407745_success.json"),
                ReservationApiContract.ReservationResponse::class.java)
        val UPDATED_AMEND_RESERVATION_RESPONSE = GsonFactory.create().fromJson(FileUtils
                .loadFileFromResource("api/ms-responses/reservation/hotels/update_amended_reservation.json"),
                AmendReservationApiResponse.AmendReservationResponse::class.java)
    }

    private companion object {
        fun fakeOriginalReservation(arrival: LocalDate, departure: LocalDate) = Reservation(
                bookingReference = "THISISNIL",
                arrival = arrival,
                departure = departure,
                hotelCode = "BLABLA",
                roomsCriteria = listOf(RoomCriteria(
                        numberOfAdults = 1,
                        numberOfChildren = 0,
                        numberOfInfants = 0,
                        includeCot = false,
                        roomType = RoomType.DOUBLE,
                        roomNumber = 1,
                        roomId = "DBS,1")),
                roomsLeadGuest = listOf(Guest(
                        roomNumber = 1,
                        roomId = "DBS,1",
                        title = "Mr",
                        firstName = "Niladree",
                        lastName = "Bhattacharjee",
                        guestHistoryNumber = null,
                        address = null,
                        emailAddress = "nbhattacharjee@and.digital",
                        phoneNumber = null)),
                cancelable = true,
                upsells = listOf(
                        Upsell(quantity = 1, category = Upsell.Category.BREAKFAST, legend = "Premier Inn Breakfast", postingDate = LocalDate.of(2020, 8, 16), unitCost = PriceDomain(24.99f, "GBP"), code = "6", roomId = "DBS,1"),
                        Upsell(quantity = 1, category = Upsell.Category.BREAKFAST, legend = "Premier Inn Breakfast", postingDate = LocalDate.of(2020, 8, 17), unitCost = PriceDomain(24.99f, "GBP"), code = "6", roomId = "DBS,1"),
                        Upsell(quantity = 1, category = Upsell.Category.BREAKFAST, legend = "Premier Inn Breakfast", postingDate = LocalDate.of(2020, 8, 18), unitCost = PriceDomain(24.99f, "GBP"), code = "6", roomId = "DBS,1")),
                roomsBreakdown = listOf(RoomBreakdown(roomId = "DBS,1", totalRoomCost = PriceDomain(amount = 100.00f, currency = GBP))))

        fun fakeRoomUpsellEntity(postingDate: LocalDate) = RoomUpsellEntity(roomId = "DBS,1", legend = "Premier Inn Breakfast", code = "6", category = "F",
                quantity = 1, postingDate = postingDate, unitCost = PriceEntity(amount = 24.99f, currency = "GBP"), amendedReservationReference = "THISISNIL")
    }
}