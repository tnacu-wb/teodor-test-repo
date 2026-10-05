package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.BRAND_CODE
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.common.ANDROID_APPS_CHANNEL
import com.whitbread.premierinn.domain.common.JOURNEY_PERMISSION_CENTRE
import com.whitbread.premierinn.domain.countries.entity.CountryDomain.Companion.getLocaleFromCountryCode
import com.whitbread.premierinn.domain.graphql.marketingPreferences.entity.UpdateMarketingPreferencesResponseDomain
import com.whitbread.premierinn.domain.graphql.marketingPreferences.repository.GraphQLMarketingPreferencesRepository
import io.reactivex.Single
import org.json.JSONObject

class GraphQLMarketingPreferencesRepositoryImpl(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val authenticationRepository: AuthenticationRepository,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariables: JSONObject
) : GraphQLMarketingPreferencesRepository {

    override fun updateMarketingPreferences(
        email: String,
        optIn: Boolean,
        doubleOptIn: Boolean,
        brandCodes: List<String>,
        isoCountryCode: String
    ): Single<UpdateMarketingPreferencesResponseDomain> {
        return authenticationRepository.getIdToken()
            .flatMap { token ->
                val mutation =
                    fileDataProvider.loadFileFromAssetGQL("graphql/UpdateMarketingPreferencesMutationGQL.txt")
                val constructVariablesJsonObj = constructVariables(
                    email = email,
                    optIn = optIn,
                    doubleOptIn = doubleOptIn,
                    brandCodes = brandCodes,
                    isoCountryCode = isoCountryCode,
                    jsonObjectForVariables
                )

                jsonObject.put("query", mutation)
                jsonObject.put("variables", constructVariablesJsonObj)

                wbGraphQLServicesApi.updateMarketingPreferencesGraphQL(
                    "$AUTHORIZATION_BEARER $token",
                    query = jsonObject.toString()
                )
                    .onGraphQLError()
                    .map { response ->
                        UpdateMarketingPreferencesResponseDomain(
                            updateMarketingPreferences = response.data?.updateMarketingPreferences ?: "Success"
                        )
                    }
                    .doOnError { throwable ->
                        println("GraphQL not able to connect: " + throwable.localizedMessage)
                    }
            }
    }

    private fun constructVariables(
        email: String,
        optIn: Boolean,
        doubleOptIn: Boolean,
        brandCodes: List<String>,
        isoCountryCode: String,
        jsonObject: JSONObject
    ): JSONObject {

        return jsonObject.apply {
            put("updateMarketingPreferencesRequest", JSONObject().apply {
                // brandCodes as simple string (first brand code)
                put("brandCodes", brandCodes.firstOrNull() ?: BRAND_CODE)
                put("optIn", optIn)
                put("doubleOptIn", doubleOptIn)
                put("customer", JSONObject().apply {
                    put("customerId", email)
                    put("countryOfResidence", isoCountryCode)
                    put("language", deviceLocaleProvider.getDeviceLanguage())
                })
                put("sourceDetails", JSONObject().apply {
                    put("locale", getLocaleFromCountryCode(isoCountryCode))
                    put("channel", ANDROID_APPS_CHANNEL)
                    put("journey", JOURNEY_PERMISSION_CENTRE)
                })
            })
        }
    }
}