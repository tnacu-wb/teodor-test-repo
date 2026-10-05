package com.whitbread.premierinn.amend

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.AmendSummarySuccessEvent
import com.whitbread.premierinn.amend.AmendReservationViewModel.AmendReservationEvent.ShowCancelBookingDialog
import com.whitbread.premierinn.amend.analytics.AmendBookingData
import com.whitbread.premierinn.api.response.InstanceFactory
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.AsyncResult.Success
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.model.PackagesAndAncillaryCloseoutDomainFixture.createDataPackagesDomain
import com.whitbread.premierinn.common.model.PackagesAndAncillaryCloseoutDomainFixture.createPackagesPackagesDomain
import com.whitbread.premierinn.common.model.RoomBreakdownFixture
import com.whitbread.premierinn.common.model.UpsellAvailableFixture
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ARRIVAL_DATE
import com.whitbread.premierinn.data.common.DEFAULT_MAX_NIGHTS_LEISURE
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.graphql.mapper.mapToAmendSummaryGQL
import com.whitbread.premierinn.data.remote.graphql.contracts.AmendSummaryGraphQLContract
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.BOOKING_CHANNEL_MOBILE
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.UpsellAvailable
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.common.hoteldetails.entity.GalleryImageDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDetailsUseCase
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.PackagesAndAncillaryCloseoutDomain
import com.whitbread.premierinn.domain.graphql.amend.entity.TempBookingRefDomain
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.CancelReservationDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.usecase.ClearReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveAmendedReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveBookingUseCase
import com.whitbread.premierinn.domain.reservation.usecase.OriginalReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.RemoveRoomUseCase
import com.whitbread.premierinn.domain.reservation.usecase.RemoveStoredRoomUseCase
import com.whitbread.premierinn.domain.reservation.usecase.UpsellsAvailableUseCase
import com.whitbread.premierinn.utils.RxJavaTestRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.Locale

private val ROOM_BREAKDOWN = RoomBreakdownFixture.aRoomBreakdown()
private val AVAILABLE_UPSELL = UpsellAvailableFixture.aUpsellAvailable()

class AmendReservationViewModelTest {

    @get:Rule
    val rxRule = RxJavaTestRule()

    private val publishBookingDetails = SingleSubject.create<Booking>()
    private val publishHotelInformationDomain = SingleSubject.create<HotelInformationDomain>()
    private val publishPackagesAndAncillaryCloseoutDomain = SingleSubject.create<PackagesAndAncillaryCloseoutDomain>()
    private val publishUpsellAvailable = SingleSubject.create<List<UpsellAvailable>>()
    private val publishReservationUpdates = PublishSubject.create<Reservation>()
    private val roomId = "DBS,2"
    private val TEMP_BASKET_REF = "TEGJDGJU938824"

