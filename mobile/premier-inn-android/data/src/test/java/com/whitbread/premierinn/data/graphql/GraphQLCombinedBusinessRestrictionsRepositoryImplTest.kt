package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToCombinedBusinessRestrictionsGQL
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.CombinedBusinessRestrictionsGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class GraphQLCombinedBusinessRestrictionsRepositoryImplTest {
    private val fileDataProvider: FileDataProvider = mockk()
    private val jsonObject: JSONObject = mockk(relaxed = true)
    private val wbGraphQLServicesApi: WBGraphQLServicesApi = mockk()
    private lateinit var repository: GraphQLCombinedBusinessRestrictionsRepositoryImpl

    private val queryString = "query string"
    private val variables = JSONObject().apply {
        put("channelPI", Channel.PI.name)
        put("channelBB", Channel.BB.name)
        put("channelEMPLOYEE", Channel.EMPLOYEE.name)
    }

    companion object {
        private val COMBINED_BUSINESS_RESTRICTIONS_SUCCESS = GsonFactory.create().fromJson(
            FileUtils.loadFileFromResource("api/graphql-responses/combined_business_restrictions_success_gql.json"),
            CombinedBusinessRestrictionsGraphQLContract.CombinedBusinessRestrictionsResponse::class.java
        )
    }

    @Before
    fun setUp() {
        repository = GraphQLCombinedBusinessRestrictionsRepositoryImpl(
            fileDataProvider,
            jsonObject,
            wbGraphQLServicesApi
        )
        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns queryString
        every { jsonObject.put("query", queryString) } returns jsonObject
        every { jsonObject.put("variables", any<JSONObject>()) } returns jsonObject
        every { jsonObject.toString() } returns "{\"query\":\"$queryString\",\"variables\":$variables}"
    }

    @Test
    fun `GIVEN successful response WHEN getCombinedBusinessRestrictions THEN mapping works and no error`() {
        every { wbGraphQLServicesApi.getCombinedBusinessRestrictionsGraphQL(any()) } returns Single.just(
            COMBINED_BUSINESS_RESTRICTIONS_SUCCESS
        )
        val expected =
            COMBINED_BUSINESS_RESTRICTIONS_SUCCESS.data?.mapToCombinedBusinessRestrictionsGQL()
        repository.getCombinedBusinessRestrictions().test()
            .assertNoErrors()
            .assertValue { it == expected }
            .assertComplete()
    }

    @Test
    fun `GIVEN error response WHEN getCombinedBusinessRestrictions THEN returns error`() {
        val error = RuntimeException("GraphQL error")
        every { wbGraphQLServicesApi.getCombinedBusinessRestrictionsGraphQL(any()) } returns Single.error(
            error
        )
        repository.getCombinedBusinessRestrictions().test()
            .assertError { it == error }
    }

    @Test
    fun `GIVEN null data in response WHEN getCombinedBusinessRestrictions THEN throws IllegalStateException`() {
        val responseWithNullData =
            mockk<CombinedBusinessRestrictionsGraphQLContract.CombinedBusinessRestrictionsResponse>(
                relaxed = true
            ) {
                every { data } returns null
            }
        every { wbGraphQLServicesApi.getCombinedBusinessRestrictionsGraphQL(any()) } returns Single.just(
            responseWithNullData
        )
        repository.getCombinedBusinessRestrictions().test()
            .assertError { it is IllegalStateException && it.message == "Invalid combined business restrictions response" }
    }
}