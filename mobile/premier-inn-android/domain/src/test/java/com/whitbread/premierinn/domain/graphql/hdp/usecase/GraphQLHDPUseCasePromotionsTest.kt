package com.whitbread.premierinn.domain.graphql.hdp.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.hdp.entity.DailyPriceDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RateClassificationsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomOptionsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomPriceBreakdownDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomRateDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomTypeDomain
import com.whitbread.premierinn.domain.graphql.hdp.repository.GraphQLHDPRepository
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromoBoxDomain
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain.Companion.PROMO_KIND_SITE_WIDE
import com.whitbread.premierinn.domain.graphql.promotions.usecase.GraphQLPromotionsInformationUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelInfoDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomSearch
import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GraphQLHDPUseCasePromotionsTest {

    private companion object {
        const val TEST_LANGUAGE = "EN"
        const val TEST_COUNTRY = "GB"
        const val TEST_HOTEL_ID = "H1"
        const val TEST_CHANNEL = "PI"
        const val TEST_ARRIVAL_DATE = "2026-01-01"
        const val TEST_DEPARTURE_DATE = "2026-01-02"

        // PromoBox Status Constants
        const val STATUS_SUCCESS = "SUCCESS"
        const val STATUS_INVALID = "INVALID"
        const val STATUS_EXPIRED = "EXPIRED"
        const val STATUS_CODE_EXPIRED = "CODE_EXPIRED"
        const val STATUS_CODE_ALREADY_APPLIED = "CODE_ALREADY_APPLIED"
        const val STATUS_MULTIPLE_REDEEM = "MULTIPLE_REDEEM"
        const val STATUS_UNAVAILABLE = "UNAVAILABLE"

        // Test Promo Codes
        const val TEST_DISCOUNT_CODE = "DISC"
        const val TEST_SITEWIDE_CODE = "SITE"
        const val TEST_UNIQUE_CODE = "UNIQUE123"
        const val TEST_GENERIC_CODE = "GENERIC10"
        const val TEST_APP_INCENTIVE_CODE = "APPINCENTIVE123"
        const val TEST_FREE_BREAKFAST_CODE = "FREEBREAKFAST"

        // Promo Kind Constants
        const val PROMO_KIND_UNIQUE = "UNIQUE"
        const val PROMO_KIND_GENERIC = "GENERIC"

        // PromoBox Default Messages
        const val MSG_WHEN_INVALID = "Invalid code, please check and try again"
        const val MSG_WHEN_MULTIPLE_REDEEM = "Only one promo code can be used per booking"
        const val MSG_WHEN_SUCCESS = "Promo code applied"
        const val MSG_WHEN_EMPTY = "Promo code is required"
        const val MSG_WHEN_CODE_ALREADY_APPLIED = "Promo code already applied"
        const val MSG_WHEN_UNAVAILABLE = "Promo code unavailable for this stay"
        const val MSG_WHEN_CODE_EXPIRED = "The promo code has expired"

        // Other Constants
        const val ROOM_TYPE_DB = "DB"
        const val SUB_CHANNEL_APP = "APP"
    }

    private val graphQLHDPRepository = mockk<GraphQLHDPRepository>()
    private val authenticationRepository = mockk<AuthenticationRepository>(relaxed = true)
    private val getFreshIdTokenAndRetryOnce = mockk<GetFreshIdTokenAndRetryOnce>(relaxed = true)
    private val isCustomerLoggedIn = mockk<IsCustomerLoggedIn>()
    private val promotionsUseCase = mockk<GraphQLPromotionsInformationUseCase>()

    private lateinit var useCase: GraphQLHDPUseCase


    @Before
    fun setUp() {
        RxJavaPlugins.reset()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }

        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns false
        every {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), any(), any()
            )
        } returns Single.just(availabilityDomain())

        useCase = GraphQLHDPUseCase(
            graphQLHDPRepository,
            authenticationRepository,
            getFreshIdTokenAndRetryOnce,
            isCustomerLoggedIn,
            promotionsUseCase
        )
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    @Test
    fun `sitewide promotion takes priority over discount code`() {
        // Mock sitewide promo check
        mockSitewidePromoCheck(
            promoResponse(
                showPromo = true,
                promotionCode = TEST_SITEWIDE_CODE,
                promoKind = PROMO_KIND_SITE_WIDE
            )
        )

        val testObserver = executeUseCase(promoCode = TEST_DISCOUNT_CODE)

        testObserver.assertNoErrors()
        testObserver.assertComplete()

        val values = testObserver.values()
        assertTrue(values.isNotEmpty(), "Expected at least one emission")

        val state =
            values.last { it.action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess }
        assertTrue(state.sitewidePromoActive)
        assertEquals(TEST_SITEWIDE_CODE, state.getPromoCodeFromAvailability)

        // Verify no error states were emitted
        val errorStates =
            values.filter { it.action is GraphQLHDPUseCase.AvailabilityAction.AvailabilityError }
        assertTrue(errorStates.isEmpty(), "No error states should be emitted")

        verifySitewidePromoCheckCalled()
        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), TEST_SITEWIDE_CODE, any()
            )
        }
    }

    @Test
    fun `discount code is validated when no sitewide promo`() {
        // Mock sitewide check - no promo
        mockSitewidePromoCheck(promoResponse(showPromo = false, promotionCode = null))

        // Mock discount code validation
        mockDiscountCodeValidation(
            TEST_DISCOUNT_CODE,
            promoResponse(
                showPromo = false,
                promotionCode = null,
                promoBoxStatus = STATUS_SUCCESS
            )
        )

        val testObserver = executeUseCase(promoCode = TEST_DISCOUNT_CODE)

        testObserver.assertNoErrors()
        testObserver.assertComplete()

        val values = testObserver.values()
        assertTrue(values.isNotEmpty(), "Expected at least one emission")

        val state =
            values.last { it.action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess }
        assertFalse(state.sitewidePromoActive)
        assertEquals(TEST_DISCOUNT_CODE, state.getPromoCodeFromAvailability)

        // Verify no error states were emitted
        val errorStates =
            values.filter { it.action is GraphQLHDPUseCase.AvailabilityAction.AvailabilityError }
        assertTrue(errorStates.isEmpty(), "No error states should be emitted")

        verifySitewidePromoCheckCalled()
        verifyDiscountCodeValidationCalled(TEST_DISCOUNT_CODE)
        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), TEST_DISCOUNT_CODE, any()
            )
        }
    }

    @Test
    fun `invalid discount code falls back to no promo`() {
        // Mock sitewide check - no promo
        mockSitewidePromoCheck(promoResponse(showPromo = false, promotionCode = null))

        // Mock discount code validation - invalid
        val response = promoResponse(
            showPromo = false,
            promotionCode = null,
            promoBoxStatus = STATUS_INVALID
        )

        mockDiscountCodeValidation(TEST_DISCOUNT_CODE, response)

        val testObserver = executeUseCase(promoCode = TEST_DISCOUNT_CODE)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(EMPTY_STRING_DOMAIN, state.getPromoCodeFromAvailability)
        assertEquals(MSG_WHEN_INVALID, state.invalidDiscountCodeMessage)

        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), isNull(), any()
            )
        }
    }

    @Test
    fun `no sitewide and blank discount code fetches availability without promo`() {
        mockSitewidePromoCheck(promoResponse(showPromo = false, promotionCode = null))

        val testObserver = executeUseCase(promoCode = null)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(EMPTY_STRING_DOMAIN, state.getPromoCodeFromAvailability)

        verifySitewidePromoCheckCalled()
        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), isNull(), any()
            )
        }
    }

    @Test
    fun `sitewide call error falls back to discount code`() {
        mockSitewidePromoCheckError(RuntimeException("sitewide error"))

        mockDiscountCodeValidation(
            TEST_DISCOUNT_CODE,
            promoResponse(
                showPromo = false,
                promotionCode = null,
                promoBoxStatus = STATUS_SUCCESS
            )
        )

        val testObserver = executeUseCase(promoCode = TEST_DISCOUNT_CODE)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(TEST_DISCOUNT_CODE, state.getPromoCodeFromAvailability)

        verifySitewidePromoCheckCalled()
        verifyDiscountCodeValidationCalled(TEST_DISCOUNT_CODE)
        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), TEST_DISCOUNT_CODE, any()
            )
        }
    }

    @Test
    fun `sitewide promo outside window is not used`() {
        mockSitewidePromoCheck(
            promoResponse(
                showPromo = true,
                promotionCode = "EXPIRED",
                promoKind = PROMO_KIND_SITE_WIDE,
                isWithinPromoWindow = false
            )
        )

        val testObserver = executeUseCase(promoCode = null)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(EMPTY_STRING_DOMAIN, state.getPromoCodeFromAvailability)

        verifySitewidePromoCheckCalled()
        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), isNull(), any()
            )
        }
    }

    @Test
    fun `sitewide promo with showPromo false is not used even if has code`() {
        mockSitewidePromoCheck(
            promoResponse(
                showPromo = false,  // Key: showPromo is false
                promotionCode = "FAKE",
                promoKind = PROMO_KIND_SITE_WIDE,
                isWithinPromoWindow = true
            )
        )

        val testObserver = executeUseCase(promoCode = null)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(EMPTY_STRING_DOMAIN, state.getPromoCodeFromAvailability)

        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), isNull(), any()
            )
        }
    }

    @Test
    fun `sitewide promo with null promoKind is not used`() {
        mockSitewidePromoCheck(
            promoResponse(
                showPromo = true,
                promotionCode = "CODE",
                promoKind = null,  // Key: null promoKind
                isWithinPromoWindow = true
            )
        )

        val testObserver = useCase.fetchPromotionsAndHotelAvailability(
            requestBody(),
            language = "EN",
            country = "GB",
            hotelId = "H1",
            channel = "PI",
            promoCode = null
        ).test()

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(EMPTY_STRING_DOMAIN, state.getPromoCodeFromAvailability)

        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), isNull(), any()
            )
        }
    }

    @Test
    fun `sitewide promo with null promotionCode is not used`() {
        mockSitewidePromoCheck(
            promoResponse(
                showPromo = true,
                promotionCode = null,
                promoKind = PROMO_KIND_SITE_WIDE,
                isWithinPromoWindow = true
            )
        )

        val testObserver = useCase.fetchPromotionsAndHotelAvailability(
            requestBody(),
            language = "EN",
            country = "GB",
            hotelId = "H1",
            channel = "PI",
            promoCode = null
        ).test()

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(EMPTY_STRING_DOMAIN, state.getPromoCodeFromAvailability)

        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), isNull(), any()
            )
        }
    }

    @Test
    fun `discount code validation error falls back to no promo`() {
        mockSitewidePromoCheck(promoResponse(showPromo = false, promotionCode = null))

        mockDiscountCodeValidationError(
            TEST_DISCOUNT_CODE,
            RuntimeException("Validation API error")
        )

        val testObserver = executeUseCase(promoCode = TEST_DISCOUNT_CODE)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(EMPTY_STRING_DOMAIN, state.getPromoCodeFromAvailability)

        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), isNull(), any()
            )
        }
    }

    @Test
    fun `sitewide error with no discount code falls back to no promo`() {
        mockSitewidePromoCheckError(RuntimeException("API error"))

        val testObserver = executeUseCase(promoCode = null)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(EMPTY_STRING_DOMAIN, state.getPromoCodeFromAvailability)

        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), isNull(), any()
            )
        }
    }

    @Test
    fun `skipPromotionsCheck true with app incentive code bypasses promotions API`() {
        val testObserver = executeUseCase(
            promoCode = TEST_APP_INCENTIVE_CODE,
            skipPromotionsCheck = true
        )

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(TEST_APP_INCENTIVE_CODE, state.getPromoCodeFromAvailability)

        // Verify promotions API was NOT called
        verify(exactly = 0) { promotionUseCaseMatcherSitewide() }
        verify(exactly = 0) { promotionUseCaseMatcherDiscountCode(any()) }

        // Verify availability was fetched with the app incentive code and null promoKind
        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), TEST_APP_INCENTIVE_CODE, isNull()
            )
        }
    }

    @Test
    fun `skipPromotionsCheck true with free breakfast code bypasses promotions API`() {
        val testObserver = executeUseCase(
            promoCode = TEST_FREE_BREAKFAST_CODE,
            skipPromotionsCheck = true
        )

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(TEST_FREE_BREAKFAST_CODE, state.getPromoCodeFromAvailability)

        // Verify promotions API was NOT called
        verify(exactly = 0) { promotionUseCaseMatcherSitewide() }
        verify(exactly = 0) { promotionUseCaseMatcherDiscountCode(any()) }

        // Verify availability was fetched with the free breakfast code and null promoKind
        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), TEST_FREE_BREAKFAST_CODE, isNull()
            )
        }
    }

    @Test
    fun `skipPromotionsCheck true with null code still bypasses promotions API`() {
        val testObserver = executeUseCase(promoCode = null, skipPromotionsCheck = true)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(EMPTY_STRING_DOMAIN, state.getPromoCodeFromAvailability)

        // Verify promotions API was NOT called
        verify(exactly = 0) { promotionUseCaseMatcherSitewide() }
        verify(exactly = 0) { promotionUseCaseMatcherDiscountCode(any()) }

        // Verify availability was fetched without promo code and null promoKind
        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), isNull(), isNull()
            )
        }
    }

    @Test
    fun `sitewide promo with valid SITE_WIDE kind and all required fields is used`() {
        mockSitewidePromoCheck(
            promoResponse(
                showPromo = true,
                promotionCode = "SITEWIDE2024",
                promoKind = PROMO_KIND_SITE_WIDE,
                isWithinPromoWindow = true
            )
        )

        val testObserver = executeUseCase(promoCode = null)

        val state = verifySuccessState(testObserver)

        assertTrue(state.sitewidePromoActive)
        assertEquals("SITEWIDE2024", state.getPromoCodeFromAvailability)

        verifySitewidePromoCheckCalled()
        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), "SITEWIDE2024", PROMO_KIND_SITE_WIDE
            )
        }
    }

    @Test
    fun `discount code with UNIQUE promo kind is validated and used`() {
        mockSitewidePromoCheck(promoResponse(showPromo = false, promotionCode = null))

        mockDiscountCodeValidation(
            TEST_UNIQUE_CODE,
            promoResponse(
                showPromo = false,
                promotionCode = null,
                promoBoxStatus = STATUS_SUCCESS,
                promoKind = PROMO_KIND_UNIQUE
            )
        )

        val testObserver = executeUseCase(promoCode = TEST_UNIQUE_CODE)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(TEST_UNIQUE_CODE, state.getPromoCodeFromAvailability)

        verifySitewidePromoCheckCalled()
        verifyDiscountCodeValidationCalled(TEST_UNIQUE_CODE)
        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), TEST_UNIQUE_CODE, PROMO_KIND_UNIQUE
            )
        }
    }

    @Test
    fun `discount code with GENERIC promo kind is validated and used`() {
        mockSitewidePromoCheck(promoResponse(showPromo = false, promotionCode = null))

        mockDiscountCodeValidation(
            TEST_GENERIC_CODE,
            promoResponse(
                showPromo = false,
                promotionCode = null,
                promoBoxStatus = STATUS_SUCCESS,
                promoKind = PROMO_KIND_GENERIC
            )
        )

        val testObserver = executeUseCase(promoCode = TEST_GENERIC_CODE)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(TEST_GENERIC_CODE, state.getPromoCodeFromAvailability)

        verifySitewidePromoCheckCalled()
        verifyDiscountCodeValidationCalled(TEST_GENERIC_CODE)
        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), TEST_GENERIC_CODE, PROMO_KIND_GENERIC
            )
        }
    }

    @Test
    fun `sitewide promo with blank promotionCode is not used`() {
        mockSitewidePromoCheck(
            promoResponse(
                showPromo = true,
                promotionCode = "",  // Blank code
                promoKind = PROMO_KIND_SITE_WIDE,
                isWithinPromoWindow = true
            )
        )

        val testObserver = executeUseCase(promoCode = null)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(EMPTY_STRING_DOMAIN, state.getPromoCodeFromAvailability)

        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), isNull(), any()
            )
        }
    }

    @Test
    fun `invalid discount code shows error message from API`() {
        testDiscountCodeErrorScenario(
            promoCode = "BADCODE",
            promoBoxStatus = STATUS_EXPIRED
        )
    }

    @Test
    fun `CODE_ALREADY_APPLIED status shows backend error message`() {
        testDiscountCodeErrorScenario(
            promoCode = "ALREADYUSED",
            promoBoxStatus = STATUS_CODE_ALREADY_APPLIED
        )
    }

    @Test
    fun `MULTIPLE_REDEEM status shows backend error message`() {
        testDiscountCodeErrorScenario(
            promoCode = "MULTIPLE",
            promoBoxStatus = STATUS_MULTIPLE_REDEEM
        )
    }

    @Test
    fun `UNAVAILABLE status shows backend error message`() {
        testDiscountCodeErrorScenario(
            promoCode = "UNAVAIL",
            promoBoxStatus = STATUS_UNAVAILABLE
        )
    }

    @Test
    fun `CODE_EXPIRED status shows backend error message`() {
        testDiscountCodeErrorScenario(
            promoCode = "OLDCODE",
            promoBoxStatus = STATUS_CODE_EXPIRED
        )
    }

    @Test
    fun `INVALID status shows backend error message`() {
        testDiscountCodeErrorScenario(
            promoCode = "INVALIDCODE",
            promoBoxStatus = STATUS_INVALID
        )
    }

    @Test
    fun `promoBox messages are included in response with valid status`() {
        mockSitewidePromoCheck(promoResponse(showPromo = false, promotionCode = null))

        val promoResponse = promoResponse(
            showPromo = false,
            promotionCode = null,
            promoBoxStatus = STATUS_INVALID,
            invalidMessage = "Custom invalid message"
        )

        assertEquals("Custom invalid message", promoResponse.promoBox?.whenInvalid)
        assertEquals(MSG_WHEN_MULTIPLE_REDEEM, promoResponse.promoBox?.whenMultipleRedeem)
        assertEquals(MSG_WHEN_SUCCESS, promoResponse.promoBox?.whenSuccess)
        assertEquals(MSG_WHEN_EMPTY, promoResponse.promoBox?.whenEmpty)
        assertEquals(MSG_WHEN_CODE_ALREADY_APPLIED, promoResponse.promoBox?.whenCodeAlreadyApplied)
        assertEquals(MSG_WHEN_UNAVAILABLE, promoResponse.promoBox?.whenUnavailable)
        assertEquals("Custom invalid message", promoResponse.promoBox?.whenCodeExpired)
    }

    @Test
    fun `promoBox is null when promoBoxStatus is null`() {
        val promoResponse = promoResponse(
            showPromo = false,
            promotionCode = null,
            promoBoxStatus = null
        )
        assertEquals(null, promoResponse.promoBox)
    }

    private fun requestBody(): HotelAvailabilityRequestBody {
        return HotelAvailabilityRequestBody(
            arrival = TEST_ARRIVAL_DATE,
            departure = TEST_DEPARTURE_DATE,
            hotel = HotelInfoDetails(TEST_HOTEL_ID),
            rooms = listOf(RoomSearch(2, 0, false, ROOM_TYPE_DB)),
            bookingChannel = BookingChannelDetails(TEST_CHANNEL, SUB_CHANNEL_APP, TEST_LANGUAGE),
            brand = TEST_CHANNEL,
            ratePlanCodes = null,
            companyId = null
        )
    }

    private fun executeUseCase(
        promoCode: String? = null,
        skipPromotionsCheck: Boolean = false
    ): TestObserver<HotelBookingAvailabilityState> {
        return useCase.fetchPromotionsAndHotelAvailability(
            requestBody(),
            language = TEST_LANGUAGE,
            country = TEST_COUNTRY,
            hotelId = TEST_HOTEL_ID,
            channel = TEST_CHANNEL,
            promoCode = promoCode,
            skipPromotionsCheck = skipPromotionsCheck
        ).test()
    }

    private fun verifySuccessState(testObserver: TestObserver<HotelBookingAvailabilityState>): HotelBookingAvailabilityState {
        testObserver.assertNoErrors()
        testObserver.assertComplete()

        val values = testObserver.values()
        assertTrue(values.isNotEmpty(), "Expected at least one emission")

        // Verify no error states were emitted
        val errorStates =
            values.filter { it.action is GraphQLHDPUseCase.AvailabilityAction.AvailabilityError }
        assertTrue(errorStates.isEmpty(), "No error states should be emitted")

        return values.last { it.action is GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess }
    }

    private fun promotionUseCaseMatcherSitewide() = promotionsUseCase.execute(
        country = TEST_COUNTRY,
        language = TEST_LANGUAGE,
        channel = TEST_CHANNEL,
        brand = TEST_CHANNEL,
        stayStartDate = TEST_ARRIVAL_DATE,
        stayEndDate = TEST_DEPARTURE_DATE,
        basketReference = null,
        promotionCode = null,
        isPromoBox = false
    )

    private fun promotionUseCaseMatcherDiscountCode(code: String) = promotionsUseCase.execute(
        country = TEST_COUNTRY,
        language = TEST_LANGUAGE,
        channel = TEST_CHANNEL,
        brand = TEST_CHANNEL,
        stayStartDate = TEST_ARRIVAL_DATE,
        stayEndDate = TEST_DEPARTURE_DATE,
        basketReference = EMPTY_STRING_DOMAIN,
        promotionCode = code,
        isPromoBox = true
    )

    private fun mockSitewidePromoCheck(response: PromotionsInformationDomain) {
        every { promotionUseCaseMatcherSitewide() } returns Single.just(response)
    }

    private fun mockSitewidePromoCheckError(error: Throwable) {
        every { promotionUseCaseMatcherSitewide() } returns Single.error(error)
    }

    private fun mockDiscountCodeValidation(code: String, response: PromotionsInformationDomain) {
        every { promotionUseCaseMatcherDiscountCode(code) } returns Single.just(response)
    }

    private fun mockDiscountCodeValidationError(code: String, error: Throwable) {
        every { promotionUseCaseMatcherDiscountCode(code) } returns Single.error(error)
    }

    private fun verifySitewidePromoCheckCalled() {
        verify(exactly = 1) { promotionUseCaseMatcherSitewide() }
    }

    private fun verifyDiscountCodeValidationCalled(code: String) {
        verify(exactly = 1) { promotionUseCaseMatcherDiscountCode(code) }
    }

    private fun testDiscountCodeErrorScenario(
        promoCode: String,
        promoBoxStatus: String
    ) {
        mockSitewidePromoCheck(promoResponse(showPromo = false, promotionCode = null))

        val response = promoResponse(
            showPromo = false,
            promotionCode = null,
            promoBoxStatus = promoBoxStatus
        )

        mockDiscountCodeValidation(promoCode, response)

        val testObserver = executeUseCase(promoCode = promoCode)

        val state = verifySuccessState(testObserver)

        assertFalse(state.sitewidePromoActive)
        assertEquals(EMPTY_STRING_DOMAIN, state.getPromoCodeFromAvailability)
        // Verify the correct message field is used based on the status (using PromotionsInformationDomain.resolveMessageForStatus)
        val expectedMessage = PromotionsInformationDomain.resolveMessageForStatus(
            promoBoxStatus,
            response.promoBox
        )
        assertEquals(expectedMessage, state.invalidDiscountCodeMessage)

        verify(exactly = 1) {
            graphQLHDPRepository.getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
                any(), any(), any(), any(), any(), any(), isNull(), any()
            )
        }
    }

    private fun promoResponse(
        showPromo: Boolean,
        promotionCode: String?,
        promoBoxStatus: String? = null,
        promoKind: String? = null,
        invalidMessage: String? = null,
        isWithinPromoWindow: Boolean = true
    ): PromotionsInformationDomain {
        val promoBox = if (promoBoxStatus != null) {
            PromoBoxDomain(
                title = "Enter promo code",
                button = "Apply",
                whenInvalid = invalidMessage ?: MSG_WHEN_INVALID,
                whenMultipleRedeem = MSG_WHEN_MULTIPLE_REDEEM,
                whenSuccess = MSG_WHEN_SUCCESS,
                whenEmpty = MSG_WHEN_EMPTY,
                whenCodeAlreadyApplied = MSG_WHEN_CODE_ALREADY_APPLIED,
                whenUnavailable = MSG_WHEN_UNAVAILABLE,
                whenCodeExpired = invalidMessage ?: MSG_WHEN_CODE_EXPIRED
            )
        } else null

        return PromotionsInformationDomain(
            showPromo = showPromo,
            isWithinPromoWindow = isWithinPromoWindow,
            promotionCode = promotionCode,
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
            promoKind = promoKind,
            promoBoxStatus = promoBoxStatus,
            promoBoxMessageKey = null
        )
    }

    private fun availabilityDomain(): HotelAvailabilityDomain {
        val priceBreakdown = RoomPriceBreakdownDomain(
            currencyCode = "GBP",
            dailyPricesDomainList = listOf(DailyPriceDomain("2026-01-01", 120.0)),
            totalNetAmount = 120.0,
            packageCode = null,
            packageAmount = null,
            baseRateAmount = null
        )
        val roomOptions = RoomOptionsDomain(
            cotAvailable = false,
            pmsRoomType = "DB",
            roomPriceBreakdownDomain = priceBreakdown,
            roomClass = "STD",
            silentSubstitution = false,
            specialRequests = emptyList()
        )
        val roomType = RoomTypeDomain(
            adults = 2,
            children = 0,
            cotRequested = false,
            roomType = "DB",
            roomOptionsDomainList = listOf(roomOptions)
        )
        val roomRate = RoomRateDomain(
            ratePlanCode = "FLEX",
            cellCode = EMPTY_STRING_DOMAIN,
            promotionCode = null,
            roomTypesDomainList = listOf(roomType)
        )
        val rateClassification = RateClassificationsDomain(
            rateClassification = "FLEX",
            rateOrder = "1",
            rateName = "Flex",
            rateDescription = "desc",
            rateLongDescription = EMPTY_STRING_DOMAIN,
            rateNotes = EMPTY_STRING_DOMAIN,
            rateTags = emptyList()
        )

        return HotelAvailabilityDomain(
            available = true,
            hotelId = "H1",
            endDate = "2026-01-02",
            startDate = "2026-01-01",
            limitedAvailability = false,
            roomRateDomainList = listOf(roomRate),
            packages = null,
            listOfRatesClassification = listOf(rateClassification),
            listOfRoomTypeInfo = emptyList(),
            error = null
        )
    }
}