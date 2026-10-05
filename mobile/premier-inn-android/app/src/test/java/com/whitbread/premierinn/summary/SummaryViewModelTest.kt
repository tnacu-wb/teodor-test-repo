package com.whitbread.premierinn.summary

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.summary.SummaryExtrasToggleHelper
import com.whitbread.premierinn.common.summary.SummaryMealsIncrementDecrementHelper
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.BookingConfirmationGQDomain
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodsWithDonationAndBookingConfirmationGQLDomain
import com.whitbread.premierinn.domain.graphql.summary.entity.SaveReservationWithAncillariesDomain
import com.whitbread.premierinn.domain.graphql.summary.usecase.GraphQLSummaryUseCase
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.FREE_BREAKFAST_PROMO_TAG
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.utils.MainDispatcherRule
import com.whitbread.premierinn.utils.model.CustomerFixture.bCustomer
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


class SummaryViewModelTest {

    // Sets the main coroutines dispatcher to a TestCoroutineDispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // mocked dependencies
    private val input = mockSummaryInput()
    private val deviceLocaleProvider = mockk<DeviceLocaleProvider>(relaxed = true)
    private val graphQLSummaryUseCase = mockk<GraphQLSummaryUseCase>(relaxed = true)
    private val simplePersistenceManager = mockk<SimplePersistenceManager>(relaxed = true)
    private val businessPersistenceManager = mockk<BusinessPersistenceManager>(relaxed = true)
    private val stringProvider = mockk<StringResourceProvider>(relaxed = true)
    private val summaryMealsHelper = SummaryMealsIncrementDecrementHelper()
    private val summaryExtrasHelper = SummaryExtrasToggleHelper()
    private val trackingAnalytics = mockk<TrackingAnalytics>(relaxed = true)
    private val logService = mockk<LogService>(relaxed = true)
    private val getStringResource = mockk<GetStringResource>(relaxed = true)

    private val savedStateHandle: SavedStateHandle= mockk(relaxed = true) {
        every { get<SummaryState>("uiState") } returns null
        every { get<SummaryInput>(SUMMARY_INPUT_V2) } returns input
    }

    // common vals
    private val basketReference = "basketRef123"
    private val testRoomList = createTestRoomList_initial()
    private val testEmptyRoomList = emptyList<SummaryRoomItem>()
    private val testTotalPrice = 60.00f

    private var viewModel = SummaryViewModel(
        savedStateHandle,
        deviceLocaleProvider,
        graphQLSummaryUseCase,
        simplePersistenceManager,
        businessPersistenceManager,
        stringProvider,
        summaryMealsHelper,
        summaryExtrasHelper,
        trackingAnalytics,
        logService,
        getStringResource
    )


    // Will need to re organize this in a later PR to have separate sections leisure and BB

