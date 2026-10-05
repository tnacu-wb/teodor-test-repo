package com.whitbread.premierinn.hoteldetails.discountcode

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.api.response.InstanceFactory
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelAvailabilityGQL
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelAvailabilityGraphQLContract
import com.whitbread.premierinn.domain.common.COUNTRY_CODE_UK
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.hdp.usecase.GraphQLHDPUseCase
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.promotions.usecase.GraphQLPromotionsInformationUseCase
import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetViewModel
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DiscountCodeBottomSheetViewModelTest {

    private lateinit var viewModel: DiscountCodeBottomSheetViewModel
    private val graphQLHDPUseCase: GraphQLHDPUseCase = mockk(relaxed = true)
    private val graphQLPromotionsInformationUseCase: GraphQLPromotionsInformationUseCase = mockk(relaxed = true)
    private val logService: LogService = mockk(relaxed = true)
    private val deviceLocaleProvider: DeviceLocaleProvider = mockk(relaxed = true)
    private val input: DiscountCodeInput = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val defaultHotelAvailability by lazy { loadHotelAvailabilityDomainFromJson() }
    private val defaultAvailabilityState by lazy {
        createCustomAvailabilityState(defaultHotelAvailability)
    }
    private val savedStateHandle: SavedStateHandle = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        every { deviceLocaleProvider.getDeviceLanguage() } returns LANGUAGE_ENGLISH
        every { deviceLocaleProvider.getCountryIfRegion(any()) } returns COUNTRY_CODE_UK
        every { input.appliedPromoCode } returns null
        every { savedStateHandle.get<DiscountCodeInput>(DiscountCodeConstants.BundleKeys.INPUT_KEY) } returns input
        viewModel = DiscountCodeBottomSheetViewModel(
            savedStateHandle,
            graphQLHDPUseCase,
            graphQLPromotionsInformationUseCase,
            logService,
            deviceLocaleProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }

    @Test
    fun `initial state is correct`() = runTest {
        assertEquals("", viewModel.state.value.discountCode)
        assertEquals(false, viewModel.state.value.isLoading)
        assertEquals(false, viewModel.state.value.isSuccess)
        assertEquals(null, viewModel.state.value.appliedPromoCode)
    }

    @Test
    fun `update discount code updates state`() = runTest {
        viewModel.processAction(DiscountCodeAction.UpdateDiscountCode("TESTCODE"))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("TESTCODE", viewModel.state.value.discountCode)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `apply discount code success updates state and emits events`() = runTest {
        stubValidatePromoSuccess()
        stubFetchHotelAvailability(defaultAvailabilityState)
        viewModel.processAction(DiscountCodeAction.ApplyDiscountCode("TESTCODE"))
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.isSuccess)
        assertEquals("TESTCODE", viewModel.state.value.appliedPromoCode)
    }

    @Test
    fun `apply discount code invalid code updates error`() = runTest {
        stubValidatePromoInvalid()
        viewModel.processAction(DiscountCodeAction.ApplyDiscountCode("INVALID"))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(
            DiscountCodeAction.DiscountAppliedError.ErrorType.INVALID,
            viewModel.state.value.errorType
        )
        verifyAvailabilityNotCalled()
    }

    @Test
    fun `apply discount code error updates error`() = runTest {
        stubValidatePromoError()
        viewModel.processAction(DiscountCodeAction.ApplyDiscountCode("ANYCODE"))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(
            DiscountCodeAction.DiscountAppliedError.ErrorType.GENERAL,
            viewModel.state.value.errorType
        )
        verifyAvailabilityNotCalled()
    }

    @Test
    fun `apply discount code when already loading does nothing`() = runTest {
        stubValidatePromoSuccess()
        stubFetchHotelAvailability(defaultAvailabilityState)
        viewModel.processAction(DiscountCodeAction.ApplyDiscountCode("TESTCODE"))
        viewModel.processAction(DiscountCodeAction.ApplyDiscountCode("TESTCODE"))
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.isSuccess)
        assertEquals("TESTCODE", viewModel.state.value.appliedPromoCode)
    }

    @Test
    fun `remove discount code triggers availability call and clears state if code not present`() =
        runTest {
            stubValidatePromoSuccess()
            val hotelAvailabilityWithCode = defaultHotelAvailability.copy(
                roomRateDomainList = listOf(
                    defaultHotelAvailability.roomRateDomainList?.firstOrNull()?.copy(
                        promotionCode = "TESTCODE",
                        ratePlanCode = "TESTCODE"
                    ) ?: error("No room rates in test data")
                )
            )
            val availabilityStateWithCode = createCustomAvailabilityState(hotelAvailabilityWithCode)
            stubFetchHotelAvailability(availabilityStateWithCode)

            // Apply discount code
            viewModel.processAction(DiscountCodeAction.ApplyDiscountCode("TESTCODE"))
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(viewModel.state.value.isSuccess)
            assertEquals("TESTCODE", viewModel.state.value.appliedPromoCode)

            // Stub next availability call with TESTCODE missing (simulate removal)
            val hotelAvailabilityWithoutCode = defaultHotelAvailability.copy(
                roomRateDomainList = defaultHotelAvailability.roomRateDomainList?.filter {
                    it.promotionCode != "TESTCODE"
                } ?: emptyList()
            )
            val availabilityStateWithoutCode =
                createCustomAvailabilityState(hotelAvailabilityWithoutCode)
            stubFetchHotelAvailability(availabilityStateWithoutCode)

            // Remove discount code (should clear state since code is not present in new data)
            viewModel.processAction(DiscountCodeAction.RemoveDiscountCode(""))
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert state is reset (no applied promo code, not success)
            assertFalse(viewModel.state.value.isSuccess)
            assertNull(viewModel.state.value.appliedPromoCode)
        }

    @Test
    fun `apply discount code with multiple codes error`() = runTest {
        val firstResponse = createPromoResponse(
            status = DiscountCodeConstants.SUCCESS
        )
        val secondResponse = createPromoResponse(
            status = DiscountCodeConstants.CODE_ALREADY_APPLIED,
            messageKey = "whenCodeAlreadyApplied",
            message = "Promotion already applied"
        )
        every {
            graphQLPromotionsInformationUseCase.execute(
                any(), any(), any(), any(), any(), any(), any(), any(), any()
            )
        } returnsMany listOf(
            Single.just(firstResponse),
            Single.just(secondResponse)
        )
        stubFetchHotelAvailability(defaultAvailabilityState)

        viewModel.processAction(DiscountCodeAction.ApplyDiscountCode("TESTCODE"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.processAction(DiscountCodeAction.ApplyDiscountCode("NEWCODE"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            DiscountCodeAction.DiscountAppliedError.ErrorType.MULTIPLE_REDEEM,
            viewModel.state.value.errorType
        )
        // When detecting locally that a code is already applied, no API message is available
        // The UI will use the fallback string resource (discount_code_multiple_redeem_error)
        assertNull(viewModel.state.value.errorMessage)
        verifyAvailabilityCallCount(1)
    }

    @Test
    fun `continue action closes dialog when success`() = runTest {
        stubValidatePromoSuccess()
        stubFetchHotelAvailability(createCustomAvailabilityState(defaultHotelAvailability))
        viewModel.processAction(DiscountCodeAction.ApplyDiscountCode("TESTCODE"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.processAction(DiscountCodeAction.Continue)
        testDispatcher.scheduler.advanceUntilIdle()
        val event = viewModel.state.value.event
        assertTrue(
            event is DiscountCodeBottomSheetEvent.DiscountCodeBottomSheetClosed,
            "Expected DiscountCodeBottomSheetClosed event to be emitted, but got: $event"
        )
        viewModel.onEventConsumed()
    }

    @Test
    fun `continue action emits cleared event when not success`() = runTest {
        viewModel.processAction(DiscountCodeAction.Continue)
        testDispatcher.scheduler.advanceUntilIdle()
        val event = viewModel.state.value.event
        assertTrue(event is DiscountCodeBottomSheetEvent.DiscountCodeBottomSheetClosed)
        viewModel.onEventConsumed()
    }

    @Test
    fun `close action emits cleared event`() = runTest {
        viewModel.processAction(DiscountCodeAction.Close)
        testDispatcher.scheduler.advanceUntilIdle()
        val event = viewModel.state.value.event
        assertTrue(event is DiscountCodeBottomSheetEvent.DiscountCodeBottomSheetClosed)
        viewModel.onEventConsumed()
    }

    @Test
    fun `applied promo message is restored on init`() = runTest {
        val localInput: DiscountCodeInput = mockk(relaxed = true)
        every { localInput.appliedPromoCode } returns "SAVE20"
        every { localInput.appliedPromoMessage } returns "Voucher applied successfully"
        every { savedStateHandle.get<DiscountCodeInput>(DiscountCodeConstants.BundleKeys.INPUT_KEY) } returns localInput

        val localViewModel = DiscountCodeBottomSheetViewModel(
            savedStateHandle,
            graphQLHDPUseCase,
            graphQLPromotionsInformationUseCase,
            logService,
            deviceLocaleProvider
        )

        assertTrue(localViewModel.state.value.isSuccess)
        assertEquals("SAVE20", localViewModel.state.value.appliedPromoCode)
        assertEquals("Voucher applied successfully", localViewModel.state.value.successMessage)
    }

    private fun loadHotelAvailabilityDomainFromJson(): HotelAvailabilityDomain {
        val gqlData =
            InstanceFactory.create<HotelAvailabilityGraphQLContract.HotelAvailabilityData>(
                HotelAvailabilityGraphQLContract.HotelAvailabilityData::class.java,
                "apiTest/graphql/hotel_availability_and_rates_with_promoCode_info_and_roomtypes_gql.json"
            )
        return gqlData.mapToHotelAvailabilityGQL()
    }

    private fun createCustomAvailabilityState(
        hotelAvailability: HotelAvailabilityDomain
    ): HotelBookingAvailabilityState = mockk(relaxed = true) {
        every { isHotelAvailabilitySuccessfulHDP } returns true
        every { getHotelAvailability } returns hotelAvailability
    }

    private fun stubFetchHotelAvailability(
        availability: HotelBookingAvailabilityState
    ) {
        every {
            graphQLHDPUseCase.fetchHotelAvailabilityAndRatesInfoAndRoomType(
                any(), any(), any(), any(), any(), any(), any()
            )
        } returns Observable.just(availability)
    }

    private fun stubValidatePromoSuccess() {
        stubValidatePromoResponse(createPromoResponse(DiscountCodeConstants.SUCCESS))
    }

    private fun stubValidatePromoInvalid() {
        stubValidatePromoResponse(
            createPromoResponse(
                status = DiscountCodeConstants.INVALID,
                messageKey = "whenInvalid",
                message = "You entered an invalid code, please Check the code and try again"
            )
        )
    }

    private fun stubValidatePromoError() {
        every { graphQLPromotionsInformationUseCase.execute(
                any(), any(), any(), any(), any(), any(), any(), any(), any()
            )
        } returns Single.error(RuntimeException("Network error"))
    }

    private fun stubValidatePromoResponse(response: PromotionsInformationDomain) {
        every {
            graphQLPromotionsInformationUseCase.execute(
                any(), any(), any(), any(), any(), any(), any(), any(), any()
            )
        } returns Single.just(response)
    }

    private fun createPromoResponse(
        status: String,
        messageKey: String? = null,
        message: String? = null
    ): PromotionsInformationDomain {
        val promoBox = messageKey?.let {
            com.whitbread.premierinn.domain.graphql.promotions.entity.PromoBoxDomain(
                title = null,
                button = null,
                whenInvalid = if (messageKey == "whenInvalid") message else null,
                whenMultipleRedeem = if (messageKey == "whenMultipleRedeem") message else null,
                whenSuccess = if (messageKey == "whenSuccess") message else null,
                whenEmpty = if (messageKey == "whenEmpty") message else null,
                whenCodeAlreadyApplied = if (messageKey == "whenCodeAlreadyApplied") message else null,
                whenUnavailable = if (messageKey == "whenUnavailable") message else null,
                whenCodeExpired = if (messageKey == "whenCodeExpired") message else null
            )
        }
        return PromotionsInformationDomain(
            showPromo = null,
            isWithinPromoWindow = null,
            promotionCode = null,
            landingPage = null,
            promoBannerColour = null,
            promoBannerIcon = null,
            termsLink = null,
            appPromoBannerTitle = null,
            appPromoBannerSubtitle = null,
            appPromoInvalidMessage = null,
            appPromoExpiredMessage = null,
            appPromoAmendMessage = null,
            promoBookingInfo = null,
            promoBox = promoBox,
            promoKind = null,
            promoBoxStatus = status,
            promoBoxMessageKey = messageKey
        )
    }

    private fun verifyAvailabilityNotCalled() {
        verifyAvailabilityCallCount(0)
    }

    private fun verifyAvailabilityCallCount(count: Int) {
        io.mockk.verify(exactly = count) {
            graphQLHDPUseCase.fetchHotelAvailabilityAndRatesInfoAndRoomType(
                any(), any(), any(), any(), any(), any(), any()
            )
        }
    }
}