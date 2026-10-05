package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelPreferenceDomain
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelPreferencesGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.booking.entity.HotelPreferenceDomain
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.result.Result
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GraphQLHotelPreferencesRepositoryImplTest {
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private val dispatchers: AppDispatchers = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private var graphQLHotelPreferencesRepository: GraphQLHotelPreferencesRepositoryImpl = mockk()
    private val hotelPreferences = GsonFactory.create().fromJson(
        FileUtils.loadFileFromResource("api/graphql-responses/hotel_preferences_success_gql.json"),
        HotelPreferencesGraphQLContract.HotelPreferencesGraphQLData::class.java
    )

    @Before
    fun setUp() {
        every { dispatchers.io } returns testDispatcher
        graphQLHotelPreferencesRepository = GraphQLHotelPreferencesRepositoryImpl(
            nonRxGraphQLServicesApi, fileDataProvider, jsonObject, dispatchers
        )
        val hotelPreferencesQuery = "Hotel Preferences query"
        every { fileDataProvider.loadFileFromAssetGQL(any<String>()) } returns hotelPreferencesQuery
    }

    @Test
    fun `getHotelPreferences api call is successful and returns mapped result`() = runTest {
        //given
        val hotelId = "FRAMTI"
        val preferenceGroupsCodes = "EVENTS"

        coEvery { nonRxGraphQLServicesApi.getHotelPreferences(any<String>()) } returns hotelPreferences
        val expectedListOfOccasions = Result.Success(hotelPreferences.data?.hotelPreferencesContainer?.hotelPreferences
            ?.map { it.mapToHotelPreferenceDomain() })

        //when
        val hotelPreferencesResult = graphQLHotelPreferencesRepository.getHotelPreferences(hotelId, preferenceGroupsCodes)
        advanceUntilIdle()

        //then
        hotelPreferencesResult.collect { result ->
            val resultedOccasions = (result as Result.Success<List<HotelPreferenceDomain>?>).data
            expectedListOfOccasions.data?.forEach { expectedOccasion ->
                assertEquals(expectedOccasion.code, resultedOccasions?.find { it.code == expectedOccasion.code}?.code)
                assertEquals(expectedOccasion.preferenceGroup, resultedOccasions?.find { it.preferenceGroup == expectedOccasion.preferenceGroup}?.preferenceGroup)
                assertEquals(expectedOccasion.label, resultedOccasions?.find { it.label == expectedOccasion.label}?.label)
            }
        }
    }

    @Test
    fun `getHotelPreferences api call is successful and result returns data errors`() = runTest {
        //given
        val hotelId = "FRAMTI"
        val preferenceGroupsCodes = "EVENTS"

        val errors = listOf(GraphQLBase.BaseError(listOf("getHotelPreferences"), "500", "Server error"))
        val hotelPreferencesError = HotelPreferencesGraphQLContract.HotelPreferencesGraphQLData(null, errors)

        coEvery { nonRxGraphQLServicesApi.getHotelPreferences(any<String>()) } returns hotelPreferencesError

        val expectedError = Result.Error(DataError.Network.BaseError(hotelPreferencesError.errors?.get(0)?.message))

        //when
        val hotelPreferencesResult = graphQLHotelPreferencesRepository.getHotelPreferences(hotelId, preferenceGroupsCodes)
        advanceUntilIdle()

        //then
        hotelPreferencesResult.collect { result ->
            assertEquals(expectedError, result as Result.Error)
        }
    }

    @Test
    fun `getHotelPreferences api call fails and throws error`() = runTest {
        //given
        val hotelId = "FRAMTI"
        val preferenceGroupsCodes = "EVENTS"

        coEvery { nonRxGraphQLServicesApi.getHotelPreferences(any<String>()) } throws
                GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server error"))

        //when
        val hotelPreferencesResult = graphQLHotelPreferencesRepository.getHotelPreferences(hotelId, preferenceGroupsCodes)
        advanceUntilIdle()

        //then
        hotelPreferencesResult.collect { result ->
            assertTrue((result as Result.Error).error is DataError.Network.GraphQlError)
        }
    }
}