    private val inputLeisure: ManageBookingInput = ManageBookingInput.builder()
        .hotelCode("LONMON")
        .surname("SomeName")
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now().plusDays(2))
        .bookingReference("BKF1234")
        .amendable(true)
        .cancellable(true)
        .isEmployeeBooking(false)
        .isBusinessBooking(false)
        .dinnerAllowance(0f)
        .build()

    private val inputBusiness: ManageBookingInput = ManageBookingInput.builder()
        .hotelCode(inputLeisure.hotelCode())
        .surname(inputLeisure.surname())
        .arrivalDate(inputLeisure.arrivalDate())
        .departureDate(inputLeisure.departureDate())
        .bookingReference(inputLeisure.bookingReference())
        .amendable(inputLeisure.amendable())
        .cancellable(inputLeisure.cancellable())
        .isEmployeeBooking(inputLeisure.isEmployeeBooking())
        .isBusinessBooking(true)
        .dinnerAllowance(inputLeisure.dinnerAllowance())
        .selectedRatePlan(inputLeisure.selectedRatePlan())
        .listOfRooms(inputLeisure.listOfRooms())
        .build()
    private val getBookingDetailsMock: ObserveBookingUseCase = mockk()
    private val observeAmendedReservationUpdatesUseCaseMock: ObserveAmendedReservationUseCase = mockk()
    private val originalReservationMock: OriginalReservationUseCase = mockk()
    private val clearReservationMock: ClearReservationUseCase = mockk()

    private val removeRoomUseCaseMock: RemoveRoomUseCase = mockk()
    private val removeStoredRoomUseCaseMock: RemoveStoredRoomUseCase = mockk()
    private val upsellsAvailableUseCaseMock: UpsellsAvailableUseCase = mockk()
    private val trackAnalyticsMock: TrackingAnalytics = mockk(relaxed = true)
    private val storageMock: SimplePersistenceManager = mockk()
    private val storageMockBusiness: BusinessPersistenceManager = mockk()
    private val deviceLocaleProviderMock: DeviceLocaleProvider = mockk()
    private val graphQLHotelDetailsUseCaseMock: GraphQLHotelDetailsUseCase = mockk()
    private val graphQLBookingDetailsUseCase: GraphQLBookingDetailsUseCase = mockk()
    private val graphQLAmendUseCase: GraphQLAmendUseCase = mockk()
    private val crashlyticsLogger: LogService = mockk()
    private val publishAmendSummaryDomain = SingleSubject.create<AmendSummaryDomain>()
    private lateinit var amendSummaryResponseDomain: AmendSummaryDomain
    private val stringProvider: AmendStringProvider = mockk()
    private lateinit var amendreservationviewmodelLeisure: AmendReservationViewModel
    private lateinit var amendreservationviewmodelLeisureWithPromo: AmendReservationViewModel
    private lateinit var amendreservationviewmodelBusiness: AmendReservationViewModel
    private val savedStateHandleMock: SavedStateHandle = mockk()

    @Before
    fun setUp() {
        every { getBookingDetailsMock.invoke(any()) } returns publishBookingDetails.toObservable()
        every { observeAmendedReservationUpdatesUseCaseMock(any()) } returns publishReservationUpdates
        every { originalReservationMock() } returns getFakeReservation()
        every { trackAnalyticsMock.track(any(), AmendBookingData("BKF1234")) } just Runs
        every { upsellsAvailableUseCaseMock.invoke() } returns publishUpsellAvailable.toObservable()
        every { deviceLocaleProviderMock.getDeviceLocale() } returns Locale.UK
        every { deviceLocaleProviderMock.getDeviceLanguage() } returns LANGUAGE_ENGLISH
        every { deviceLocaleProviderMock.getBookingChannel() } returns BOOKING_CHANNEL_MOBILE
        every { deviceLocaleProviderMock.getDeviceLocale().country } returns "gb"
        every { storageMockBusiness.getBusinessCustomerEmail() } returns EMPTY_STRING
        every { storageMock.getMaxNightsLeisure() } returns DEFAULT_MAX_NIGHTS_LEISURE
        every { storageMock.getMaxArrivalDateLeisure() } returns DEFAULT_MAX_ARRIVAL_DATE
        every { storageMock.getTempBookingRef() } returns TEMP_BASKET_REF
        every { graphQLHotelDetailsUseCaseMock.fetchHotelInfoFromGQL(any(), any(), any())} returns publishHotelInformationDomain


        every { graphQLAmendUseCase.copyBookingAndBookingConfirmationWithSavingToDb(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns publishPackagesAndAncillaryCloseoutDomain //Single.just(packagesAndAncillaryDomain)
        amendSummaryResponseDomain = InstanceFactory.create<AmendSummaryGraphQLContract.AmendSummaryData>(
            AmendSummaryGraphQLContract.AmendSummaryData::class.java, "apiTest/graphql/amend_summary_success_gql.json")
            .mapToAmendSummaryGQL()
        every { graphQLAmendUseCase.amendSummary(any()) } returns publishAmendSummaryDomain

        every { savedStateHandleMock.get<Boolean>(IS_ECI_LCO_BOOKING) } returns true
        every { savedStateHandleMock.get<Boolean>(IS_BUSINESS_BOOKING) } returns false
        every { savedStateHandleMock.get<String>(UUID_BASKET_REFERENCE) } returns "16635-72322asd32-777sggsg"
        every { savedStateHandleMock.get<String>(TOKEN) } returns "someToken"

        val promotionsInformationFalseMock = mockk<ParcelablePromotionsInformationDomain>()
        every { promotionsInformationFalseMock.promoBookingInfo?.promotionCode } returns ""

        val promotionsInformationTrueMock = mockk<ParcelablePromotionsInformationDomain>()
        every { promotionsInformationTrueMock.promoBookingInfo?.promotionCode } returns "promo_12345"

        mockSavedStateHandle()

        amendreservationviewmodelLeisure = AmendReservationViewModel(
            savedStateHandleMock,
            getBookingDetailsMock,
            observeAmendedReservationUpdatesUseCaseMock,
            originalReservationMock,
            clearReservationMock,
            removeRoomUseCaseMock,
            removeStoredRoomUseCaseMock,
            stringProvider,
            upsellsAvailableUseCaseMock,
            trackAnalyticsMock,
            storageMock,
            storageMockBusiness,
            deviceLocaleProviderMock,
            graphQLHotelDetailsUseCaseMock,
            graphQLAmendUseCase,
            graphQLBookingDetailsUseCase,
            crashlyticsLogger
        ).apply {
            setPromotionInformation(promotionsInformationFalseMock)
        }

        amendreservationviewmodelLeisureWithPromo = AmendReservationViewModel(
            savedStateHandleMock,
            getBookingDetailsMock,
            observeAmendedReservationUpdatesUseCaseMock,
            originalReservationMock,
            clearReservationMock,
            removeRoomUseCaseMock,
            removeStoredRoomUseCaseMock,
            stringProvider,
            upsellsAvailableUseCaseMock,
            trackAnalyticsMock,
            storageMock,
            storageMockBusiness,
            deviceLocaleProviderMock,
            graphQLHotelDetailsUseCaseMock,
            graphQLAmendUseCase,
            graphQLBookingDetailsUseCase,
            crashlyticsLogger
        ).apply {
            setPromotionInformation(promotionsInformationTrueMock)
        }

        amendreservationviewmodelBusiness = AmendReservationViewModel(
            savedStateHandleMock,
            getBookingDetailsMock,
            observeAmendedReservationUpdatesUseCaseMock,
            originalReservationMock,
            clearReservationMock,
            removeRoomUseCaseMock,
            removeStoredRoomUseCaseMock,
            stringProvider,
            upsellsAvailableUseCaseMock,
            trackAnalyticsMock,
            storageMock,
            storageMockBusiness,
            deviceLocaleProviderMock,
            graphQLHotelDetailsUseCaseMock,
            graphQLAmendUseCase,
            graphQLBookingDetailsUseCase,
            crashlyticsLogger
        ).apply {
            setPromotionInformation(promotionsInformationFalseMock)
        }
    }

    @Test
    fun `should StartAmend successfully leisure`() {
        val testObserver = amendreservationviewmodelLeisure.states().test()
        publishPackagesAndAncillaryCloseoutDomain.onSuccess(getFakePackagesAndAncillaries())
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) { it.startAmendResult is Success }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `should StartAmend successfully leisure with promo`() {
        val testObserver = amendreservationviewmodelLeisureWithPromo.states().test()
        publishPackagesAndAncillaryCloseoutDomain.onSuccess(getFakePackagesAndAncillaries())
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) { it.isPromotionalBooking }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `should StartAmend successfully business`() {
        val testObserver = amendreservationviewmodelBusiness.states().test()
        publishPackagesAndAncillaryCloseoutDomain.onSuccess(getFakePackagesAndAncillaries())
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) { it.startAmendResult is Success }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `should load hotel name successfully leisure`() {
        val testObserver = amendreservationviewmodelLeisure.states().test()
        publishHotelInformationDomain.onSuccess(getFakeHotelInfo())
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) { it.hotelName == "Bangor (Gwynedd, North Wales)" }
            .assertValueAt(1) { it.hotelName == "Bangor (Gwynedd, North Wales)" }
            .assertValueAt(1) { it.galleryImages == listOf("image1","image2")}
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `should load hotel name successfully business`() {
        val testObserver = amendreservationviewmodelBusiness.states().test()
        publishHotelInformationDomain.onSuccess(getFakeHotelInfo())
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) { it.hotelName == "Bangor (Gwynedd, North Wales)" }
            .assertValueAt(1) { it.hotelName == "Bangor (Gwynedd, North Wales)" }
            .assertValueAt(1) { it.galleryImages == listOf("image1","image2")}
            .assertValueCount(2)
            .assertNoErrors()
    }


    @Test
    fun `should load ancillary closeout leisure`() {
        val testObserver = amendreservationviewmodelLeisure.states().test()
        publishPackagesAndAncillaryCloseoutDomain.onSuccess(getFakePackagesAndAncillaries())
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) { it.startAmendResult is Success }
            .assertValueAt(1) { it.pairOfMealsAndAncillaryCloseoutItems == Pair(getFakePackagesAndAncillaries().packages.packages.meals,
                getFakePackagesAndAncillaries().ancillaryCloseOutItems) }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `should load ancillary closeout business`() {
        val testObserver = amendreservationviewmodelBusiness.states().test()
        publishPackagesAndAncillaryCloseoutDomain.onSuccess(getFakePackagesAndAncillaries())
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) { it.startAmendResult is Success }
            .assertValueAt(1) { it.pairOfMealsAndAncillaryCloseoutItems == Pair(getFakePackagesAndAncillaries().packages.packages.meals,
                getFakePackagesAndAncillaries().ancillaryCloseOutItems) }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `should load amended reservation successfully leisure`() {
        val testObserver = amendreservationviewmodelLeisure.states().test()
        publishReservationUpdates.onNext(getFakeReservation())
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.reservationDates == getFakeDates() &&
                        it.amendedRoomWithDetails[0] == AmendReservationState.AmendedRoom(
                    position = 1,
                    isRoomRemovable = false,
                    roomNumber = 1,
                    roomId = "",
                    roomCriteria = getFakeRoomCriteria()[0],
                    roomLeadGuest = getFakeGuests()[0],
                    roomUpsells = emptyList(),
                    isGuestNamesRestricted = false,
                    isRoomRestricted = false,
                    roomBreakdown = ROOM_BREAKDOWN
                )
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `should load amended reservation successfully business`() {
        val testObserver = amendreservationviewmodelBusiness.states().test()
        publishReservationUpdates.onNext(getFakeReservation())
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.reservationDates == getFakeDates() &&
                        it.amendedRoomWithDetails[0] == AmendReservationState.AmendedRoom(
                    position = 1,
                    isRoomRemovable = false,
                    roomNumber = 1,
                    roomId = "",
                    roomCriteria = getFakeRoomCriteria()[0],
                    roomLeadGuest = getFakeGuests()[0],
                    roomUpsells = emptyList(),
                    isGuestNamesRestricted = false,
                    isRoomRestricted = false,
                    roomBreakdown = ROOM_BREAKDOWN
                )
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when cancel button clicked show dialog`() {
        val testObserver = amendreservationviewmodelLeisure.events().test()
        amendreservationviewmodelLeisure.onCancelClicked()
        testObserver.assertValue(ShowCancelBookingDialog)
    }

    @Test
    fun `when cancel button clicked show dialog business`() {
        val testObserver = amendreservationviewmodelBusiness.events().test()
        amendreservationviewmodelBusiness.onCancelClicked()
        testObserver.assertValue(ShowCancelBookingDialog)
    }

    @Test
    fun `when cancel booking confirmed invoke cancel booking`() {
        every { graphQLBookingDetailsUseCase.cancelReservation(any(), any()) } returns Single.just(cancelBookingResponse)
        amendreservationviewmodelLeisure.onCancelBookingConfirmed()
        verify { graphQLBookingDetailsUseCase.cancelReservation(any(), any()) }
    }

    @Test
    fun `when cancel booking confirmed invoke cancel booking business`() {
        every { graphQLBookingDetailsUseCase.cancelReservation(any(), any()) } returns Single.just(cancelBookingResponse)
        amendreservationviewmodelBusiness.onCancelBookingConfirmed()
        verify { graphQLBookingDetailsUseCase.cancelReservation(any(), any()) }
    }

    @Test
    fun `should get UpsellsAvailable successfully`() {
        val testObserver = amendreservationviewmodelLeisure.states().test()
        publishUpsellAvailable.onSuccess(listOf(AVAILABLE_UPSELL))
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.getAvailableUpsells == listOf(AVAILABLE_UPSELL)
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `should get UpsellsAvailable successfully business`() {
        val testObserver = amendreservationviewmodelBusiness.states().test()
        publishUpsellAvailable.onSuccess(listOf(AVAILABLE_UPSELL))
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.getAvailableUpsells == listOf(AVAILABLE_UPSELL)
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when remove room is invoke then invoke remove room call`() {
        every { removeRoomUseCaseMock
            .invoke(any()) } returns
                Observable.just(TempBookingRefDomain("test")            )
        amendreservationviewmodelLeisure.onRemoveRoomConfirmed(roomId)
        verify { removeRoomUseCaseMock.invoke(any()) }
    }

    @Test
    fun `when remove room is invoke then invoke remove room call business`() {
        every { removeRoomUseCaseMock.invoke(any()) } returns Observable.just(TempBookingRefDomain("test"))
        amendreservationviewmodelBusiness.onRemoveRoomConfirmed(roomId)
        verify { removeRoomUseCaseMock.invoke(any()) }
    }

    @Test
    fun `when amend summary is called amend success event is called`() {
        publishAmendSummaryDomain.onSuccess(amendSummaryResponseDomain)
        val testObserver = amendreservationviewmodelLeisure.events().test()
        amendreservationviewmodelLeisure.amendSummaryCall()
        testObserver.assertValue(AmendSummarySuccessEvent(amendSummaryResponseDomain))
        amendreservationviewmodelLeisure.amendSummaryCall()
        verify { graphQLAmendUseCase.amendSummary(any()) }
    }

    @Test
    fun `when amend summary is called amend success event is called business`() {
        publishAmendSummaryDomain.onSuccess(amendSummaryResponseDomain)
        val testObserver = amendreservationviewmodelBusiness.events().test()
        amendreservationviewmodelBusiness.amendSummaryCall()
        testObserver.assertValue(AmendSummarySuccessEvent(amendSummaryResponseDomain))
        amendreservationviewmodelBusiness.amendSummaryCall()
        verify { graphQLAmendUseCase.amendSummary(any()) }
    }

    fun mockSavedStateHandle(isBusiness: Boolean = false) {
        if(isBusiness) {
            every { savedStateHandleMock.get<Boolean>(IS_BUSINESS_BOOKING) } returns false
                every { savedStateHandleMock.get<ManageBookingInput>(EXTRA_AMEND_INPUT) } returns inputBusiness
        } else {
            every { savedStateHandleMock.get<ManageBookingInput>(EXTRA_AMEND_INPUT) } returns inputLeisure
        }
    }

    private fun getFakeReservation(): Reservation {
        val now = LocalDate.now()
        return Reservation(
            bookingReference = "BKF1234",
            arrival = now,
            departure = now.plusDays(1),
            hotelCode = "LONMON",
            roomsCriteria = getFakeRoomCriteria(),
            roomsLeadGuest = getFakeGuests(),
            cancelable = true,
            upsells = emptyList(),
            roomsBreakdown = listOf(ROOM_BREAKDOWN)
        )
    }

    private fun getFakeGuests(): List<Guest> {
        return listOf(
            Guest(
                roomNumber = 1,
                title = "Mr",
                firstName = "John",
                lastName = "Smith",
                guestHistoryNumber = null,
                address = null,
                emailAddress = null,
                phoneNumber = null
            )
        )
    }

    private fun getFakeRoomCriteria(): List<RoomCriteria> {
        return listOf(
            RoomCriteria(
                roomNumber = 1,
                roomType = RoomType.DOUBLE,
                numberOfAdults = 1,
                numberOfChildren = 0,
                includeCot = false,
                numberOfInfants = 0
            )
        )
    }

    private fun getFakeHotelInfo(): HotelInformationDomain =
        HotelInformationDomain.createEmptyDomain().copy(
            name = "Bangor (Gwynedd, North Wales)",
            brand = "PI",
            galleryImages = listOf(GalleryImageDomain("image1"), GalleryImageDomain("image2")))

    private fun getFakeDates(): Pair<LocalDate, LocalDate> {
        val now = LocalDate.now()
        return now to now.plusDays(1)
    }

    private fun getFakePackagesAndAncillaries() : PackagesAndAncillaryCloseoutDomain {
        val packagesPackagesDomain = createPackagesPackagesDomain()
        val packagesDomain = createDataPackagesDomain().copy(packages = packagesPackagesDomain)
        val ancillaryCloseOutItems = listOf(
            AncillaryCloseOutItem(
                startDate = "25/12/2027",
                endDate = "31/12/2027",
                upsellCodes = "BFADBF,MDP"
            )
        )
        return PackagesAndAncillaryCloseoutDomain(
            packages = packagesDomain,
            ancillaryCloseOutItems = ancillaryCloseOutItems
        )
    }
    private val cancelBookingResponse = CancelReservationDomain(
        "BKF1234"
    )
}
