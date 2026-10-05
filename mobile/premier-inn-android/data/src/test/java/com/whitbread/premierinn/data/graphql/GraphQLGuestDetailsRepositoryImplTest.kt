package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToCreateReservationGuestGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToFormattedAddressGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToPartialAddressGQL
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.CreateReservationGuestGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.FormattedAddressGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PartialAddressGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PaymentMethodsGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Booker
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookerAddress
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PartialAddressRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuests
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)

class GraphQLGuestDetailsRepositoryImplTest {

    private val wbGraphQLServicesApi: WBGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphqlGuestDetailsRepo: GraphQLGuestDetailsRepositoryImpl = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesCreateReservation: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesPaymentMethodsAndDonation: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesPaymentMethods: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesForPaymentMethodsAndDonationWithBookingConfirmation: JSONObject = mockk(relaxed = true)

    val CREATE_RESERVATION_GUEST_SUCCESS = GsonFactory.create().fromJson(
            FileUtils.loadFileFromResource("api/graphql-responses/create_reservation_guest_success_gql.json"),
            CreateReservationGuestGraphQLContract.CreateReservationGuestData::class.java)
    val PAYMENT_METHODS_DONATION_AND_BOOKING_CONFIRMATION = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/payment_methods_with_donation_and_booking_confirmation_success_gql.json"),
        PaymentMethodsGraphQLContract.PaymentMethodsData::class.java)
    val PARTIAL_ADDRESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/partial_address_success_gql.json"),
        PartialAddressGraphQLContract.PartialAddressData::class.java)
    val FORAMTTED_ADDRESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/formatted_address_success_gql.json"),
        FormattedAddressGraphQLContract.FormattedAddressData::class.java)

    val GUEST_DETAILS_QUERY_STRING = "Guest details query string"

    @Before
    fun setUp() {
        graphqlGuestDetailsRepo = GraphQLGuestDetailsRepositoryImpl(wbGraphQLServicesApi, fileDataProvider,
                jsonObject, jsonObjectForVariablesCreateReservation,
            jsonObjectForVariablesPaymentMethodsAndDonation,
            jsonObjectForVariablesPaymentMethods)
        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns GUEST_DETAILS_QUERY_STRING
        every { jsonObject.toString() } returns GUEST_DETAILS_QUERY_STRING
    }

    @Test
    fun `Give Create reservation Guest is successful then no error is thrown and mapping works`() {
        every { wbGraphQLServicesApi.createReservationGuestGraphQL(any(), any()) } returns Single.just(CREATE_RESERVATION_GUEST_SUCCESS)

        val expectedCreateReservationGuestResp = CREATE_RESERVATION_GUEST_SUCCESS.mapToCreateReservationGuestGQL()

        val mockInput = mockGuestReservationInput()

        graphqlGuestDetailsRepo.createReservationGuest("token", mockInput).test()
                .assertNoErrors()
                .assertValue { it == expectedCreateReservationGuestResp }
    }

    @Test
    fun `Given Create reservation Guest contains Error then return response as GraphQl Error`() {
        every { wbGraphQLServicesApi.createReservationGuestGraphQL(any(),any()) }  returns Single.error(
                GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphqlGuestDetailsRepo.createReservationGuest("token", mockGuestReservationInput()).test()
                .assertError {it is GraphQLServerError }
    }

    private fun mockGuestReservationInput(): CreateReservationGuestRequestBody {
        val mockInput = CreateReservationGuestRequestBody(
            "BANBRI1234",
            "BANBRI",
            "LEI",
            Booker(
                "Mr",
                "First name",
                "lastname",
                "test@gql.com",
                "1234512345",
                true,
                BookerAddress(
                    addressLine1 = "Address line 1",
                    addressType = "HOME",
                    countryCode = "UK",
                    postalCode = "EC1N 2TD"
                )
            ),
            listOf(
                StayingGuests(
                    true,
                    StayingGuestDetails(
                        "",
                        "",
                        ""
                    )
                )
            ))
        return mockInput
    }

    @Test
    fun `Given Partial Address query is successful then no error is thrown and mapping works`() {
        every { wbGraphQLServicesApi.getPartialAddressGraphQL(any()) } returns Single.just(PARTIAL_ADDRESS)

        val expectedPartialAddress = PARTIAL_ADDRESS.mapToPartialAddressGQL("EC1N 2TD")

        val partialAddressRequestBody = PartialAddressRequestBody(
            "EC1N 2TD")

        graphqlGuestDetailsRepo.getPartialAddress(partialAddressRequestBody).test()
            .assertNoErrors()
            .assertValue { it == expectedPartialAddress }
    }

    @Test
    fun `Given  Partial Address query contains Error then return response as GraphQl Error`() {
        every { wbGraphQLServicesApi.getPartialAddressGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        val partialAddressRequestBody = PartialAddressRequestBody(
            "EC1N 2TD")

        graphqlGuestDetailsRepo.getPartialAddress(partialAddressRequestBody).test()
            .assertError {it is GraphQLServerError}
    }

    @Test
    fun `Given Formatted Address query is successful then no error is thrown and mapping works`() {
        every { wbGraphQLServicesApi.getFormattedAddressGraphQL(any()) } returns Single.just(FORAMTTED_ADDRESS)

        val expectedFormattedAddress = FORAMTTED_ADDRESS.mapToFormattedAddressGQL()

        graphqlGuestDetailsRepo.getFormattedAddress("someId").test()
            .assertNoErrors()
            .assertValue { it == expectedFormattedAddress }
    }

    @Test
    fun `Given  Formatted Address query contains Error then return response as GraphQl Error`() {
        every { wbGraphQLServicesApi.getFormattedAddressGraphQL(any()) } returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphqlGuestDetailsRepo.getFormattedAddress("someId").test()
            .assertError {it is GraphQLServerError}
    }

}