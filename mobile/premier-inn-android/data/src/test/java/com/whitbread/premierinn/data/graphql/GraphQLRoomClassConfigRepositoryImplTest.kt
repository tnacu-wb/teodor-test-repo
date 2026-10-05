package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.RoomClassConfigGraphQLContract
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomClassConfigRequestBody
import com.whitbread.premierinn.domain.graphql.roomClassConfig.entity.RoomClassConfig
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class GraphQLRoomClassConfigRepositoryImplTest {
    private lateinit var wbGraphQLServicesApi: WBGraphQLServicesApi
    private lateinit var fileDataProvider: FileDataProvider
    private lateinit var jsonObject: JSONObject
    private lateinit var jsonObjectForVariablesRoomClassConfig: JSONObject
    private lateinit var repository: GraphQLRoomClassConfigRepositoryImpl

    @Before
    fun setUp() {
        wbGraphQLServicesApi = mockk()
        fileDataProvider = mockk()
        jsonObject = mockk(relaxed = true)
        jsonObjectForVariablesRoomClassConfig = mockk(relaxed = true)
        repository = GraphQLRoomClassConfigRepositoryImpl(
            wbGraphQLServicesApi,
            fileDataProvider,
            jsonObject,
            jsonObjectForVariablesRoomClassConfig
        )
        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns "query { roomClassConfig { code order } }"
        every { jsonObject.toString() } returns "query { roomClassConfig { code order } }"
    }

    @Test
    fun `should map valid RoomClassConfig items from API response`() {
        val items = listOf(
            RoomClassConfigGraphQLContract.RoomClassConfigItem("A", 1),
            RoomClassConfigGraphQLContract.RoomClassConfigItem("B", 2)
        )
        val contract = RoomClassConfigGraphQLContract.RoomClassConfigData(
            RoomClassConfigGraphQLContract.Data(
                RoomClassConfigGraphQLContract.RoomClassConfig(items)
            ), null
        )
        every { wbGraphQLServicesApi.roomClassConfig(any()) } returns Single.just(contract)
        val input = RoomClassConfigRequestBody("PI", "PI", "GB", "en")
        repository.roomClassConfigObservable(input).test()
            .assertValue(listOf(RoomClassConfig("A", 1), RoomClassConfig("B", 2)))
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun `should filter out RoomClassConfig items with null code or order`() {
        val items = listOf(
            RoomClassConfigGraphQLContract.RoomClassConfigItem(null, 1),
            RoomClassConfigGraphQLContract.RoomClassConfigItem("C", null),
            RoomClassConfigGraphQLContract.RoomClassConfigItem("D", 3)
        )
        val contract = RoomClassConfigGraphQLContract.RoomClassConfigData(
            RoomClassConfigGraphQLContract.Data(
                RoomClassConfigGraphQLContract.RoomClassConfig(items)
            ), null
        )
        every { wbGraphQLServicesApi.roomClassConfig(any()) } returns Single.just(contract)
        val input = RoomClassConfigRequestBody("PI", "PI", "GB", "en")
        repository.roomClassConfigObservable(input).test()
            .assertValue(listOf(RoomClassConfig("D", 3)))
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun `should return empty list when API response items are null`() {
        val contract = RoomClassConfigGraphQLContract.RoomClassConfigData(
            RoomClassConfigGraphQLContract.Data(
                RoomClassConfigGraphQLContract.RoomClassConfig(null)
            ), null
        )
        every { wbGraphQLServicesApi.roomClassConfig(any()) } returns Single.just(contract)
        val input = RoomClassConfigRequestBody("PI", "PI", "GB", "en")
        repository.roomClassConfigObservable(input).test()
            .assertValue(emptyList())
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun `should propagate error when API throws`() {
        every { wbGraphQLServicesApi.roomClassConfig(any()) } returns Single.error(Exception("API error"))
        val input = RoomClassConfigRequestBody("PI", "PI", "GB", "en")
        repository.roomClassConfigObservable(input).test()
            .assertError { it.message == "API error" }
    }

    @Test
    fun `should return empty list when contract data is null`() {
        val contract = RoomClassConfigGraphQLContract.RoomClassConfigData(
            null, null
        )
        every { wbGraphQLServicesApi.roomClassConfig(any()) } returns Single.just(contract)
        val input = RoomClassConfigRequestBody("PI", "PI", "GB", "en")
        repository.roomClassConfigObservable(input).test()
            .assertValue(emptyList())
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun `should return empty list when contract roomClassConfig is null`() {
        val contract = RoomClassConfigGraphQLContract.RoomClassConfigData(
            RoomClassConfigGraphQLContract.Data(null), null
        )
        every { wbGraphQLServicesApi.roomClassConfig(any()) } returns Single.just(contract)
        val input = RoomClassConfigRequestBody("PI", "PI", "GB", "en")
        repository.roomClassConfigObservable(input).test()
            .assertValue(emptyList())
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun `should return empty list when API response items list is empty`() {
        val contract = RoomClassConfigGraphQLContract.RoomClassConfigData(
            RoomClassConfigGraphQLContract.Data(
                RoomClassConfigGraphQLContract.RoomClassConfig(emptyList())
            ), null
        )
        every { wbGraphQLServicesApi.roomClassConfig(any()) } returns Single.just(contract)
        val input = RoomClassConfigRequestBody("PI", "PI", "GB", "en")
        repository.roomClassConfigObservable(input).test()
            .assertValue(emptyList())
            .assertNoErrors()
            .assertComplete()
    }
}