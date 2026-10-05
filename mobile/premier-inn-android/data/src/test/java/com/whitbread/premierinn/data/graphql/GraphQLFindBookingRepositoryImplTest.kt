package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToFindBookingDomain
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.FindBookingGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(JUnitParamsRunner::class)
class GraphQLFindBookingRepositoryImplTest {

    private val wbGraphQLServicesApi: WBGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphqlFindBookingRepo: GraphQLFindBookingRepositoryImpl = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesFindBooking: JSONObject = mockk(relaxed = true)

    val FIND_BOOKING_QUERY = "Find booking query"

    val FIND_BOOKING_OPERA_SUCCESS = GsonFactory.create().fromJson(
            FileUtils.loadFileFromResource("api/graphql-responses/find_booking_opera_success_gql.json"),
            FindBookingGraphQLContract.FindBookingData::class.java)
    val FIND_BOOKING_BART_SUCCESS = GsonFactory.create().fromJson(
            FileUtils.loadFileFromResource("api/graphql-responses/find_booking_bart_success_gql.json"),
            FindBookingGraphQLContract.FindBookingData::class.java)

    @Before
    fun setUp() {
        graphqlFindBookingRepo = GraphQLFindBookingRepositoryImpl(wbGraphQLServicesApi, fileDataProvider,
                jsonObject, jsonObjectForVariablesFindBooking)

        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns FIND_BOOKING_QUERY
        every { jsonObject.toString() } returns FIND_BOOKING_QUERY
    }

    @Test
    fun `Given Find booking is successful For an Opera booking then no error is thrown and mapping works`() {
        every { wbGraphQLServicesApi.findBookingGraphQL(any()) } returns Single.just(FIND_BOOKING_OPERA_SUCCESS)

        val expectedFindBookingResponse = FIND_BOOKING_OPERA_SUCCESS.mapToFindBookingDomain("MAH4107029")
        graphqlFindBookingRepo.findBooking(FindBookingRequestBody("MAH4107029", "Tester",
                "2023-04-01", "en", "gb",
            BookingChannelDetails("PI", "MOBILE", "en"))).test()
                .assertNoErrors()
                .assertValue { it == expectedFindBookingResponse }
                .assertValue { it.sourcePms == "Opera"}
                .assertValue { it.token == "CfiYrAs4Ks787uRmO+9A0HWnkl7/i"}
    }

    @Test
    fun `Given Find booking is successful For an Bart booking then no error is thrown and mapping works`() {
        every { wbGraphQLServicesApi.findBookingGraphQL(any()) } returns Single.just(FIND_BOOKING_BART_SUCCESS)

        val expectedFindBookingResponse = FIND_BOOKING_BART_SUCCESS.mapToFindBookingDomain("MAH4107029")
        graphqlFindBookingRepo.findBooking(FindBookingRequestBody("MAH4107029", "Tester",
                "2023-04-01", "en", "gb", BookingChannelDetails("PI", "MOBILE", "en"))).test()
                .assertNoErrors()
                .assertValue { it == expectedFindBookingResponse }
                .assertValue { it.sourcePms == "Bart"}
                .assertValue { it.token == null}
    }

    @Test
    fun `Given Initiate payment contains Error then return response as GraphQl Error`() {
        every { wbGraphQLServicesApi.findBookingGraphQL(any()) }  returns Single.error(
                GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphqlFindBookingRepo.findBooking(FindBookingRequestBody("MAH4107029", "Tester",
                "2023-04-01", "en", "gb", BookingChannelDetails("PI", "MOBILE", "en")  )).test()
                .assertError {it is GraphQLServerError }
    }
}