    @Test
    fun `initial state is correctly set on ViewModel start`() = runTest {

        viewModel.state.test {

            // Capture the initial default state
            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)
            assertEquals(EMPTY_STRING_DOMAIN, initialState.basketReference)
            assertEquals(null, initialState.error)
            assertEquals(testEmptyRoomList, initialState.roomList)
            assertEquals(EMPTY_STRING_DOMAIN, initialState.totalStayPrice)

            // Trigger the first state update
            viewModel.init()

            // Capture the loading state
            val loadingState = awaitItem()
            assertEquals(basketReference, loadingState.basketReference)
            assertEquals(null, loadingState.error)
            assertEquals(testRoomList, loadingState.roomList)
            assertEquals(testTotalPrice.addCurrency(), loadingState.totalStayPrice)


            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial state calls logAnalytics - leisure`() = runTest {

        viewModel.state.test {

            // Capture the initial default state
            awaitItem()

            // Trigger the first state update
            viewModel.init()

            // Capture the loading state
            awaitItem()
            val screenNameSlot = slot<String>()
            val dataSlot = slot<AnalyticsData>()

            verify(exactly = 1) {
                trackingAnalytics.track(
                    capture(screenNameSlot),
                    capture(dataSlot)
                )
            }

            assertEquals("Add Extras", screenNameSlot.captured)

            cancelAndIgnoreRemainingEvents()
        }
    }

    //To replace with just log analytics
    @Test
    fun `initial state calls logAnalytics - business`() = runTest {

        val businessUserInput = mockSummaryInput_businessUser() // true for businessUser
        val viewModel = SummaryViewModel(
            savedStateHandle,
            deviceLocaleProvider,
            graphQLSummaryUseCase,
            simplePersistenceManager,
            businessPersistenceManager,
            stringProvider,
            summaryMealsHelper,
            summaryExtrasHelper,
            trackingAnalytics,
            logService,
            getStringResource
        )

        every { businessPersistenceManager.getCompany() } returns bCustomer().company

        viewModel.state.test {

            // Capture the initial default state
            awaitItem()

            // Trigger the first state update
            viewModel.init()

            // Capture the loading state
            awaitItem()
            val screenNameSlot = slot<String>()
            val dataSlot = slot<AnalyticsData>()

            verify(exactly = 1) {
                trackingAnalytics.track(
                    capture(screenNameSlot),
                    capture(dataSlot)
                )
            }

            assertEquals("Add Extras", screenNameSlot.captured)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveReservation updates state correctly on success response`() = runTest {

        val saveReservation = "basketRef123"

        coEvery { graphQLSummaryUseCase.saveResWithAncillaries(any()) } returns flow {
            emit(Result.Success(SaveReservationWithAncillariesDomain(saveReservation)))
        }

        viewModel.state.test {

            // Trigger the side effects
            viewModel.saveReservationWithAncillaries()

            // Capture the initial state - isLoading is false prior to saveReservationWithAncillaries() being called
            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)
            assertEquals(null, initialState.error)

            // Capture the loading state - isLoading is set to true while saveReservationWithAncillaries() runs
            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)
            assertEquals(null, loadingState.error)


            // Capture the final state after saveReservation success - isLoading is set to false again
            val finalState = awaitItem()
            assertEquals(false, finalState.isLoading)
            assertEquals(null, finalState.error)


            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveReservation updates state correctly on error response`() = runTest {

        coEvery { graphQLSummaryUseCase.saveResWithAncillaries(any()) } returns flow {
            emit(Result.Error(DataError.Network.GraphQlError("")))
        }

        viewModel.state.test {

            // Trigger the side effects
            viewModel.saveReservationWithAncillaries()

            // Capture the initial state - isLoading is false prior to saveReservationWithAncillaries() being called
            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)
            assertEquals(null, initialState.error)

            // Capture the loading state - isLoading is set to true while saveReservationWithAncillaries() runs
            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)
            assertEquals(null, loadingState.error)

            // Capture the final state after saveReservation error - isLoading is set to false again
            val finalState = awaitItem()
            assertEquals(false, finalState.isLoading)
            assertEquals(SummaryError.SaveReservationWithAncillariesError(MealSelectionStatus.NO_SELECTION, false), finalState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `isLoggedIn is set correctly on ViewModel init`() = runTest {

        val loggedInInput = mockSummaryInput_loginTrue()
        every { savedStateHandle.get<SummaryInput>(SUMMARY_INPUT_V2) } returns loggedInInput
        val viewModel = SummaryViewModel(
            savedStateHandle,
            deviceLocaleProvider,
            graphQLSummaryUseCase,
            simplePersistenceManager,
            businessPersistenceManager,
            stringProvider,
            summaryMealsHelper,
            summaryExtrasHelper,
            trackingAnalytics,
            logService,
            getStringResource
        )

        // Initial
        assertEquals(false, viewModel.isLoggedIn)

        viewModel.init()

        assertEquals(true, viewModel.isLoggedIn)
    }

    @Test
    fun `isBusinessUser is set correctly on ViewModel init`() = runTest {

        val businessUserInput = mockSummaryInput_businessUser()
        every { savedStateHandle.get<SummaryInput>(SUMMARY_INPUT_V2) } returns businessUserInput
        val viewModel = SummaryViewModel(
            savedStateHandle,
            deviceLocaleProvider,
            graphQLSummaryUseCase,
            simplePersistenceManager,
            businessPersistenceManager,
            stringProvider,
            summaryMealsHelper,
            summaryExtrasHelper,
            trackingAnalytics,
            logService,
            getStringResource
        )

        // Initial
        assertEquals(false, viewModel.isInnBusinessUser)

        viewModel.init()

        assertEquals(true, viewModel.isInnBusinessUser)
    }

    @Test
    fun `isBusinessUser setAdditional information is set correctly on ViewModel init`() = runTest {

        val businessUserInput = mockSummaryInput_businessUser()
        every { businessPersistenceManager.getCompany() } returns bCustomer().company
        every { savedStateHandle.get<SummaryInput>(SUMMARY_INPUT_V2) } returns businessUserInput
        val viewModel = SummaryViewModel(
            savedStateHandle,
            deviceLocaleProvider,
            graphQLSummaryUseCase,
            simplePersistenceManager,
            businessPersistenceManager,
            stringProvider,
            summaryMealsHelper,
            summaryExtrasHelper,
            trackingAnalytics,
            logService,
            getStringResource
        )
        // Initial
        assertEquals(false, viewModel.isInnBusinessUser)

        viewModel.init()

        assertEquals(true, viewModel.isInnBusinessUser)
    }

    @Test
    fun `mealIncrementCounter updates state correctly`() = runTest {
        // Arrange
        val updatedMealsTotalPrice = (testTotalPrice +
                createTestRoomList_initial().first().meals.first().price.amount)
            .addCurrency()

        val updatedTestRoomList = createTestRoomList_mealsInc()

        coEvery { graphQLSummaryUseCase.createReservation(any()) } returns flow {
            emit(Result.Success(basketReference))
        }

        // Act
        viewModel.state.test {

            // Ignore the initial state
            awaitItem()

            // Initialize the ViewModel to populate the initial state
            viewModel.init()

            awaitItem()

            // call the method to increment the meal counter
            viewModel.mealIncrementCounter(0, 0)

            // Assert
            val updatedState = awaitItem()
            assertEquals(updatedTestRoomList, updatedState.roomList)
            assertEquals(updatedMealsTotalPrice, updatedState.totalStayPrice)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `mealDecrementCounter updates state correctly`() = runTest {
        // Arrange
        val updatedMealsTotalPrice = (testTotalPrice +
                createTestRoomList_initial().first().meals.first().price.amount)
            .addCurrency()

        val updatedTestRoomList = createTestRoomList_mealsInc()

        coEvery { graphQLSummaryUseCase.createReservation(any()) } returns flow {
            emit(Result.Success(basketReference))
        }

        // Act
        viewModel.state.test {

            // Ignore the initial state
            awaitItem()

            // Initialize the ViewModel to populate the initial state
            viewModel.init()

            awaitItem()

            // call the method to increment the meal counter
            viewModel.mealIncrementCounter(0, 0)


            // Assert
            val incrementState = awaitItem()
            assertEquals(updatedTestRoomList, incrementState.roomList)
            assertEquals(updatedMealsTotalPrice, incrementState.totalStayPrice)

            // call the method to increment the meal counter
            viewModel.mealDecrementCounter(0, 0)

            // Assert
            val decrementState = awaitItem()
            assertEquals(testRoomList, decrementState.roomList)
            assertEquals(testTotalPrice.addCurrency(), decrementState.totalStayPrice)


            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateExtrasToggleState updates state correctly`() = runTest {
        // Arrange
        val updatedTestTotalPrice = (testTotalPrice +
                createTestRoomList_initial().first().extras.first().price.amount)
            .addCurrency()

        val updatedTestRoomList = createTestRoomList_extrasToggled()

        coEvery { graphQLSummaryUseCase.createReservation(any()) } returns flow {
            emit(Result.Success(basketReference))
        }

        // Act
        viewModel.state.test {
            // Ignore the initial state
            awaitItem()

            // Initialize the ViewModel to populate the initial state
            viewModel.init()

            awaitItem()

            viewModel.updateExtrasToggleState(0, 0, true)

            // Assert
            val updatedState = awaitItem()

            // Assert
            assertEquals(updatedTestRoomList, updatedState.roomList)
            assertEquals(updatedTestTotalPrice, updatedState.totalStayPrice)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `goToNextScreen emits OpenGuestDetailsActivity when user is logged in`() = runTest {
        // Since the basket ref needs to be set first , we need to trigger the state via init()
        viewModel.state.test {
            awaitItem()

            viewModel.init()

            awaitItem()

            cancelAndIgnoreRemainingEvents()
        }

        // prepare the state so that constructBookingFlowInput(...) will pick up the testRoomList
        val expectedInput = viewModel.constructBookingFlowInput(testRoomList, true)

        // Act: collect the navigationEvent
        viewModel.navigationEvent.test {

            // Call the function under test
            viewModel.goToNextScreen(true)

            // Grab the emitted navigation event
            val navEvent = awaitItem()
            // It should be the "guest details" variant
            assertEquals(true, navEvent is SummaryNavigation.OpenGuestDetailsActivity)

            // And inside, the bookingFlowInput should reflect your testRoomList and 'true'
            assertEquals(expectedInput, (navEvent as SummaryNavigation.OpenGuestDetailsActivity).bookingFlowInput)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `goToNextScreen emits OpenLoginActivity when user is not logged in`() = runTest {


        viewModel.navigationEvent.test {

            viewModel.goToNextScreen(false)

            val navEvent = awaitItem()
            // Should be the login-activity variant
            assertEquals(true, navEvent is SummaryNavigation.OpenLoginActivity)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `goToNextScreen emits OpenReviewBookActivity for business user without employee question`() = runTest {
        val businessUserInput = mockSummaryInput_businessUser()
        every { savedStateHandle.get<SummaryInput>(SUMMARY_INPUT_V2) } returns businessUserInput
        val mockResponseForPaymentMethod = mockk<PaymentMethodsWithDonationAndBookingConfirmationGQLDomain>(relaxed = true)
        val mockResponseForBookingConfirmation = mockk<BookingConfirmationGQDomain>(relaxed = true)

        every { mockResponseForPaymentMethod.isPaymentMethodAvailable } returns true
        every { mockResponseForPaymentMethod.bookingConfirmation } returns mockResponseForBookingConfirmation


        coEvery { graphQLSummaryUseCase.getPaymentMethodsAndBookingConfirmation(any(), any(), any(), any(), any()) } returns flow {
            emit(Result.Success(mockResponseForPaymentMethod))
        }

        val viewModel = SummaryViewModel(
            savedStateHandle,
            deviceLocaleProvider,
            graphQLSummaryUseCase,
            simplePersistenceManager,
            businessPersistenceManager,
            stringProvider,
            summaryMealsHelper,
            summaryExtrasHelper,
            trackingAnalytics,
            logService,
            getStringResource
        )

        viewModel.init()

        viewModel.navigationEvent.test {
            viewModel.goToNextScreen(true)

            val navEvent = awaitItem()
            assertTrue(navEvent is SummaryNavigation.OpenReviewBookActivity)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `goToNextScreen emits OpenAdditionalInfoActivity for business user with employee question`() =
        runTest {
            val businessUserInput = mockSummaryInput_businessUser()
            every { savedStateHandle.get<SummaryInput>(SUMMARY_INPUT_V2) } returns businessUserInput
            every { businessPersistenceManager.getCompany() } returns bCustomer().company

            val viewModel = SummaryViewModel(
                savedStateHandle,
                deviceLocaleProvider,
                graphQLSummaryUseCase,
                simplePersistenceManager,
                businessPersistenceManager,
                stringProvider,
                summaryMealsHelper,
                summaryExtrasHelper,
                trackingAnalytics,
                logService,
                getStringResource
            )

            viewModel.init()

            viewModel.navigationEvent.test {
                viewModel.goToNextScreen(true)

                val navEvent = awaitItem()
                assertTrue(navEvent is SummaryNavigation.OpenAdditionalInfoActivity)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `mealsList sets all promo meal fields correctly when isPromoMeal is true`() = runTest {

        val freeBreakfastPromoInput = mockSummaryInputFreeBreakfastPromo()
        every { savedStateHandle.get<SummaryInput>(SUMMARY_INPUT_V2) } returns freeBreakfastPromoInput
        val viewModel = SummaryViewModel(
            savedStateHandle,
            deviceLocaleProvider,
            graphQLSummaryUseCase,
            simplePersistenceManager,
            businessPersistenceManager,
            stringProvider,
            summaryMealsHelper,
            summaryExtrasHelper,
            trackingAnalytics,
            logService,
            getStringResource
        )

        every { simplePersistenceManager.getFreeBreakfastPromotionCode() } returns "FREECODE"
        every { getStringResource.invoke(FREE_BREAKFAST_PROMO_TAG) } returns "Free breakfast"

        val adults = 2

        val result = viewModel.mealsList(adults)

        assertEquals(1, result.size)
        val meal = result.first()
        assertEquals(0f, meal.price.amount, 0.0001f)
        assertEquals(11.99f, meal.originalPrice!!.amount, 0.0001f)
        assertEquals("Free breakfast", meal.offerTag)
        assertEquals(adults, meal.counter)
    }

    @Test
    fun `mealsList sets isPromoMeal to false when price is positive even if promotion code matches`() = runTest {

        val freeBreakfastPromoInput = mockSummaryInputFreeBreakfastPromoWithPositiveUpsell()
        every { savedStateHandle.get<SummaryInput>(SUMMARY_INPUT_V2) } returns freeBreakfastPromoInput

        val viewModel = SummaryViewModel(
            savedStateHandle,
            deviceLocaleProvider,
            graphQLSummaryUseCase,
            simplePersistenceManager,
            businessPersistenceManager,
            stringProvider,
            summaryMealsHelper,
            summaryExtrasHelper,
            trackingAnalytics,
            logService,
            getStringResource
        )

        every { simplePersistenceManager.getFreeBreakfastPromotionCode() } returns "FREECODE"
        every { getStringResource.invoke(FREE_BREAKFAST_PROMO_TAG) } returns "Free breakfast"

        val adults = 2

        val result = viewModel.mealsList(adults)

        assertEquals(1, result.size)
        val meal = result.first()
        assertEquals(11.99f, meal.price.amount, 0.0001f)
        assertNull(meal.originalPrice)
        assertEquals("", meal.offerTag)
        assertEquals(0, meal.counter)
    }

    @Test
    fun `mealsList sets isPromoMeal false when price is negative but promotion code does not match`() = runTest {

        val someOtherBfPromo = mockSummaryInputFreeBreakfastPromoWithDiffPromoCode()
        every { savedStateHandle.get<SummaryInput>(SUMMARY_INPUT_V2) } returns someOtherBfPromo

        val viewModel = SummaryViewModel(
            savedStateHandle,
            deviceLocaleProvider,
            graphQLSummaryUseCase,
            simplePersistenceManager,
            businessPersistenceManager,
            stringProvider,
            summaryMealsHelper,
            summaryExtrasHelper,
            trackingAnalytics,
            logService,
            getStringResource
        )

        every { simplePersistenceManager.getFreeBreakfastPromotionCode() } returns "FREECODE"
        every { getStringResource.invoke(FREE_BREAKFAST_PROMO_TAG) } returns "Free breakfast"

        val adults = 2

        val result = viewModel.mealsList(adults)

        assertEquals(1, result.size)
        val meal = result.first()
        assertEquals(11.99f, meal.price.amount, 0.0001f)
        assertNull(meal.originalPrice)
        assertEquals("", meal.offerTag)
        assertEquals(0, meal.counter)
    }

}

