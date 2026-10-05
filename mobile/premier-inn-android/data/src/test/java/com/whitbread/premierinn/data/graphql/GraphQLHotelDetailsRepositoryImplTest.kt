package com.whitbread.premierinn.data.graphql

import android.content.SharedPreferences
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelAvailabilityGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelInformationGQL
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelAvailabilityGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelInfoGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelInfoDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomSearch
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(JUnitParamsRunner::class)
class GraphQLHotelDetailsRepositoryImplTest {
    private val graphQlApi: WBGraphQLServicesApi = mockk()
    private val preferences: SharedPreferences = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private val businessPersistenceManager: BusinessPersistenceManager = mockk()
    private var graphQLHotelDetailsRepository: GraphQLHotelDetailsRepository = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesHotelInfo: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesHotelAvailability: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesGetHotelAvailableCheck: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesHotelDisclaimer: JSONObject = mockk(relaxed = true)

    val HOTEL_INFO_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/hotel_info_success_gql.json"),
        HotelInfoGraphQLContract.HotelInfoData::class.java)
    val HOTEL_AVAILABILITY_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/hotel_availability_success_gql.json"),
        HotelAvailabilityGraphQLContract.HotelAvailabilityData::class.java)
    val HOTEL_DETAILS_QUERY_STRING = "Hotel info query"

    @Before
    fun setUp() {
        graphQLHotelDetailsRepository = GraphQLHotelDetailsRepositoryImpl(graphQlApi, fileDataProvider,
                businessPersistenceManager, jsonObject, jsonObjectForVariablesHotelInfo, jsonObjectForVariablesHotelAvailability,
            jsonObjectForVariablesGetHotelAvailableCheck, jsonObjectForVariablesHotelDisclaimer)

        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns HOTEL_DETAILS_QUERY_STRING
        every { preferences.getString(any(), any()) } returns " Some API key"
        every { jsonObject.toString() } returns HOTEL_DETAILS_QUERY_STRING
    }

    @Test
    fun `Given Hotel info is successful then no error is thrown and mapping works`() {
        every { graphQlApi.getHotelInformationGraphQL(any()) } returns Single.just(HOTEL_INFO_SUCCESS)

        val expectedHotelInfoDomain = HOTEL_INFO_SUCCESS.mapToHotelInformationGQL()

        graphQLHotelDetailsRepository.getHotelInfo("gb", "MANOLD", "en").test()
            .assertNoErrors()
            .assertValue { it == expectedHotelInfoDomain }
    }

    @Test
    fun `Given Hotel info contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getHotelInformationGraphQL(any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphQLHotelDetailsRepository.getHotelInfo("gb", "MANOLD", "en").test()
            .assertError {it is GraphQLServerError}
    }

    @Test
    fun `Given Only Hotel availability req being made is successful then no error is thrown and mapping works`() {
        every { graphQlApi.getHotelAvailabilityGraphQL(any(), any()) } returns Single.just(HOTEL_AVAILABILITY_SUCCESS)
        val mockAvailabilityRequestBody =  HotelAvailabilityRequestBody("2022-10-04", "2022-10-05", HotelInfoDetails("TKKINPT"),
                                     Collections.singletonList(RoomSearch(2, 0, true, "DB")),
                BookingChannelDetails("PI", "MOBILE", "en"), "pi", emptyList(), "")
        val expectedHotelAvailability = HOTEL_AVAILABILITY_SUCCESS.mapToHotelAvailabilityGQL()

        graphQLHotelDetailsRepository.checkIfHotelAvailable(mockAvailabilityRequestBody,"").test()
            .assertNoErrors()
            .assertValue { it == expectedHotelAvailability }
    }

    @Test
    fun `Given Only Hotel availability req being made contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getHotelAvailabilityGraphQL(any(), any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        val mockAvailabilityRequestBody =  HotelAvailabilityRequestBody("2022-10-04", "2022-10-05", HotelInfoDetails("TKKINPT"),
            Collections.singletonList(RoomSearch(2, 0, true, "DB")),
                BookingChannelDetails("PI", "MOBILE", "en"), "pi", emptyList(), "")

        graphQLHotelDetailsRepository.checkIfHotelAvailable(mockAvailabilityRequestBody,"").test()
            .assertError {it is GraphQLServerError}
    }

    @Test
    fun `Given Hotel availability with Packages and rates req being made  contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getHotelAvailabilityGraphQL(any(), any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        val mockAvailabilityRequestBody =  HotelAvailabilityRequestBody("2022-10-04", "2022-10-05", HotelInfoDetails("BANBRI"),
            Collections.singletonList(RoomSearch(2, 0, true, "DB")),
            BookingChannelDetails("PI", "MOBILE", "en"), "pi", emptyList(), "")

        graphQLHotelDetailsRepository.checkIfHotelAvailable(mockAvailabilityRequestBody,"").test()
            .assertError {it is GraphQLServerError}
    }
}