package com.whitbread.premierinn.amend.amendupsells

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.businessbooker.domain.company.BookingAllowances
import com.whitbread.premierinn.businessbooker.domain.company.Company
import com.whitbread.premierinn.businessbooker.domain.company.CompanyDetails
import com.whitbread.premierinn.businessbooker.domain.company.RequestCompany
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.model.HotelInfoFixture
import com.whitbread.premierinn.common.model.ManageBookingInputFixture
import com.whitbread.premierinn.common.model.ReservationFixture
import com.whitbread.premierinn.common.model.RoomCriteriaFixture
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.summary.SummaryExtrasToggleHelper
import com.whitbread.premierinn.common.summary.SummaryMealsIncrementDecrementHelper
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDetailsUseCase
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.amend.usecase.GetUpsellsAndAncillaryCloseoutUseCase
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.amend.usecase.PackagesAndAncillariesCloseOutData
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.UpsellDomainItem
import com.whitbread.premierinn.domain.graphql.utils.getId
import com.whitbread.premierinn.domain.reservation.entity.RoomSelectedUpsells
import com.whitbread.premierinn.domain.reservation.usecase.ObserveAmendedReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.meals.UpdateAllRoomUpsellUseCase
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.utils.MainDispatcherRule
import com.whitbread.premierinn.utils.RxJavaTestRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.Locale

private val INPUT = ManageBookingInputFixture.aManageBookingInput()
private val HOTEL = HotelInfoFixture.aHotelInfoMockForNameIncludingRestaurantTopSectionImage()
private val RESERVATION = ReservationFixture.aReservation(
    roomsCriteria = listOf(
        RoomCriteriaFixture.aRoomCriteria(
            numberOfAdults = 2,
            numberOfChildren = 1,
            roomId = "room1"
        )
    )
)
private const val TEMPORARY_BASKET_REFERENCE = "16635-72322asd32-777sggsg"
private const val BOOKING_FLOW_ID = "booking-flow-123"
private const val HOTEL_BRAND = "PI"

@OptIn(ExperimentalCoroutinesApi::class)
class AmendUpsellsViewModelTest {

    @get:Rule
    val rxRule = RxJavaTestRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private var testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers: AppDispatchers = mockk(relaxed = true)
    private val savedStateHandleMock: SavedStateHandle = mockk(relaxed = true)
    private val deviceLocaleProviderMock: DeviceLocaleProvider = mockk(relaxed = true)
    private val trackingAnalytics: TrackingAnalytics = mockk(relaxed = true)
    private val crashlyticsLogger: LogService = mockk(relaxed = true)
    private val graphQLAmendUseCase: GraphQLAmendUseCase = mockk(relaxed = true)
    private val getUpsellsAndAncillaryCloseoutUseCase: GetUpsellsAndAncillaryCloseoutUseCase = mockk(relaxed = true)
    private val observeAmendedReservationUseCase: ObserveAmendedReservationUseCase =
        mockk(relaxed = true)
    private val graphQLHotelDetailsUseCase: GraphQLHotelDetailsUseCase = mockk(relaxed = true)
    private val summaryMealsHelper = SummaryMealsIncrementDecrementHelper()
    private val summaryExtrasHelper = SummaryExtrasToggleHelper()
    private val updateAllRoomUpsellUseCase: UpdateAllRoomUpsellUseCase = mockk(relaxed = true)
    private val storage: SimplePersistenceManagerImpl = mockk(relaxed = true)
    private val businessStorage: BusinessPersistenceManager = mockk(relaxed = true)
    private val amendStringProvider: AmendStringProvider = mockk(relaxed = true)

    private lateinit var viewModel: AmendUpsellsViewModel

    private val testMealDomain1 = MealDomain(
        id = "BFADBF",
        name = "Unlimited Premier Inn Breakfast",
        bartId = "meal-bart-id",
        price = 12.99,
        currency = "GBP",
        shortDescription = "A hearty breakfast",
        description = "Full English Breakfast with eggs, bacon, sausages",
        imageSrc = "/images/breakfast1.jpg",
        allergyInfoSrc = "/allergy/breakfast1-info.pdf",
        menu = null,
        freeBreakfastOption = false
    )
    private val testMealDomain2 = MealDomain(
        id = "BFADCT",
        name = "Unlimited Continental Breakfast",
        price = 9.99,
        currency = "GBP",
        shortDescription = "Continental Breakfast",
        description = "A lighter way to start your day with tasty pastries, American pancakes, fruit, cereals and more.",
        imageSrc = "/images/breakfast2.jpg",
        allergyInfoSrc = "/allergy/breakfast2-info.pdf",
        menu = null,
        freeBreakfastOption = false
    )

