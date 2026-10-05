package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelAvailabilitiesGQL
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelAvailabilitiesGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.common.EMPLOYEE_CODE
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilitiesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Place
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Room
import com.whitbread.premierinn.domain.graphql.srp.repository.GraphQLSRPRepository
import com.whitbread.premierinn.domain.graphql.promotions.repository.GraphQLPromotionsInformationRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Collections

@RunWith(JUnitParamsRunner::class)
class GraphQLSRPRepositoryImplTest {
    private val graphQlApi: WBGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphqlSrpRepo: GraphQLSRPRepository = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val jsonObjectForVariablesHotelAvailabilities: JSONObject = mockk(relaxed = true)
    private val promotionsRepository: GraphQLPromotionsInformationRepository = mockk()

    val HOTEL_AVAILABILITIES_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/hotel_availabilities_success_gql.json"),
        HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData::class.java
    )
    val HOTEL_AVAILABILITIES_EMPLOYEE_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/hotel_availabilities_employee_success_gql.json"),
        HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData::class.java
    )
    val HOTEL_AVAILABILITIES_BUSINESS_SUCCESS = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/hotel_availabilities_business_success_gql.json"),
        HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData::class.java
    )
    val HOTEL_AVAILABILITIES_QUERY_STRING = "Hotel availabilities query"

    @Before
    fun setUp() {
        graphqlSrpRepo = GraphQLSRPRepositoryImpl(
            graphQlApi, fileDataProvider,
            jsonObject, jsonObjectForVariablesHotelAvailabilities,
            promotionsRepository
        )
        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns HOTEL_AVAILABILITIES_QUERY_STRING
        every { jsonObject.toString() } returns HOTEL_AVAILABILITIES_QUERY_STRING
    }

    //Leisure tests
    @Test
    fun `Given Hotel availabilities req is successful then no error is thrown and mapping works`() {
        every { graphQlApi.getHotelAvailabilitiesGraphQL(any(), any()) } returns Single.just(
            HOTEL_AVAILABILITIES_SUCCESS
        )

        val mockAvailabilitiesRequestBody = HotelAvailabilitiesRequestBody(
            Place("ChIJdd4hrwug2EcRmSrV3Vo6llI", "PLACEID", "MILES", 50),
            "2024-10-04", "2024-10-05", Collections.singletonList(Room("DB", 1, 0)),
            emptyList(), "gb", "en", "MOBILE", "PI", "MOBILE",
            "DISTANCE", 1, 40, 10, null)

        val expectedHotelAvailability = HOTEL_AVAILABILITIES_SUCCESS.mapToHotelAvailabilitiesGQL()

        graphqlSrpRepo.getHotelAvailabilities(
            mockAvailabilitiesRequestBody, "token").test()
            .assertNoErrors()
            .assertValue { it == expectedHotelAvailability }
            .assertValue { it.multiHotelAvailabilities!![1].hotelId == "LONSTM" }
            .assertValue { it.multiHotelAvailabilities!![1].name == "hub London Covent Garden" }
    }

    @Test
    fun `Given Hotel availabilities req is successful for Employee offer then no error is thrown and mapping works`() {
        every { graphQlApi.getHotelAvailabilitiesGraphQL(any(), any()) } returns Single.just(
            HOTEL_AVAILABILITIES_EMPLOYEE_SUCCESS
        )

        val mockAvailabilitiesRequestBody = HotelAvailabilitiesRequestBody(
            Place("ChIJdd4hrwug2EcRmSrV3Vo6llI", "PLACEID", "MILES", 50),
            "2024-10-04", "2024-10-05", Collections.singletonList(Room("DB", 1, 0)),
            listOf(EMPLOYEE_CODE), "gb", "en", "MOBILE", "PI", "MOBILE",
            "DISTANCE", 1, 40, 10, null)

        val expectedHotelAvailability = HOTEL_AVAILABILITIES_EMPLOYEE_SUCCESS.mapToHotelAvailabilitiesGQL()

        graphqlSrpRepo.getHotelAvailabilities(
            mockAvailabilitiesRequestBody, "token").test()
            .assertNoErrors()
            .assertValue { it == expectedHotelAvailability }
            .assertValue { it.multiHotelAvailabilities!![0].hotelId == "LONKIN" }
            .assertValue { it.multiHotelAvailabilities!![0].name == "hub London Kings Cross" }
            .assertValue { it.multiHotelAvailabilities!![0].hotelAvailability.cellCode == EMPLOYEE_CODE}
    }

    @Test
    fun `Given Only Hotel availabilities req contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getHotelAvailabilitiesGraphQL(any(), any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        val mockAvailabilitiesRequestBody = HotelAvailabilitiesRequestBody(
            Place("ChIJdd4hrwug2EcRmSrV3Vo6llI", "PLACEID", "MILES", 50),
            "2024-10-04", "2024-10-05", Collections.singletonList(Room("DB", 1, 0)),
            emptyList(), "gb", "en", "MOBILE", "PI", "MOBILE",
            "DISTANCE", 1, 40, 10, null)

        graphqlSrpRepo.getHotelAvailabilities(mockAvailabilitiesRequestBody, "token").test()
            .assertError { it is GraphQLServerError }
    }

    //Business tests
    @Test
    fun `Given Hotel availabilities req is successful for Business booking then no error is thrown and mapping works`() {
        every { graphQlApi.getHotelAvailabilitiesGraphQL(any(), any()) } returns Single.just(
            HOTEL_AVAILABILITIES_BUSINESS_SUCCESS
        )

        val mockAvailabilitiesRequestBody = HotelAvailabilitiesRequestBody(
            Place("ChIJdd4hrwug2EcRmSrV3Vo6llI", "PLACEID", "MILES", 50),
            "2024-10-04", "2024-10-05", Collections.singletonList(Room("DB", 1, 0)),
            emptyList(), "gb", "en", "MOBILE", "BB", "MOBILE",
            "DISTANCE", 1, 40, 10, null
        )

        val expectedHotelAvailability = HOTEL_AVAILABILITIES_BUSINESS_SUCCESS.mapToHotelAvailabilitiesGQL()

        graphqlSrpRepo.getHotelAvailabilities(
            mockAvailabilitiesRequestBody, "token").test()
            .assertNoErrors()
            .assertValue { it == expectedHotelAvailability }
            .assertValue { it.multiHotelAvailabilities!![1].hotelId == "LINMIL" }
            .assertValue { it.multiHotelAvailabilities!![1].name == "Lincoln (Canwick)" }
    }

    @Test
    fun `Given Hotel availabilities req contains Error for Business booking then return response as GraphQl Error`() {
        every { graphQlApi.getHotelAvailabilitiesGraphQL(any(), any()) }  returns Single.error(
            GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        val mockAvailabilitiesRequestBody = HotelAvailabilitiesRequestBody(
            Place("ChIJdd4hrwug2EcRmSrV3Vo6llI", "PLACEID", "MILES", 50),
            "2024-10-04", "2024-10-05", Collections.singletonList(Room("DB", 1, 0)),
            emptyList(), "gb", "en", "MOBILE", "BB", "MOBILE",
            "DISTANCE", 1, 40, 10, null
        )

        graphqlSrpRepo.getHotelAvailabilities(mockAvailabilitiesRequestBody, "token").test()
            .assertError { it is GraphQLServerError }
    }
}