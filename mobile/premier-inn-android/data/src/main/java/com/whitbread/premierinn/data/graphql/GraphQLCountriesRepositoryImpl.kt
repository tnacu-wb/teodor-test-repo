package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToCountryListDomain
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.graphql.countries.repository.GraphQLCountriesRepository
import io.reactivex.Single
import org.json.JSONObject
import javax.inject.Inject

class GraphQLCountriesRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariablesCountries: JSONObject): GraphQLCountriesRepository {

    override fun getCountries(country: String, language: String, site: String): Single<List<CountryDomain>> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/CountriesQueryGQL.txt")
        val createVariableJsonObj = jsonObjectForVariablesCountries.apply {
            put("country", country)
            put("language", language)
            put("site", site)
        }

        jsonObject.put("query", query)
        jsonObject.put("variables", createVariableJsonObj)

        return wbGraphQLServicesApi.getCountriesGraphQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToCountryListDomain().sortedBy { it.countryName }
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

}
