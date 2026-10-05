package com.whitbread.premierinn.data.graphql

import android.content.SharedPreferences
import com.whitbread.premierinn.data.GsonFactory
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToCountryListDomain
import com.whitbread.premierinn.data.remote.GraphQLErrorBody
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.remote.graphql.contracts.CountriesGraphQLContract
import com.whitbread.premierinn.data.utils.FileUtils
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import com.whitbread.premierinn.domain.graphql.countries.repository.GraphQLCountriesRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import junitparams.JUnitParamsRunner
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class GraphQLCountriesRepositoryImplTest {
    private val graphQlApi: WBGraphQLServicesApi = mockk()
    private val preferences: SharedPreferences = mockk()
    private val fileDataProvider: FileDataProvider = mockk()
    private var graphQLCountriesRepository: GraphQLCountriesRepository = mockk()
    private var jsonObject: JSONObject = mockk(relaxed = true)
    private var jsonObjectForVariablesCountries: JSONObject = mockk(relaxed = true)

    val COUNTRIES_SUCCESS = GsonFactory.create().fromJson(
            FileUtils.loadFileFromResource("api/graphql-responses/countries_success_gql.json"),
            CountriesGraphQLContract.CountriesData::class.java)

    val COUNTRIES_QUERY = "COuntries query"

    @Before
    fun setUp() {
        graphQLCountriesRepository = GraphQLCountriesRepositoryImpl(graphQlApi, fileDataProvider,
            jsonObject, jsonObjectForVariablesCountries)

        every { fileDataProvider.loadFileFromAssetGQL(any()) } returns COUNTRIES_QUERY
        every { preferences.getString(any(), any()) } returns " Some API key"
        every { jsonObject.toString() } returns COUNTRIES_QUERY
    }

    @Test
    fun `Given Countries is successful then no error is thrown and mapping works`() {
        every { graphQlApi.getCountriesGraphQL(any()) } returns Single.just(COUNTRIES_SUCCESS)

        val expectedHeaderInfoDomain = COUNTRIES_SUCCESS.mapToCountryListDomain().sortedBy { it.countryName }

        graphQLCountriesRepository.getCountries("gb", "en", "leisure").test()
                .assertNoErrors()
                .assertValue { result ->
                    result.size == expectedHeaderInfoDomain.size &&
                    result[0].countryCode == "AFG" &&
                    result[0].countryIsoCode == "AF" &&
                    result[0].countryName == "Afghanistan" &&
                    result[8].countryCode == "ARM" &&
                    result[8].countryIsoCode == "AM" &&
                    result[8].countryName == "Armenia"
                }
    }

    @Test
    fun `Given Countries contains Error then return response as GraphQl Error`() {
        every { graphQlApi.getCountriesGraphQL(any()) }  returns Single.error(
                GraphQlThrowable.GraphQLError(500, GraphQLErrorBody(500,"Server Error")))

        graphQLCountriesRepository.getCountries("gb", "en", "leisure").test()
                .assertError {it is GraphQLServerError }
    }

}