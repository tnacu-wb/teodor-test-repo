package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToAnonymousNewsletterPreferencesDomain
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.entity.AnonymousNewsletterPreferencesDomain
import com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.repository.GraphQLAnonymousNewsletterPreferencesRepository
import io.reactivex.Single
import org.json.JSONObject

class GraphQLAnonymousNewsletterPreferencesRepositoryImpl(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariables: JSONObject
) : GraphQLAnonymousNewsletterPreferencesRepository {

    override fun getAnonymousNewsletterPreferences(
        email: String,
        brandCode: String,
        countryOfResidence: String,
        language: String
    ): Single<AnonymousNewsletterPreferencesDomain> {
        val query =
            fileDataProvider.loadFileFromAssetGQL("graphql/AnonymousNewsletterPreferencesQueryGQL.txt")
        val constructVariablesJsonObj = constructVariables(
            email = email,
            brandCode = brandCode,
            countryOfResidence = countryOfResidence,
            language = language,
            jsonObjectForVariables
        )

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObj)

        return wbGraphQLServicesApi.getAnonymousNewsletterPreferencesGraphQL(
            query = jsonObject.toString()
        )
            .onGraphQLError()
            .map { it.mapToAnonymousNewsletterPreferencesDomain() }
            .doOnError { throwable ->
                println("GraphQL not able to connect: " + throwable.localizedMessage)
            }
    }

    private fun constructVariables(
        email: String,
        brandCode: String,
        countryOfResidence: String,
        language: String,
        jsonObject: JSONObject
    ): JSONObject {
        return jsonObject.apply {
            put("email", email)
            put("brandCode", brandCode)
            put("countryOfResidence", countryOfResidence)
            put("language", language)
        }
    }
}