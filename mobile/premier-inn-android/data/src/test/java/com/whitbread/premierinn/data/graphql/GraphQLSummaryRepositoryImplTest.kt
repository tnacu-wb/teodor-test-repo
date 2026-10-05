package com.whitbread.premierinn.data.graphql

import android.content.SharedPreferences
import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToCreateReservationGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToPaymentMethodsGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToSaveReservationGQL
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.CreateReservationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PaymentMethodsGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.SaveReservationWithAncillariesGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.data.utils.MainDispatcherRule
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.common.ANDROID_APPS_CHANNEL
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.common.LEISURE_FULL_NAME
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PreviousRoomsSelections
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ReservationPackage
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Reservations
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomRates
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomsSelections
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveReservationWithAncillariesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SelectedPackages
import com.whitbread.premierinn.domain.result.Result
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junitparams.JUnitParamsRunner
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFailsWith

@RunWith(JUnitParamsRunner::class)
class GraphQLSummaryRepositoryImplTest {

    // Sets the main coroutines dispatcher to a TestCoroutineDispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi = mockk()
    private val preferences: SharedPreferences = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphqlSummaryRepo: GraphQLSummaryRepositoryImpl = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesCreateReservation: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesSaveReservation: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesForPaymentMethodsAndBookingConfirmation: JSONObject = mockk(relaxed = true)
    private val authenticationRepository: AuthenticationRepository = mockk()
    private val isCustomerLoggedIn: IsCustomerLoggedIn = mockk()
    private val dispatchers: AppDispatchers = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    val CREATE_RESERVATION_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/create_reservation_success_gql.json"),
        CreateReservationGraphQLContract.CreateReservationData::class.java)

    val SAVE_RESERVATION_WITH_ANCILLARIES_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/save_reservation_with_ancillaries_success_gql.json"),
        SaveReservationWithAncillariesGraphQLContract.SaveReservationWithAncillariesData::class.java)

    val PAYMENT_METHODS_AND_BOOKING_CONFIRMATION = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/payment_methods_and_booking_confirmation_success_gql.json"),
        PaymentMethodsGraphQLContract.PaymentMethodsData::class.java)

    val SUMMARY_QUERY_STRING = "Summary query string"

    @Before
    fun setUp() {
        every { dispatchers.io } returns testDispatcher
        graphqlSummaryRepo = GraphQLSummaryRepositoryImpl(nonRxGraphQLServicesApi,
            fileDataProvider, jsonObject, jsonObjectForVariablesCreateReservation,
            jsonObjectForVariablesSaveReservation, jsonObjectForVariablesForPaymentMethodsAndBookingConfirmation,
            authenticationRepository, isCustomerLoggedIn, dispatchers)
        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns SUMMARY_QUERY_STRING
        every { preferences.getString(any(), any()) } returns " Some API key"
        every { jsonObject.toString() } returns SUMMARY_QUERY_STRING
    }

    @Test
    fun `Given Create reservation is successful then no error is thrown and mapping works`() = runTest {
        coEvery { nonRxGraphQLServicesApi.createReservationGraphQL(any(), any()) } returns CREATE_RESERVATION_SUCCESS
        val expectedCreateReservationResp = CREATE_RESERVATION_SUCCESS.mapToCreateReservationGQL().basketReference
        val mockInput = mockReservationInput()

        val result = graphqlSummaryRepo.createReservation(mockInput).toList()

        assertTrue(result.first() is Result.Success)
        assertEquals(expectedCreateReservationResp, (result.first() as Result.Success).data)
    }

    @Test
    fun `Given Create reservation contains Unauthorized Error then exception is thrown`() = runTest {
        coEvery { nonRxGraphQLServicesApi.createReservationGraphQL(any(), any()) } throws UnAuthorizedCustomerError
        val mockInput = mockReservationInput()

        val exception = assertFailsWith<UnAuthorizedCustomerError> {
            graphqlSummaryRepo.createReservation(mockInput).toList()
        }

        assertEquals(UnAuthorizedCustomerError, exception)
    }

    @Test
    fun `Given Save reservation is successful then no error is thrown and mapping works`() = runTest {
        coEvery { nonRxGraphQLServicesApi.saveReservationWithAncillariesGraphQL(any()) } returns SAVE_RESERVATION_WITH_ANCILLARIES_SUCCESS

        val expectedResp = SAVE_RESERVATION_WITH_ANCILLARIES_SUCCESS.mapToSaveReservationGQL()
        val mockInput = mockSaveReservationInput()

        val result = graphqlSummaryRepo.saveReservationWithAncillaries(mockInput).toList()

        assertTrue(result.first() is Result.Success)
        assertEquals(expectedResp, (result.first() as Result.Success).data)
    }

    @Test
    fun `Given Save reservation with ancillaries contains Error then return response as GraphQl Error`() = runTest {
        coEvery { nonRxGraphQLServicesApi.saveReservationWithAncillariesGraphQL(any()) } throws GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error"))

        val result = graphqlSummaryRepo.saveReservationWithAncillaries(mockSaveReservationInput()).toList()

        assertTrue(result.first() is Result.Error)
        assertTrue((result.first() as Result.Error).error is DataError.Network.GraphQlError)
    }

    @Test
    fun `Given Payment Methods and Booking Confirmation is successful then no error is thrown and mapping works`() = runTest {
        coEvery { nonRxGraphQLServicesApi.getPaymentMethods(any(), any()) } returns PAYMENT_METHODS_AND_BOOKING_CONFIRMATION
        val expectedCreateReservationResp = PAYMENT_METHODS_AND_BOOKING_CONFIRMATION.mapToPaymentMethodsGQL()
        val paymentMethodsRequestBody = PaymentMethodsRequestBody(
            "MANOLD6806399", "en", "gb", ANDROID_APPS_CHANNEL, LEISURE_FULL_NAME)

        val result = graphqlSummaryRepo.getPaymentMethodsAndBookingConfirmation(
            paymentMethodsRequestBody, "MANOLD6806399", "gb", "en", "BB").toList()

        assertTrue(result.first() is Result.Success)
        assertEquals(expectedCreateReservationResp, (result.first() as Result.Success).data)
    }

    @Test
    fun `Given Payment Methods and Booking Confirmation contains Unauthorized Error then exception is thrown`() = runTest {
        coEvery { nonRxGraphQLServicesApi.getPaymentMethods(any(), any()) } throws UnAuthorizedCustomerError
        val paymentMethodsRequestBody = PaymentMethodsRequestBody(
            "MANOLD6806399", "en", "gb", ANDROID_APPS_CHANNEL, LEISURE_FULL_NAME
        )

        val exception = assertFailsWith<UnAuthorizedCustomerError> {
            graphqlSummaryRepo.getPaymentMethodsAndBookingConfirmation(
                paymentMethodsRequestBody, "MANOLD6806399", "gb", "en", "BB").toList()
        }

        assertEquals(UnAuthorizedCustomerError, exception)
    }

    private fun mockReservationInput(): CreateReservationRequestBody {
        val mockInput = CreateReservationRequestBody(
            reservations = listOf(Reservations(
                "BANBRI", "2022-06-23", "2022-06-25",
                "NEWDRO3701317", 1, 0, false,
                RoomRates(
                    startDate = "2022-06-23",
                    endDate = "2022-06-25",
                    pmsRoomType = "DOUBLE",
                    specialRequests = listOf("SING", "LOWB"),
                    ratePlanCode = "FLEXRATE",
                    promotionCode = null,
                    promoKind = null),
                reservationPackages = listOf(ReservationPackage(100F, 1, "HSATWN", "2022-06-23", "2022-06-25"))
            )
            ), BookingChannelDetails("PI", "MOBILE", "en"))
        return mockInput
    }

    private fun mockSaveReservationInput(): SaveReservationWithAncillariesRequestBody {
        val listOfRoomSelections = mutableListOf<RoomsSelections>()
        val listOfPreviousRoomSelections = mutableListOf<PreviousRoomsSelections>()
        val listOfSelectedPackages1 = mutableListOf<SelectedPackages>()
        val listOfSelectedPackages2 = mutableListOf<SelectedPackages>()
        val selectedPackages1 = SelectedPackages("BFADBF", 1)
        val selectedPackages2 = SelectedPackages("MDP", 1)
        listOfSelectedPackages1.add(selectedPackages1)
        listOfSelectedPackages1.add(selectedPackages2)
        listOfSelectedPackages2.add(selectedPackages1)
        listOfSelectedPackages2.add(selectedPackages2)

        listOfRoomSelections.add(RoomsSelections(listOfSelectedPackages1))
        listOfRoomSelections.add(RoomsSelections(listOfSelectedPackages2))
        listOfPreviousRoomSelections.add(PreviousRoomsSelections(listOfSelectedPackages1))
        listOfPreviousRoomSelections.add(PreviousRoomsSelections(listOfSelectedPackages2))

        return SaveReservationWithAncillariesRequestBody(
            "MANOLD1234",
            "MANOLD",
            "2022-09-06",
            "2022-09-07",
            listOfRoomSelections,
            listOfPreviousRoomSelections
        )
    }
}