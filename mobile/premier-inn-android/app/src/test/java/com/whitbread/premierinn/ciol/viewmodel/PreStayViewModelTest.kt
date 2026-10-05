package com.whitbread.premierinn.ciol.viewmodel

import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.hotelPreferences
import com.whitbread.premierinn.ciol.mapper.mapToHotelPreferenceUiModel
import com.whitbread.premierinn.ciol.uimodel.HotelPreferenceUiModel
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.mapper.PriceDomainParcelable
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.ciol.usecase.IsUpsellFlowEnabledUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetPriceBreakdownUseCase
import com.whitbread.premierinn.domain.ciol.usecase.UpdatePriceBreakdownUseCase
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ConfirmPreCheckInUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.GetHotelPreferencesUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.GetUpsellsUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.UpdateReservationPreferencesUseCase
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesSelectionDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.utils.TestCoroutineRule
import com.whitbread.premierinn.utils.collectEmissions
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.threeten.bp.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(TestCoroutineRule::class)
class PreStayViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val confirmPreCheckInUseCase = mockk<ConfirmPreCheckInUseCase>()
    private val getUpsellsUseCase = mockk<GetUpsellsUseCase>()
    private val getHotelPreferencesUseCase = mockk<GetHotelPreferencesUseCase>()
    private val updateReservationPreferencesUseCase = mockk<UpdateReservationPreferencesUseCase>()
    private val deviceLocaleProvider = mockk<DeviceLocaleProvider>(relaxed = true)
    private val trackingAnalytics = mockk<TrackingAnalytics>(relaxed = true)
    private val isUpsellFlowEnabledUseCase = mockk<IsUpsellFlowEnabledUseCase>()
    private val getPriceBreakdownUseCase = mockk<GetPriceBreakdownUseCase>()
    private val updatePriceBreakdownUseCase = mockk<UpdatePriceBreakdownUseCase>()
    private val isFeatureOn = mockk<IsFeatureOn>()

    private lateinit var preStayViewModel: PreStayViewModel
    private var stateResults = mutableListOf<PreStayViewModel.PreStayState>()

    val preStayUiModel = mockk<PreStayUiModel>(relaxed = true) {
        every { paymentOption } returns "PIBA_CNP"
        every { isBusinessBooking } returns false
        every { preStayDetails.preCheckInStatus } returns false
        every { preStayDetails.rooms.size } returns 2
        every { preStayHeaderInfo.hotelBrand } returns "PI"
        every { outstandingBalance } returns PriceDomainParcelable(100f, "GBP")
        every { basketReference } returns "basket123"
        every { bookingReference } returns "BOOKING123"
        every { preStayDetails.hotelId } returns "HOTEL123"
        every { preStayDetails.bookerDetails.leadBookerFirstName } returns "John"
        every { preStayDetails.startDate } returns LocalDate.of(2025, 1, 18)
        every { preStayDetails.endDate } returns LocalDate.of(2025, 1, 19)
        every { preStayDetails.numberOfNights } returns 1
        every { preStayDetails.numberOfAdults } returns 2
        every { preStayDetails.numberOfChildren } returns 0
        every { preStayHeaderInfo.hotelImage } returns "hotel_image_url"
        every { isThirdPartyBooking } returns false
    }

    @BeforeEach
    fun setup() {
        preStayViewModel = PreStayViewModel(
            confirmPreCheckInUseCase,
            getUpsellsUseCase,
            getHotelPreferencesUseCase,
            updateReservationPreferencesUseCase,
            deviceLocaleProvider,
            trackingAnalytics,
            isUpsellFlowEnabledUseCase,
            getPriceBreakdownUseCase,
            updatePriceBreakdownUseCase,
            isFeatureOn
        )

        stateResults.clear()
        collectEmissions(preStayViewModel.state, stateResults, testCoroutineRule.testScheduler)
    }

    @Nested
    @DisplayName("Special Occasion test suite")
    inner class PreStayOccasionsTests {
        @Test
        @DisplayName(
            "GIVEN the special occasions switch was unchecked, WHEN the user checked the special occasions switch," +
                    "THEN the select occasions layout should be displayed"
        )
        fun scenario1() = runTest {
            //GIVEN
            preStayViewModel.onOccasionsSwitchStateChanged(false)
            stateResults.last().apply {
                assertFalse(shouldDisplaySelectOccasionsLayout)
            }

            //WHEN
            preStayViewModel.onOccasionsSwitchStateChanged(true)

            //THEN
            stateResults.last().apply {
                assertEquals(selectedOccasion, null)
                assertFalse(showMissingOccasionError)
                assertTrue(shouldDisplaySelectOccasionsLayout)
            }
        }

        @Test
        @DisplayName(
            "GIVEN the user is on pre stay screen, WHEN the special occasions are returned," +
                    "THEN state is updated for special occasions"
        )
        fun scenario2() = runTest {
            //GIVEN
            val hotelId = "FRAMTI"
            val language = "en"

            val hotelPrefsUiModelList =
                listOf(hotelPreferences).map { it.mapToHotelPreferenceUiModel() }

            coEvery { getHotelPreferencesUseCase.invoke(any<String>(), any<String>()) } returns
                    flowOf(Result.Success(listOf(hotelPreferences)))

            //WHEN
            preStayViewModel.retrieveSpecialOccasions(hotelId, language)
            advanceUntilIdle()

            //THEN
            coVerify(exactly = 1) {
                getHotelPreferencesUseCase(hotelId, language)

            }
            stateResults.last().apply {
                assertEquals(
                    specialOccasions?.firstOrNull()?.code,
                    hotelPrefsUiModelList.firstOrNull()?.code
                )
                assertEquals(
                    specialOccasions?.firstOrNull()?.preferenceGroup,
                    hotelPrefsUiModelList.firstOrNull()?.preferenceGroup
                )
                assertEquals(
                    specialOccasions?.firstOrNull()?.label,
                    hotelPrefsUiModelList.firstOrNull()?.label
                )
            }
        }

        @Test
        @DisplayName("GIVEN the bottom sheet is open, WHEN the user selected an occasion THEN the selected occasion is updated")
        fun scenario3() = runTest {
            //GIVEN
            val mockedSelectedOccasion = HotelPreferenceUiModel(
                code = "ANNV",
                preferenceGroup = "EVENTS",
                label = "Anniversary"
            )
            preStayViewModel.onSelectOccasionClicked()
            stateResults.last().apply {
                assertTrue(openBottomSheet)
            }

            //WHEN
            preStayViewModel.onSpecialOccasionSelected(mockedSelectedOccasion)

            //THEN
            stateResults.last().apply {
                assertEquals(mockedSelectedOccasion, selectedOccasion)
                assertFalse(showMissingOccasionError)
            }
        }

        @Test
        @DisplayName(
            "GIVEN isBusinessBooking is true, WHEN confirming pre check-in, " +
                    "THEN skip upsell page and navigate to PayAndCheckInFragment"
        )
        fun skipUpsellPageForBusinessBooking() = runTest {
            val preStayUiModel = createPreStayUiModel(isBusinessBooking = true)

            coEvery { isUpsellFlowEnabledUseCase() } returns true
            preStayViewModel._state.update { it.copy(upsellItems = listOf(mockk())) }

            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            val navigation = stateResults.last().navigation
            assertTrue(navigation is PreStayViewModel.NavigationDestination.PayAndCheckInFragment)
        }

        @Test
        @DisplayName(
            "GIVEN third party booking with outstanding balance and upsells available, " +
                    "WHEN confirming pre check-in, THEN skip upsells and navigate to PayAndCheckInFragment"
        )
        fun skipUpsellsForThirdPartyBookingWithBalance() = runTest {
            val preStayUiModel =
                createPreStayUiModel(isThirdPartyBooking = true, isBusinessBooking = false)

            coEvery { isUpsellFlowEnabledUseCase() } returns true
            preStayViewModel._state.update { it.copy(upsellItems = listOf(mockk())) }

            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            val allNavigations = stateResults.mapNotNull { it.navigation }
            assertFalse(allNavigations.any { it is PreStayViewModel.NavigationDestination.UpsellsFragment })
            assertTrue(allNavigations.last() is PreStayViewModel.NavigationDestination.PayAndCheckInFragment)

        }

        @Test
        @DisplayName(
            "GIVEN third party booking with no outstanding balance and upsells available, " +
                    "WHEN confirming pre check-in, THEN skip upsells and proceed with check-in"
        )
        fun skipUpsellsForThirdPartyBookingWithoutBalance() = runTest {
            val preStayUiModel = mockk<PreStayUiModel>(relaxed = true) {
                every { isThirdPartyBooking } returns true
                every { isBusinessBooking } returns false
                every { preStayDetails.preCheckInStatus } returns false
                every { preStayDetails.rooms.size } returns 2
                every { preStayDetails.startDate } returns LocalDate.of(2025, 1, 18)
                every { preStayDetails.endDate } returns LocalDate.of(2025, 1, 19)
                every { preStayDetails.numberOfNights } returns 1
                every { preStayDetails.numberOfAdults } returns 2
                every { preStayDetails.numberOfChildren } returns 0
                every { preStayHeaderInfo.hotelBrand } returns "PI"
                every { preStayHeaderInfo.hotelImage } returns "hotel_image_url"
                every { outstandingBalance } returns PriceDomainParcelable(0f, "GBP")
                every { basketReference } returns "basket123"
                every { bookingReference } returns "BOOKING123"
                every { preStayDetails.hotelId } returns "HOTEL123"
                every { preStayDetails.bookerDetails.leadBookerFirstName } returns "John"
            }
            coEvery { isUpsellFlowEnabledUseCase() } returns true
            coEvery { confirmPreCheckInUseCase(any()) } returns flowOf(
                Result.Success(mockk(relaxed = true) {
                    every { basketStatus } returns "PRE_CHECKED_IN"
                })
            )
            preStayViewModel._state.update { it.copy(upsellItems = listOf(mockk())) }

            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            val allNavigations = stateResults.mapNotNull { it.navigation }
            assertTrue(allNavigations.isNotEmpty())
            assertFalse(allNavigations.any { it is PreStayViewModel.NavigationDestination.UpsellsFragment })
            assertTrue(allNavigations.last() is PreStayViewModel.NavigationDestination.CompletionScreen)
        }


        @Test
        @DisplayName(
            "GIVEN regular booking with outstanding balance and upsells available, " +
                    "WHEN confirming pre check-in, THEN navigate to UpsellsFragment"
        )
        fun showUpsellsForRegularBookingWithBalance() = runTest {
            val preStayUiModel =
                createPreStayUiModel(isThirdPartyBooking = false, isBusinessBooking = false)

            coEvery { isUpsellFlowEnabledUseCase() } returns true
            preStayViewModel._state.update { it.copy(upsellItems = listOf(mockk())) }

            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            val navigation = stateResults.last().navigation
            assertTrue(navigation is PreStayViewModel.NavigationDestination.UpsellsFragment)
        }

        @Test
        @DisplayName(
            "GIVEN third party booking with upsells disabled, " +
                    "WHEN confirming pre check-in, THEN skip upsells and navigate to PayAndCheckInFragment"
        )
        fun thirdPartyBookingWithUpsellsDisabled() = runTest {
            val preStayUiModel =
                createPreStayUiModel(isThirdPartyBooking = true, isBusinessBooking = false)

            coEvery { isUpsellFlowEnabledUseCase() } returns false
            preStayViewModel._state.update { it.copy(upsellItems = emptyList()) }

            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            val allNavigations = stateResults.mapNotNull { it.navigation }
            assertFalse(allNavigations.any { it is PreStayViewModel.NavigationDestination.UpsellsFragment })
            assertTrue(allNavigations.last() is PreStayViewModel.NavigationDestination.PayAndCheckInFragment)
        }
    }

    private fun createPreStayUiModel(
        isThirdPartyBooking: Boolean = false,
        isBusinessBooking: Boolean = false,
        outstandingBalance: PriceDomainParcelable = PriceDomainParcelable(100f, "GBP")
    ): PreStayUiModel = mockk(relaxed = true) {
        every { this@mockk.isThirdPartyBooking } returns isThirdPartyBooking
        every { this@mockk.isBusinessBooking } returns isBusinessBooking
        every { preStayDetails.preCheckInStatus } returns false
        every { preStayDetails.rooms.size } returns 2
        every { preStayHeaderInfo.hotelBrand } returns "PI"
        every { this@mockk.outstandingBalance } returns outstandingBalance
    }

    @Nested
    @DisplayName("hasNoPackages test suite")
    inner class HasNoPackagesTests {

        @Test
        @DisplayName("GIVEN empty preselectedRoomSelections, WHEN hasNoPackages is called, THEN should return true")
        fun emptyRoomSelectionsReturnsTrue() = runTest {
            // GIVEN
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = emptyList()) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertTrue(result)
        }

        @Test
        @DisplayName("GIVEN room with only wifi packages, WHEN hasNoPackages is called, THEN should return false")
        fun wifiOnlyReturnsFalse() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "FI24HR", noOfSelections = 1), // Ultimate WI-FI
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertFalse(result)
        }

        @Test
        @DisplayName("GIVEN room with wifi, donation and breakfast packages, WHEN hasNoPackages is called, THEN should return false")
        fun wifiDonationAndBreakfastReturnsFalse() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "FI24HR", noOfSelections = 1),// Ultimate WI-FI
                        PackagesSelectionDomain(id = "ZCHRY1", noOfSelections = 1),// DONATION_LIST
                        PackagesSelectionDomain(id = "BFADBF", noOfSelections = 1) // PI breakfast
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertFalse(result)
        }

        @Test
        @DisplayName("GIVEN room with only donation packages, WHEN hasNoPackages is called, THEN should return true")
        fun onlyDonationPackagesReturnsTrue() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "ZCHRY1", noOfSelections = 1), // DONATION_LIST
                        PackagesSelectionDomain(id = "ZCHRY2", noOfSelections = 2)  // DONATION_LIST
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertTrue(result)
        }

        @Test
        @DisplayName("GIVEN room with only CITY_TAX package, WHEN hasNoPackages is called, THEN should return true")
        fun onlyCityTaxPackageReturnsTrue() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "CITYTAX", noOfSelections = 1)
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertTrue(result)
        }

        @Test
        @DisplayName("GIVEN room with only ECI_LCO packages, WHEN hasNoPackages is called, THEN should return true")
        fun onlyEciLcoPackagesReturnsTrue() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "HSCKIN", noOfSelections = 1), // ECI_LCO
                        PackagesSelectionDomain(id = "HSCOU2", noOfSelections = 1)  // ECI_LCO
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertTrue(result)
        }

        @Test
        @DisplayName("GIVEN room with only HAS_TWIN_PACKAGE_CODE package, WHEN hasNoPackages is called, THEN should return true")
        fun onlyTwinPackageCodeReturnsTrue() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(
                            id = "HSATWN",
                            noOfSelections = 1
                        ) // HAS_TWIN_PACKAGE_CODE
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertTrue(result)
        }

        @Test
        @DisplayName("GIVEN multi rooms with a mix of excluded packages, WHEN hasNoPackages is called, THEN should return true")
        fun mixOfExcludedPackagesReturnsTrue() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "ZCHRY1", noOfSelections = 1), // DONATION_LIST
                        PackagesSelectionDomain(id = "CITYTAX", noOfSelections = 1),
                        PackagesSelectionDomain(id = "HSCKIN", noOfSelections = 1), // ECI_LCO
                        PackagesSelectionDomain(
                            id = "HSATWN",
                            noOfSelections = 1
                        )  // HAS_TWIN_PACKAGE_CODE
                    )
                ),
                RoomSelectionDomain(
                    reservationId = "RES002",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "ZCHRY7", noOfSelections = 1), // DONATION_LIST
                        PackagesSelectionDomain(id = "HSCOU2", noOfSelections = 1)  // ECI_LCO
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertTrue(result)
        }

        @Test
        @DisplayName("GIVEN room with a non-excluded package, WHEN hasNoPackages is called, THEN should return false")
        fun nonExcludedPackageReturnsFalse() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(
                            id = "BFADBF",
                            noOfSelections = 1
                        ) // PI breakfast
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertFalse(result)
        }

        @Test
        @DisplayName("GIVEN room with mix of excluded and non-excluded packages, WHEN hasNoPackages is called, THEN should return false")
        fun mixOfPackagesWithNonExcludedReturnsFalse() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "ZCHRY1", noOfSelections = 1), // DONATION_LIST
                        PackagesSelectionDomain(
                            id = "BFADBF",
                            noOfSelections = 1
                        ) // PI breakfast
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertFalse(result)
        }

        @Test
        @DisplayName("GIVEN multi rooms where one has non-excluded package, WHEN hasNoPackages is called, THEN should return false")
        fun multipleRoomsOneWithNonExcludedReturnsFalse() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "CITYTAX", noOfSelections = 1) // Excluded
                    )
                ),
                RoomSelectionDomain(
                    reservationId = "RES002",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(
                            id = "BFADBF",
                            noOfSelections = 1
                        ) // PI breakfast
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertFalse(result)
        }

        @Test
        @DisplayName("GIVEN room with empty packages list, WHEN hasNoPackages is called, THEN should return true")
        fun emptyPackagesListReturnsTrue() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = emptyList()
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            // WHEN
            val result = preStayViewModel.hasNoPackages()

            // THEN
            assertTrue(result)
        }
    }

    @Nested
    @DisplayName("shouldCheckInForPibaBooking test suite")
    inner class ShouldCheckInForPibaBookingTests {

        @Test
        @DisplayName("GIVEN PIBA_CNP booking with no packages, WHEN onConfirmPreCheckInOrContinueNavigation is called, THEN should confirm check-in and navigate to CompletionScreen")
        fun pibaCnpWithNoPackagesConfirmsCheckIn() = runTest {
            // GIVEN
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = emptyList()) } // No packages

            coEvery { confirmPreCheckInUseCase(any(), any()) } returns flowOf(
                Result.Success(mockk(relaxed = true) {
                    every { basketStatus } returns "PRE_CHECKED_IN"
                })
            )
            coEvery { isUpsellFlowEnabledUseCase() } returns false

            // WHEN
            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            // THEN
            coVerify { confirmPreCheckInUseCase("basket123", true) }
            val navigation = stateResults.last().navigation
            assertTrue(navigation is PreStayViewModel.NavigationDestination.CompletionScreen)
        }

        @Test
        @DisplayName("GIVEN PIBA_CNP business booking with packages, WHEN onConfirmPreCheckInOrContinueNavigation is called, THEN should confirm check-in")
        fun pibaCnpBusinessBookingWithPackagesConfirmsCheckIn() = runTest {
            // GIVEN
            val preStayUiModel = mockk<PreStayUiModel>(relaxed = true) {
                every { paymentOption } returns "PIBA_CNP"
                every { isBusinessBooking } returns true
                every { preStayDetails.preCheckInStatus } returns false
                every { preStayDetails.rooms.size } returns 2
                every { preStayHeaderInfo.hotelBrand } returns "PI"
                every { outstandingBalance } returns PriceDomainParcelable(100f, "GBP")
                every { basketReference } returns "basket123"
                every { bookingReference } returns "BOOKING123"
                every { preStayDetails.hotelId } returns "HOTEL123"
                every { preStayDetails.bookerDetails.leadBookerFirstName } returns "John"
                every { preStayDetails.startDate } returns LocalDate.of(2025, 1, 18)
                every { preStayDetails.endDate } returns LocalDate.of(2025, 1, 19)
                every { preStayDetails.numberOfNights } returns 1
                every { preStayDetails.numberOfAdults } returns 2
                every { preStayDetails.numberOfChildren } returns 0
                every { preStayHeaderInfo.hotelImage } returns "hotel_image_url"
                every { isThirdPartyBooking } returns false
            }
            // Has non-excluded packages
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "BREAKFAST", noOfSelections = 1)
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            coEvery { confirmPreCheckInUseCase(any(), any()) } returns flowOf(
                Result.Success(mockk(relaxed = true) {
                    every { basketStatus } returns "PRE_CHECKED_IN"
                })
            )
            coEvery { isUpsellFlowEnabledUseCase() } returns true

            // WHEN
            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            // THEN
            coVerify { confirmPreCheckInUseCase("basket123", true) }
            val navigation = stateResults.last().navigation
            assertTrue(navigation is PreStayViewModel.NavigationDestination.CompletionScreen)
        }

        @Test
        @DisplayName("GIVEN PIBA_CNP booking with packages and not business booking, WHEN onConfirmPreCheckInOrContinueNavigation is called, THEN should go to upsells screen")
        fun pibaCnpWithPackagesNotBusinessDoesNotConfirmPibaFlow() = runTest {
            // GIVEN
            val preStayUiModel = mockk<PreStayUiModel>(relaxed = true) {
                every { paymentOption } returns "PIBA_CNP"
                every { isBusinessBooking } returns false
                every { isThirdPartyBooking } returns false
                every { preStayDetails.preCheckInStatus } returns false
                every { preStayDetails.rooms.size } returns 2
                every { preStayHeaderInfo.hotelBrand } returns "PI"
                every { outstandingBalance } returns PriceDomainParcelable(100f, "GBP")
            }
            // Has non-excluded packages
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "BREAKFAST", noOfSelections = 1)
                    )
                )
            )
            preStayViewModel._state.update {
                it.copy(
                    preselectedRoomSelections = roomSelections,
                    upsellItems = listOf(mockk())
                )
            }

            coEvery { isUpsellFlowEnabledUseCase() } returns true

            // WHEN
            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            // THEN - should navigate to upsells instead of confirming PIBA check-in
            val navigation = stateResults.last().navigation
            assertTrue(navigation is PreStayViewModel.NavigationDestination.UpsellsFragment)
        }

        @Test
        @DisplayName("GIVEN CC paymentOption, WHEN onConfirmPreCheckInOrContinueNavigation is called, THEN should go to upsells screen")
        fun nonPibaCnpDoesNotTriggerPibaFlow() = runTest {
            // GIVEN
            val preStayUiModel = mockk<PreStayUiModel>(relaxed = true) {
                every { paymentOption } returns "CC"
                every { isBusinessBooking } returns false
                every { isThirdPartyBooking } returns false
                every { preStayDetails.preCheckInStatus } returns false
                every { preStayDetails.rooms.size } returns 2
                every { preStayHeaderInfo.hotelBrand } returns "PI"
                every { outstandingBalance } returns PriceDomainParcelable(100f, "GBP")
            }
            preStayViewModel._state.update {
                it.copy(
                    preselectedRoomSelections = emptyList(),
                    upsellItems = listOf(mockk())
                )
            }

            coEvery { isUpsellFlowEnabledUseCase() } returns true

            // WHEN
            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            // THEN - should navigate to upsells (normal flow, not PIBA)
            val navigation = stateResults.last().navigation
            assertTrue(navigation is PreStayViewModel.NavigationDestination.UpsellsFragment)
        }

        @Test
        @DisplayName("GIVEN PIBA_CP booking with no packages, AND feature_piba_cp_enabled is true, WHEN onConfirmPreCheckInOrContinueNavigation is called, THEN should go to pay and check-in screen")
        fun pibaCpWithNoPackagesAndFFTurnedOnFlow() = runTest {
            // GIVEN
            val preStayUiModel = mockk<PreStayUiModel>(relaxed = true) {
                every { paymentOption } returns "PIBA_CP" // Not PIBA_CNP
                every { isFeatureOn.invoke(Key.FEATURE_PIBA_CP_ENABLED) } returns true
                every { isBusinessBooking } returns false
                every { isThirdPartyBooking } returns false
                every { preStayDetails.preCheckInStatus } returns false
                every { preStayDetails.rooms.size } returns 2
                every { preStayHeaderInfo.hotelBrand } returns "PI"
                every { outstandingBalance } returns PriceDomainParcelable(100f, "GBP")
            }
            preStayViewModel._state.update {
                it.copy(
                    preselectedRoomSelections = emptyList(),
                    upsellItems = listOf(mockk()),
                    isPibaCpEnabled = true
                )
            }

            coEvery { isUpsellFlowEnabledUseCase() } returns true

            // WHEN
            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            // THEN - should navigate to upsells (normal flow)
            val navigation = stateResults.last().navigation
            assertTrue(navigation is PreStayViewModel.NavigationDestination.PayAndCheckInFragment)
        }

        @Test
        @DisplayName("GIVEN PIBA_CP booking with no packages, AND feature_piba_cp_enabled is false, WHEN onConfirmPreCheckInOrContinueNavigation is called, THEN should go to upsells page")
        fun pibaCpWithNoPackagesAndFFTurnedOffFlow() = runTest {
            // GIVEN
            val preStayUiModel = mockk<PreStayUiModel>(relaxed = true) {
                every { paymentOption } returns "PIBA_CP" // Not PIBA_CNP
                every { isFeatureOn.invoke(Key.FEATURE_PIBA_CP_ENABLED) } returns false
                every { isBusinessBooking } returns false
                every { isThirdPartyBooking } returns false
                every { preStayDetails.preCheckInStatus } returns false
                every { preStayDetails.rooms.size } returns 2
                every { preStayHeaderInfo.hotelBrand } returns "PI"
                every { outstandingBalance } returns PriceDomainParcelable(100f, "GBP")
            }
            preStayViewModel._state.update {
                it.copy(
                    preselectedRoomSelections = emptyList(),
                    upsellItems = listOf(mockk()),
                    isPibaCpEnabled = false
                )
            }

            coEvery { isUpsellFlowEnabledUseCase() } returns true

            // WHEN
            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            // THEN - should navigate to upsells (normal flow)
            val navigation = stateResults.last().navigation
            assertTrue(navigation is PreStayViewModel.NavigationDestination.UpsellsFragment)
        }

        @Test
        @DisplayName("GIVEN PIBA_CP booking with packages, WHEN onConfirmPreCheckInOrContinueNavigation is called, THEN should go to upsells screen")
        fun pibaCpWithPackagesFlow() = runTest {
            // GIVEN
            val preStayUiModel = mockk<PreStayUiModel>(relaxed = true) {
                every { paymentOption } returns "PIBA_CP" // Not PIBA_CNP
                every { isBusinessBooking } returns false
                every { isThirdPartyBooking } returns false
                every { preStayDetails.preCheckInStatus } returns false
                every { preStayDetails.rooms.size } returns 2
                every { preStayHeaderInfo.hotelBrand } returns "PI"
                every { outstandingBalance } returns PriceDomainParcelable(100f, "GBP")
            }

            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "BREAKFAST", noOfSelections = 1)
                    )
                )
            )
            preStayViewModel._state.update {
                it.copy(
                    preselectedRoomSelections = roomSelections,
                    upsellItems = listOf(mockk())
                )
            }

            coEvery { isUpsellFlowEnabledUseCase() } returns true

            // WHEN
            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            // THEN - should navigate to upsells (normal flow)
            val navigation = stateResults.last().navigation
            assertTrue(navigation is PreStayViewModel.NavigationDestination.UpsellsFragment)
        }

        @Test
        @DisplayName("GIVEN PIBA_CNP booking with only excluded packages, WHEN onConfirmPreCheckInOrContinueNavigation is called, THEN should confirm check-in for PIBA flow")
        fun pibaCnpWithOnlyExcludedPackagesConfirmsCheckIn() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "ZCHRY1", noOfSelections = 1), // DONATION_LIST
                        PackagesSelectionDomain(id = "CITYTAX", noOfSelections = 1)
                    )
                )
            )
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = roomSelections) }

            coEvery { confirmPreCheckInUseCase(any(), any()) } returns flowOf(
                Result.Success(mockk(relaxed = true) {
                    every { basketStatus } returns "PRE_CHECKED_IN"
                })
            )
            coEvery { isUpsellFlowEnabledUseCase() } returns false
            // WHEN
            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            // THEN
            coVerify { confirmPreCheckInUseCase("basket123", true) }
            val navigation = stateResults.last().navigation
            assertTrue(navigation is PreStayViewModel.NavigationDestination.CompletionScreen)
        }

        @Test
        @DisplayName("GIVEN PIBA_CNP booking with non excluded and excluded packages, WHEN onConfirmPreCheckInOrContinueNavigation is called, THEN should go to upsells screen ")
        fun pibaCnpWithNonExcludedAndExcludedPackagesNavigatesToUpsells() = runTest {
            // GIVEN
            val roomSelections = listOf(
                RoomSelectionDomain(
                    reservationId = "RES001",
                    packagesSelection = listOf(
                        PackagesSelectionDomain(id = "ZCHRY1", noOfSelections = 1),
                        PackagesSelectionDomain(id = "CITYTAX", noOfSelections = 1),
                        PackagesSelectionDomain(id = "BFADBF", noOfSelections = 1)
                    )
                )
            )
            preStayViewModel._state.update {
                it.copy(
                    preselectedRoomSelections = roomSelections,
                    upsellItems = listOf(mockk())
                )
            }


            coEvery { isUpsellFlowEnabledUseCase() } returns true
            // WHEN
            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            // THEN
            val navigation = stateResults.last().navigation
            assertTrue(navigation is PreStayViewModel.NavigationDestination.UpsellsFragment)
        }

        @Test
        @DisplayName("GIVEN PIBA_CNP booking with check-in error, WHEN onConfirmPreCheckInOrContinueNavigation is called, THEN should show error with isPibaCnp flag")
        fun pibaCnpCheckInErrorShowsErrorWithFlag() = runTest {
            // GIVEN
            preStayViewModel._state.update { it.copy(preselectedRoomSelections = emptyList()) }

            coEvery { confirmPreCheckInUseCase(any(), any()) } returns flowOf(
                Result.Error(DataError.Network.BaseError("check-in failed"))
            )
            coEvery { isUpsellFlowEnabledUseCase() } returns false

            // WHEN
            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(preStayUiModel)
            advanceUntilIdle()

            // THEN
            val error = stateResults.last().error
            assertTrue(error is PreStaySharedViewModel.Error.GenericError)
            assertTrue((error).isPibaCnp)
        }
    }
}
