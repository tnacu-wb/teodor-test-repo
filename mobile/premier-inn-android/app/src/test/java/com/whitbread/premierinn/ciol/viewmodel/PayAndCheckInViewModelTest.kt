package com.whitbread.premierinn.ciol.viewmodel

import com.whitbread.premierinn.ciol.analytics.logCiolAnalytics
import com.whitbread.premierinn.ciol.analyticsModel
import com.whitbread.premierinn.ciol.mapper.convertToUiModel
import com.google.common.truth.Truth
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpdateReservationPackagesUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.entity.upsells.toUpdateReservationPackagesRequestBody
import com.whitbread.premierinn.ciol.paymentMethod
import com.whitbread.premierinn.ciol.preStayModel
import com.whitbread.premierinn.ciol.usecase.GenerateAndSaveRegCardPdfUseCase
import com.whitbread.premierinn.ciol.usecase.GetConfirmationSpinnerMessagesUseCase
import com.whitbread.premierinn.ciol.usecase.PaymentPollingUseCase
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel.BillingAddressError.FirstLineInvalidError
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel.BillingAddressError.PostcodeInvalidError
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.ciol.entity.PriceBreakdown
import com.whitbread.premierinn.domain.ciol.usecase.GetCountriesUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetPriceBreakdownUseCase
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.error.DomainError
import com.whitbread.premierinn.domain.error.DomainError.BillingAddressValidationError.CountriesRetrievalError
import com.whitbread.premierinn.domain.error.DomainError.BillingAddressValidationError.InvalidFieldsError
import com.whitbread.premierinn.domain.graphql.ciol.usecase.GetPaymentMethodsUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.InitiatePaymentUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.PreCheckInUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.UpdateReservationPackagesUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ValidateBillingAddressUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ValidateBillingAddressUseCase.InvalidBillingAddressFields.FIRST_LINE
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ValidateBillingAddressUseCase.InvalidBillingAddressFields.POSTCODE
import com.whitbread.premierinn.domain.graphql.requestBodyModels.InitiatePaymentRequestBody
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.utils.TestCoroutineRule
import com.whitbread.premierinn.utils.collectEmissions
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(TestCoroutineRule::class)
class PayAndCheckInViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val deviceLocaleProvider = mockk<DeviceLocaleProvider>(relaxed = true)
    private val getPaymentMethodsUseCase = mockk<GetPaymentMethodsUseCase>()
    private val initiatePaymentUseCase = mockk<InitiatePaymentUseCase>()
    private val getCountriesUseCase = mockk<GetCountriesUseCase>()
    private val validateBillingAddressUseCase = mockk<ValidateBillingAddressUseCase>()
    private val appConfiguration = mockk<AppConfiguration>()
    private val paymentPollingUseCase = mockk<PaymentPollingUseCase>()
    private val getConfirmationSpinnerMessagesUseCase = mockk<GetConfirmationSpinnerMessagesUseCase>()
    private val trackingAnalytics = mockk<TrackingAnalytics>()
    private val updateReservationPackagesUseCase = mockk<UpdateReservationPackagesUseCase>()
    private val getPriceBreakdownUseCase = mockk<GetPriceBreakdownUseCase>()
    private val generateAndSaveRegCardPdfUseCase = mockk<GenerateAndSaveRegCardPdfUseCase>()
    private val preCheckInInitiationUseCase = mockk<PreCheckInUseCase>()
    private val isFeatureOn = mockk<IsFeatureOn>()


    private lateinit var payAndCheckInViewModel: PayAndCheckInViewModel
    private var stateResults = mutableListOf<PayAndCheckInViewModel.PayAndCheckInState>()

    @BeforeEach
    fun setup() {
        payAndCheckInViewModel = PayAndCheckInViewModel(
            deviceLocaleProvider,
            getPaymentMethodsUseCase,
            initiatePaymentUseCase,
            getCountriesUseCase,
            validateBillingAddressUseCase,
            appConfiguration,
            paymentPollingUseCase,
            getConfirmationSpinnerMessagesUseCase,
            trackingAnalytics,
            updateReservationPackagesUseCase,
            getPriceBreakdownUseCase,
            generateAndSaveRegCardPdfUseCase,
            preCheckInInitiationUseCase,
            isFeatureOn
        )

        stateResults.clear()
        collectEmissions(payAndCheckInViewModel.state, stateResults, testCoroutineRule.testScheduler)
    }

    @Test
    @DisplayName("GIVEN the user pressed Find Address with a valid post code, WHEN the address is returned, " +
            "THEN the billingAddress will be updated in the state")
    fun scenario1() {
        // GIVEN
        val expectedAddress = Address(
            line1 = "1st Cherry St",
            postCode = "0XE 0NY"
        )

        // WHEN
        payAndCheckInViewModel.onBillingAddressGenerated(expectedAddress)

        // THEN
        assertEquals(expectedAddress, stateResults.last().billingAddress)
    }

    @Nested
    @DisplayName("Billing Address Validation Test Suite")
    inner class BillingAddressValidationTests {

        private val billingAddress = Address(
            postCode = "M1 1AE",
            line1 = "Apartment 1",
            line2 = "113 Newton Street",
            countryCode = "GB"
        )

        @BeforeEach
        fun setup() {
            every { trackingAnalytics.track(any(), any<AnalyticsData>()) } just runs
            val priceBreakdown = PriceBreakdown(
                outstandingBalance = PriceDomain(100f, "GBP"),
                roomSelections = emptyList(),
                nights = 1
            )
            coEvery { getPriceBreakdownUseCase() } returns MutableStateFlow(priceBreakdown)
        }

        @Test
        @DisplayName("GIVEN the user pressed Continue to Pay button, WHEN 'same as booker address' is checked, " +
                "THEN the app will proceed with initiate payment")
        fun scenario1() = runTest {
            // GIVEN
            every { isFeatureOn.invoke(Key.FEATURE_PIBA_CP_ENABLED) } returns false
            coEvery { validateBillingAddressUseCase.invoke(any(), any()) } returns flowOf(Result.Success(false))
            every {
                logCiolAnalytics(trackingAnalytics, preStayModel.convertToUiModel(),
                    AnalyticsConstants.ScreenState.CIOL_PAYMENTS
                )
            } just runs

            every { appConfiguration.graphQLUrl } returns "https://api.preprod.premierinn.digital"
            coEvery { getPaymentMethodsUseCase(any()) } returns flowOf(Result.Success(listOf(paymentMethod)))
            coEvery { initiatePaymentUseCase(any(), any()) } returns flowOf(Result.Success(InitiatePaymentUseCase.InitiatePaymentResultData.NormalPayment("content")))
            payAndCheckInViewModel.onScreenOpened(preStayModel, analyticsModel, "Birthday", null)

            // WHEN
            payAndCheckInViewModel.validateAddress(null, false, null)
            advanceUntilIdle()

            // THEN
            coVerify(exactly = 1) {
                initiatePaymentUseCase(false, any())
            }
        }

        @Test
        @DisplayName("GIVEN the user pressed Continue to Pay button AND 'same as booker address' was not checked, " +
                "WHEN the address is valid, THEN the app will proceed with initiate payment")
        fun scenario2() = runTest {
            // GIVEN
            val expectedAddress = Address(
                line1 = "1st Cherry St",
                postCode = "0XE 0NY"
            )
            coEvery {
                validateBillingAddressUseCase.invoke(
                    any(),
                    any()
                )
            } returns flowOf(Result.Success(true))
            every {
                logCiolAnalytics(trackingAnalytics, preStayModel.convertToUiModel(),
                    AnalyticsConstants.ScreenState.CIOL_PAYMENTS
                )
            } just runs
            every { isFeatureOn.invoke(Key.FEATURE_PIBA_CP_ENABLED) } returns false

            every { appConfiguration.graphQLUrl } returns "https://api.preprod.premierinn.digital"
            coEvery { getPaymentMethodsUseCase(any()) } returns flowOf(Result.Success(listOf(paymentMethod)))
            coEvery { initiatePaymentUseCase(any(), any()) } returns flowOf(Result.Success(InitiatePaymentUseCase.InitiatePaymentResultData.NormalPayment("content")))
            payAndCheckInViewModel.onScreenOpened(preStayModel, analyticsModel, "Birthday", null)

            // WHEN
            payAndCheckInViewModel.validateAddress(expectedAddress, false, null)
            advanceUntilIdle()

            // THEN
            val initiatePaymentRequestBodySlot = slot<InitiatePaymentRequestBody>()
            coVerify(exactly = 1) {
                initiatePaymentUseCase(false, capture(initiatePaymentRequestBodySlot))
            }
            initiatePaymentRequestBodySlot.captured.createPaymentCriteria.payment.billing.apply {
                assertEquals(expectedAddress.line1, address.addressLine1)
                assertEquals(expectedAddress.postCode, address.postalCode)
            }
        }

        @Test
        @DisplayName("GIVEN the user pressed Continue to Pay button AND 'same as booker address' was not" +
                " checked, WHEN CountriesRetrievalError is returned, THEN a GenericError will be emitted")
        fun scenario3() = runTest {
            // GIVEN
            val shouldDisplayDetailedAddress = false
            coEvery { validateBillingAddressUseCase.invoke(any(), any()) } returns flowOf(Result.Error(CountriesRetrievalError))

            // WHEN
            payAndCheckInViewModel.validateAddress(billingAddress, shouldDisplayDetailedAddress, null)
            advanceUntilIdle()

            // THEN
            assertEquals(payAndCheckInViewModel.state.value.error, PayAndCheckInViewModel.Error.GenericError)
        }

        @Test
        @DisplayName("GIVEN the user pressed Continue to Pay button AND 'same as booker address' was not" +
                " checked, WHEN POSTCODE and FIRST_LINE are invalid, THEN the correct billingAddressErrors  will be emitted")
        fun scenario4() = runTest {
            // GIVEN
            val shouldDisplayDetailedAddress = false
            coEvery { validateBillingAddressUseCase.invoke(any(), any()) } returns
                    flowOf(Result.Error(InvalidFieldsError(listOf(POSTCODE, FIRST_LINE))))

            // WHEN
            payAndCheckInViewModel.validateAddress(billingAddress, shouldDisplayDetailedAddress, null)
            advanceUntilIdle()

            // THEN
            assertEquals(
                payAndCheckInViewModel.state.value.billingAddressErrors,
                listOf(PostcodeInvalidError, FirstLineInvalidError)
            )
        }
    }

    @Nested
    @DisplayName("Billing Address Switch state changed Test Suite")
    inner class BillingAddressSwitchStateChangedTests {
        @Test
        @DisplayName("GIVEN the billing address switch was checked, WHEN the user unchecks the switch, " +
                "THEN shouldDisplayDetailedAddress will be set to false")
        fun scenario1() = runTest {
            // GIVEN
            val country = CountryDomain("A", "AT", "Austria", true, "Austrian")
            coEvery { getCountriesUseCase(any()) } returns flowOf(Result.Success(listOf(country) to 0))
            payAndCheckInViewModel.onBillingAddressSwitchStateChanged(true)
            assertFalse { stateResults.last().shouldDisplayDetailedAddress }

            // WHEN
            payAndCheckInViewModel.onBillingAddressSwitchStateChanged(false)
            advanceUntilIdle()

            // THEN
            assertTrue { stateResults.last().shouldDisplayDetailedAddress }
        }

        @Test
        @DisplayName("GIVEN the user unchecked the billing address switch, WHEN the countries are retrieved " +
                "successfully, THEN the state will be updated with returned countries")
        fun scenario2() = runTest {
            // GIVEN
            val country = CountryDomain("A", "AT", "Austria", true, "Austrian")
            coEvery { getCountriesUseCase(any()) } returns flowOf(Result.Success(listOf(country) to 0))

            // WHEN
            payAndCheckInViewModel.onBillingAddressSwitchStateChanged(false)
            advanceUntilIdle()

            // THEN
            stateResults.last().apply {
                assertTrue { shouldDisplayDetailedAddress }
                assertFalse { isLoading }
                assertEquals(listOf(country), countries)
            }
        }

        @Test
        @DisplayName("GIVEN the user unchecked the billing address switch, WHEN the countries retrieval " +
                "is failing, THEN the state will be updated with GenericError")
        fun scenario3() = runTest {
            // GIVEN
            coEvery { getCountriesUseCase(any()) } returns flowOf(Result.Error(DomainError.CountriesError()))

            // WHEN
            payAndCheckInViewModel.onBillingAddressSwitchStateChanged(false)
            advanceUntilIdle()

            // THEN
            stateResults.last().apply {
                assertFalse { shouldDisplayDetailedAddress }
                assertFalse { isLoading }
                assertEquals(PayAndCheckInViewModel.Error.GenericError, error)
            }
        }

        @Test
        @DisplayName("GIVEN the user unchecked the billing address switch, WHEN the countries retrieval " +
                "is failing, THEN the loading state will be updated accordingly")
        fun scenario4() = runTest {
            // GIVEN
            coEvery { getCountriesUseCase(any()) } returns flowOf(Result.Error(DomainError.CountriesError()))

            // WHEN
            payAndCheckInViewModel.onBillingAddressSwitchStateChanged(false)
            advanceUntilIdle()

            // THEN
            stateResults.apply {
                assertEquals(3, size)
                assertFalse { this[0].isLoading } // Initial state
                assertTrue { this[1].isLoading } // Before starting work
                assertFalse { this[2].isLoading } // After work finished
            }
        }
    }

    @Nested
    @DisplayName("Update reservation packages Test Suite")
    inner class UpdateReservationPackagesTests {

        private val updateReservationPackagesUiModel = UpdateReservationPackagesUiModel(
            basketReference = "AQN123",
            roomSelectionList = listOf(
                RoomSelection(
                    reservationId = "132435",
                    selectedUpsells = mutableListOf(
                        MealUiModel(
                            id = "BFADBF",
                            noOfSelections = 2,
                            displayAsEnabled = true,
                            freeBreakfastSelections = 0,
                            preselectedNoOfSelections = 0
                        )
                    )
                )
            ),
            hotelId = "HEAPTI",
            arrivalDate = "2025-03-07",
            departureDate = "2025-03-08"
        )

        @Test
        @DisplayName(
            "GIVEN upsells were added, WHEN user pressed the continue button AND " +
                    "reservation call succeeds, THEN wereUpsellsUpdatedRemotely flag will be set to true"
        )
        fun scenario1() = runTest {
            // GIVEN
            coEvery {
                updateReservationPackagesUseCase(updateReservationPackagesUiModel.toUpdateReservationPackagesRequestBody())
            } returns flowOf(Result.Success(Unit))

            // WHEN
            payAndCheckInViewModel.updateReservationPackages(null, updateReservationPackagesUiModel)
            advanceUntilIdle()

            // THEN
            stateResults.apply {
                Truth.assertThat(this[0].isInitiatePaymentLoading).isEqualTo(false)
                Truth.assertThat(this[0].wereUpsellsUpdatedRemotely).isEqualTo(false)
                Truth.assertThat(this[1].isInitiatePaymentLoading).isEqualTo(true)
                Truth.assertThat(this[2].isInitiatePaymentLoading).isEqualTo(false)
                Truth.assertThat(this[2].wereUpsellsUpdatedRemotely).isEqualTo(true)
            }
        }

        @Test
        @DisplayName(
            "GIVEN no upsells were added, WHEN user pressed the continue button, THEN " +
                    "updateReservation won't be called"
        )
        fun scenario2() = runTest {
            // WHEN
            payAndCheckInViewModel.updateReservationPackages(null, null)
            advanceUntilIdle()

            // THEN
            coVerify(exactly = 0) {
                updateReservationPackagesUseCase(any())
            }
        }

        @Test
        @DisplayName(
            "GIVEN upsells were added, WHEN user pressed the continue button AND " +
                    "reservation call fails, THEN wereUpsellsUpdatedRemotely won't be changed"
        )
        fun scenario3() = runTest {
            // GIVEN
            coEvery {
                updateReservationPackagesUseCase(updateReservationPackagesUiModel.toUpdateReservationPackagesRequestBody())
            } returns flowOf(Result.Error(DataError.Network.Unknown("error message")))

            // WHEN
            payAndCheckInViewModel.updateReservationPackages(null, updateReservationPackagesUiModel)
            advanceUntilIdle()

            // THEN
            stateResults.apply {
                Truth.assertThat(this[0].isInitiatePaymentLoading).isEqualTo(false)
                Truth.assertThat(this[0].wereUpsellsUpdatedRemotely).isEqualTo(false)
                Truth.assertThat(this[1].isInitiatePaymentLoading).isEqualTo(true)
                Truth.assertThat(this[2].isInitiatePaymentLoading).isEqualTo(false)
                Truth.assertThat(this[2].wereUpsellsUpdatedRemotely).isEqualTo(false)
            }
        }
    }
}