    private val testExtraItemDomain = ExtrasItemDomain(
        id = "extra1",
        name = "Early Check-in",
        price = 15.00,
        currency = "GBP",
        description = "Check in from 12pm"
    )

    private val testAvailableUpsellsWithExtras: MutableList<UpsellDomainItem> = mutableListOf(
        testMealDomain1,
        testMealDomain2,
        testExtraItemDomain
    )

    private val testAvailableUpsellsWithoutExtras: MutableList<UpsellDomainItem> = mutableListOf(
        testMealDomain1,
        testMealDomain2
    )

    private fun createAmendUpsellsInput(): AmendUpsellsInput {
        return AmendUpsellsInput(
            manageBookingInput = INPUT,
            temporaryBasketReference = TEMPORARY_BASKET_REFERENCE,
            totalAdults = 2,
            totalChildren = 1,
            hotelBrand = HOTEL_BRAND,
            hotelName = HOTEL.name,
            bookingFlowId = BOOKING_FLOW_ID,
            galleryImages = listOf("/images/hotelimage1.jpg", "/images/hotelimage2.jpg"),
            arrivalDeparturePair = Pair(LocalDate.of(2026, 11, 21),
                LocalDate.of(2026, 11, 23))
        )
    }

    @Before
    fun setUp() {
        every { dispatchers.io } returns testDispatcher

        every { deviceLocaleProviderMock.getDeviceLocale() } returns Locale.UK
        every { deviceLocaleProviderMock.getDeviceLanguage() } returns "en"
        every { savedStateHandleMock.get<AmendUpsellsInput>(AMEND_UPSELLS_INPUT) } returns createAmendUpsellsInput()
        every {
            graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(
                any(),
                any(),
                any()
            )
        } returns Single.just(HOTEL)
        every { observeAmendedReservationUseCase.invoke(INPUT.bookingReference()) } returns Observable.just(
            RESERVATION
        )
        every { storage.getOriginalRoomSelection() } returns emptyList<RoomSelectionDomain>()
        every { amendStringProvider.trackAmendUpsellsError } returns "Amend Upsells Error"

        coEvery { getUpsellsAndAncillaryCloseoutUseCase.invoke(any()) } returns flowOf(
            Result.Success(
                PackagesAndAncillariesCloseOutData(
                    preselectedRoomSelections = null,
                    availableUpsells = testAvailableUpsellsWithExtras,
                    ancillaryCloseout = emptyList()
                )
            )
        )

        viewModel = getNewViewModel()
    }

    @Test
    fun `getPackages success updates state with available upsells`() = runTest {
        advanceUntilIdle()

        val currentState = viewModel.state.value
        assertEquals(testAvailableUpsellsWithExtras, currentState.availableUpsells)
    }

    @Test
    fun `getPackages error sends GeneralError event`() = runTest {
        coEvery { getUpsellsAndAncillaryCloseoutUseCase.invoke(any()) } returns flowOf(
            Result.Error(DataError.Network.GraphQlError("Error fetching packages"))
        )

        val viewModel = getNewViewModel()

        advanceUntilIdle()

        val currentState = viewModel.state.value
        assertTrue(
            "Expected GeneralError event",
            currentState.event is AmendUpsellsEvent.GeneralError
        )
    }

    @Test
    fun `initState sets the hotelname and hotelImage`() = runTest {

        val currentState = viewModel.state.value

        assertEquals(HOTEL.name, currentState.hotelName)
        assertEquals("/images/hotelimage1.jpg", currentState.galleryImageSrc?.first())
    }

    @Test
    fun `getAmendedReservation updates state with reservation and guest count`() = runTest {
        advanceUntilIdle()

        val currentState = viewModel.state.value
        assertNotNull(currentState.reservation)
        assertEquals(RESERVATION, currentState.reservation)
        assertEquals(RESERVATION.roomsCriteria.size, currentState.numberOfRooms)
    }

    @Test
    fun `initState sets isDinnerAllowance correctly when dinnerAllowance is positive`() = runTest {
        val inputWithDinnerAllowance =
            ManageBookingInputFixture.aManageBookingInput(dinnerAllowance = 50f)

        every { savedStateHandleMock.get<AmendUpsellsInput>(AMEND_UPSELLS_INPUT) } returns createAmendUpsellsInput().copy(
            manageBookingInput = inputWithDinnerAllowance
        )

        val viewModel = getNewViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.state.value.isDinnerAllowance)
    }

    @Test
    fun `getPackagesAndAncillaryCloseout sets restaurantClosed correctly`() = runTest {
        coEvery { getUpsellsAndAncillaryCloseoutUseCase.invoke(any()) } returns flowOf(
            Result.Success(
                PackagesAndAncillariesCloseOutData(
                    preselectedRoomSelections = null,
                    availableUpsells = testAvailableUpsellsWithoutExtras,
                    ancillaryCloseout = listOf(AncillaryCloseOutItem(
                        startDate = "21/11/2026",
                        endDate = "26/11/2026",
                        upsellCodes = testMealDomain1.id!! + "," + testMealDomain2.id!!)
                    )
                )
            )
        )

        val viewModel = getNewViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.state.value.isRestaurantClosed)
    }


    @Test
    fun `initState filters the ancillariescloseout from available meals`() = runTest {
        coEvery { getUpsellsAndAncillaryCloseoutUseCase.invoke(any()) } returns flowOf(
            Result.Success(
                PackagesAndAncillariesCloseOutData(
                    preselectedRoomSelections = null,
                    availableUpsells = testAvailableUpsellsWithoutExtras,
                    ancillaryCloseout = listOf(AncillaryCloseOutItem(
                        startDate = "21/11/2026",
                        endDate = "26/11/2026",
                        upsellCodes = testMealDomain1.id!!)
                    )
                )
            )
        )

        val viewModel = getNewViewModel()

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isRestaurantClosed)

        assertFalse( viewModel.state.value.isRestaurantClosed)
        assertEquals(1, viewModel.state.value.availableUpsells.size)
        assertEquals(testMealDomain2.id, viewModel.state.value.availableUpsells.first().getId())

    }

    @Test
    fun `initState sets showApprovedMeals correctly for business booking`() = runTest {
        val businessBookingInput =
            ManageBookingInputFixture.aManageBookingInput(isBusinessBooking = true)

        every { savedStateHandleMock.get<AmendUpsellsInput>(AMEND_UPSELLS_INPUT) } returns createAmendUpsellsInput().copy(
            manageBookingInput = businessBookingInput
        )

        val viewModel = getNewViewModel()

        advanceUntilIdle()

        assertTrue(viewModel.state.value.showApprovedMeals)
    }

    @Test
    fun `onAction BackClicked sends NavigateBack event`() = runTest {
        advanceUntilIdle()

        viewModel.onAction(AmendUpsellsAction.BackClicked)

        advanceUntilIdle()

        assertTrue(viewModel.state.value.event is AmendUpsellsEvent.NavigateBack)
    }

    @Test
    fun `onAction MenuAndAllergyInfoClicked with isAllergyInfo true sends ShowMenuAndAllergyInfo event`() =
        runTest {
            advanceUntilIdle()

            viewModel.onAction(AmendUpsellsAction.MenuAndAllergyInfoClicked(isAllergyInfo = true))

            advanceUntilIdle()

            assertTrue(viewModel.state.value.event is AmendUpsellsEvent.ShowMenuAndAllergyInfo)
        }

    @Test
    fun `onAction MenuAndAllergyInfoClicked with isAllergyInfo false sends ShowMenuAndAllergyInfo event`() =
        runTest {
            advanceUntilIdle()

            viewModel.onAction(AmendUpsellsAction.MenuAndAllergyInfoClicked(isAllergyInfo = false))

            advanceUntilIdle()

            assertTrue(viewModel.state.value.event is AmendUpsellsEvent.ShowMenuAndAllergyInfo)
        }

    @Test
    fun `onEventConsumed clears event from state`() = runTest {
        advanceUntilIdle()

        viewModel.onAction(AmendUpsellsAction.BackClicked)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.event)

        viewModel.onEventConsumed()
        advanceUntilIdle()

        assertNull(viewModel.state.value.event)
    }


    @Test
    fun `onAction ContinueClicked calls updateAllRoomUpsellUseCase`() = runTest {
        // Used a PublishSubject to control when values are emitted
        val updateSubject = PublishSubject.create<UpdateAllRoomUpsellUseCase.UpdateUpsellState>()

        every { updateAllRoomUpsellUseCase.invoke(any(), any<List<RoomSelectedUpsells>>()) } returns
                updateSubject

        advanceUntilIdle()

        viewModel.onAction(AmendUpsellsAction.ContinueClicked)
        advanceUntilIdle()

        updateSubject.onNext(UpdateAllRoomUpsellUseCase.UpdateUpsellState.Loading)
        advanceUntilIdle()

        assertTrue("Expected isLoading to be true", viewModel.state.value.isLoading)

        verify { updateAllRoomUpsellUseCase.invoke(any(), any<List<RoomSelectedUpsells>>()) }
    }

    @Test
    fun `setGalleryImages returns correct image URLs from meals`() = runTest {
        advanceUntilIdle()

        val currentState = viewModel.state.value
        assertTrue(currentState.listOfGalleryUpsellImages.isNotEmpty())
        assertTrue(currentState.listOfGalleryUpsellImages.any { it.contains("breakfast1.jpg") || it.contains("breakfast2.jpg") })
    }

    @Test
    fun `loading state becomes false after data is loaded`() = runTest {
        advanceUntilIdle()

        val currentState = viewModel.state.value
        assertFalse(currentState.isLoading)
    }

    @Test
    fun `state contains correct number of guests`() = runTest {
        advanceUntilIdle()

        val currentState = viewModel.state.value

        assertEquals(3, currentState.numberOfGuests)
    }

    @Test
    fun `state contains correct number of rooms`() = runTest {
        advanceUntilIdle()

        val currentState = viewModel.state.value
        assertEquals(RESERVATION.roomsCriteria.size, currentState.numberOfRooms)
    }

    @Test
    fun `business booking with all meals restricted sets areAllMealsRestricted true`() = runTest {
        val businessBookingInput = ManageBookingInputFixture.aManageBookingInput(isBusinessBooking = true)

        every { savedStateHandleMock.get<AmendUpsellsInput>(AMEND_UPSELLS_INPUT) } returns createAmendUpsellsInput().copy(
            manageBookingInput = businessBookingInput
        )
        every { businessStorage.getCompany() } returns createCompany(allowedUpsellItems = emptyList())
        every { observeAmendedReservationUseCase.invoke(businessBookingInput.bookingReference()) } returns Observable.just(
            RESERVATION
        )

        coEvery { getUpsellsAndAncillaryCloseoutUseCase.invoke(any()) } returns flow {
            emit(
                Result.Success(
                    PackagesAndAncillariesCloseOutData(
                        preselectedRoomSelections = null,
                        availableUpsells = testAvailableUpsellsWithExtras,
                        ancillaryCloseout = emptyList()
                    )
                )
            )
        }

        val viewModel = getNewViewModel()

        advanceUntilIdle()

        val currentState = viewModel.state.value
        assertTrue(currentState.areAllMealsRestricted)
        assertTrue(currentState.roomList.all { it.meals.isEmpty() })
        assertTrue(currentState.availableUpsells.none { it is MealDomain })
        assertTrue(currentState.availableUpsells.any { it is ExtrasItemDomain })
    }

    @Test
    fun `business booking with allowed meal keeps meal and clears areAllMealsRestricted`() = runTest {
        val businessBookingInput = ManageBookingInputFixture.aManageBookingInput(isBusinessBooking = true)

        every { savedStateHandleMock.get<AmendUpsellsInput>(AMEND_UPSELLS_INPUT) } returns createAmendUpsellsInput().copy(
            manageBookingInput = businessBookingInput
        )
        every { businessStorage.getCompany() } returns createCompany(allowedUpsellItems = listOf("meal-bart-id"))
        every { observeAmendedReservationUseCase.invoke(businessBookingInput.bookingReference()) } returns Observable.just(
            RESERVATION
        )

        coEvery { getUpsellsAndAncillaryCloseoutUseCase.invoke(any()) } returns flow {
            delay(10) //Refactor this in tech debt story to avoid using delay in tests
            emit(
                Result.Success(
                    PackagesAndAncillariesCloseOutData(
                        preselectedRoomSelections = null,
                        availableUpsells = testAvailableUpsellsWithExtras,
                        ancillaryCloseout = emptyList()
                    )
                )
            )
        }


        val viewModel = getNewViewModel()

        advanceUntilIdle()

        val currentState = viewModel.state.value
        assertFalse(currentState.areAllMealsRestricted)
        assertTrue(currentState.roomList.any { it.meals.isNotEmpty() })
        assertTrue(currentState.availableUpsells.any { it is MealDomain && it.bartId == "meal-bart-id" })
    }

    @Test
    fun `AmendUpsellsState has correct default values`() {
        val state = AmendUpsellsViewModel.AmendUpsellsState()

        assertEquals(EMPTY_STRING_DOMAIN, state.dateAndNights)
        assertEquals(EMPTY_STRING_DOMAIN, state.guestsAndRooms)
        assertFalse(state.showApprovedMeals)
        assertFalse(state.areAllMealsRestricted)
        assertFalse(state.isDinnerAllowance)
        assertTrue(state.isLoading)
        assertTrue(state.listOfGalleryUpsellImages.isEmpty())
        assertTrue(state.roomList.isEmpty())
        assertTrue(state.availableUpsells.isEmpty())
        assertNull(state.reservation)
        assertNull(state.hotelName)
        assertEquals(0, state.numberOfGuests)
        assertEquals(0, state.numberOfRooms)
        assertNull(state.event)
    }

    @Test
    fun `mealIncrementCounter action updates meal counter when room list is ready`() = runTest {
        advanceUntilIdle()

        val currentState = viewModel.state.value
        if (currentState.roomList.isNotEmpty() && currentState.roomList[0].meals.isNotEmpty()) {
            val initialCounter = currentState.roomList[0].meals[0].counter

            viewModel.onAction(AmendUpsellsAction.MealIncrement(roomIndex = 0, mealIndex = 0))
            advanceUntilIdle()

            val newCounter = viewModel.state.value.roomList[0].meals[0].counter
            assertEquals(initialCounter + 1, newCounter)
        }
    }

    @Test
    fun `mealDecrementCounter action updates meal counter after increment`() = runTest {
        advanceUntilIdle()

        val currentState = viewModel.state.value
        if (currentState.roomList.isNotEmpty() && currentState.roomList[0].meals.isNotEmpty()) {
            // First increment
            viewModel.onAction(AmendUpsellsAction.MealIncrement(roomIndex = 0, mealIndex = 0))
            advanceUntilIdle()

            val counterAfterIncrement = viewModel.state.value.roomList[0].meals[0].counter

            // Then decrement
            viewModel.onAction(AmendUpsellsAction.MealDecrement(roomIndex = 0, mealIndex = 0))
            advanceUntilIdle()

            val counterAfterDecrement = viewModel.state.value.roomList[0].meals[0].counter
            assertEquals(counterAfterIncrement - 1, counterAfterDecrement)
        }
    }

    @Test
    fun `updateExtraToggled action updates extra selection state`() = runTest {
        advanceUntilIdle()

        val currentState = viewModel.state.value
        if (currentState.roomList.isNotEmpty() && currentState.roomList[0].extras.isNotEmpty()) {
            viewModel.onAction(
                AmendUpsellsAction.ExtraToggled(
                    roomIndex = 0,
                    extraIndex = 0,
                    enabled = true
                )
            )
            advanceUntilIdle()

            assertTrue(viewModel.state.value.roomList[0].extras[0].selected)
        }
    }

    private fun getNewViewModel() = AmendUpsellsViewModel(
        savedStateHandleMock,
        deviceLocaleProviderMock,
        trackingAnalytics,
        crashlyticsLogger,
        graphQLAmendUseCase,
        getUpsellsAndAncillaryCloseoutUseCase,
        observeAmendedReservationUseCase,
        summaryMealsHelper,
        summaryExtrasHelper,
        updateAllRoomUpsellUseCase,
        storage,
        businessStorage,
        amendStringProvider,
        dispatchers
    )

    private fun createCompany(allowedUpsellItems: List<String>) = Company(
        requestedCompany = RequestCompany(
            companyDetails = CompanyDetails(
                companyName = "Goran's Gelato Company",
                alternateCompanyName = "GGC"
            ),
            paymentDetails = null,
            bookingAllowances = BookingAllowances(
                maxDinnerBudgets = null,
                upsellItemsAllowed = allowedUpsellItems,
                allowAlcohol = false,
                allowCarParking = false,
                allowPremierSaverRates = false,
                allowIndividualCards = false
            ),
            companyManagementDetails = null
        ),
        companyCellCodes = emptyList(),
        allowCentralCreditCard = false
    )
}
