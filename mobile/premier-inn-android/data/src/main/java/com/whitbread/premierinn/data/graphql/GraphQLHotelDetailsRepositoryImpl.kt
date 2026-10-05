package com.whitbread.premierinn.data.graphql

import com.google.gson.Gson
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelAvailabilityGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelDisclaimerDomain
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelInformationGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelDisclaimerDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.graphql.hdp.entity.*
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import io.reactivex.Single
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class GraphQLHotelDetailsRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val businessPersistenceManager: BusinessPersistenceManager,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariablesHotelInfo: JSONObject,
    private val jsonObjectForVariablesGetHotelAvailableCheck: JSONObject,
    private val jsonObjectForVariablesHotelAvailability: JSONObject,
    private val jsonObjectForVariablesHotelDisclaimer: JSONObject): GraphQLHotelDetailsRepository {

    override fun getHotelInfo(
        country: String,
        hotelId: String,
        language: String
    ): Single<HotelInformationDomain> {

        val query = fileDataProvider.loadFileFromAssetGQL("graphql/HotelInformationQueryGQL.txt")
        val createJsonObjectForVariables = jsonObjectForVariablesHotelInfo.apply {
            put("country", country)
            put("hotelId", hotelId)
            put("language", language)
        }

        jsonObject.put("query", query)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.getHotelInformationGraphQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToHotelInformationGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: "+throwable.localizedMessage)
            }
    }

    override fun checkIfHotelAvailable(input: HotelAvailabilityRequestBody, token: String?): Single<HotelAvailabilityDomain> {

        val query = fileDataProvider.loadFileFromAssetGQL("graphql/HotelAvailabilityForAvailableCheckQueryGQL.txt")
        val constructVariablesJsonObject = input.constructVariables(jsonObjectForVariablesGetHotelAvailableCheck)

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject )

        return wbGraphQLServicesApi.getHotelAvailabilityGraphQL(
            token?.let { "$AUTHORIZATION_BEARER $token" },
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToHotelAvailabilityGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: "+throwable.localizedMessage)
            }
    }


    private fun HotelAvailabilityRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("availabilitySearchCriteria", JSONObject().apply {
                put("arrival", this@constructVariables.arrival)
                put("departure", this@constructVariables.departure)
                put("hotel", JSONObject(Gson().toJson(this@constructVariables.hotel)))
                put("rooms", JSONArray(Gson().toJson(this@constructVariables.rooms)))
                put("bookingChannel", JSONObject(Gson().toJson(this@constructVariables.bookingChannel)))
                if (this@constructVariables.bookingChannel.channel.equals(Channel.BB.name, ignoreCase = true)) {
                        put("companyId", businessPersistenceManager.getOperaCompanyId())
                }
                this@constructVariables.ratePlanCodes?.let {
                    put("ratePlanCodes", JSONArray(Gson().toJson(it)))
                }
                this@constructVariables.companyId?.takeIf { it.isNotEmpty() }?.let {
                    put("companyId", it)
                }
            })
        }
    }

    private fun CategoryLabelsRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("country", this@constructVariables.country)
            put("language", this@constructVariables.language)
            put("category", this@constructVariables.category)
            put("labels", JSONArray(Gson().toJson(this@constructVariables.labels)))
        }
    }

    override fun getHotelAvailability(input: HotelAvailabilityRequestBody, token: String?): Single<HotelAvailabilityDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/HotelAvailabilityQueryGQL.txt")
        val constructVariablesJsonObject = input.constructVariables(jsonObjectForVariablesHotelAvailability)

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject )

        return wbGraphQLServicesApi.getHotelAvailabilityGraphQL(
            bearerToken = token?.let { "$AUTHORIZATION_BEARER $it" },
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToHotelAvailabilityGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: "+throwable.localizedMessage)
            }
    }

    override fun getHotelDisclaimer(input: CategoryLabelsRequestBody): Single<HotelDisclaimerDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/GetCategoryLabelsQueryGQL.txt")
        val constructVariablesJsonObject = input.constructVariables(jsonObjectForVariablesHotelDisclaimer)

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject)

        return wbGraphQLServicesApi.getHotelDisclaimerGraphQL(
            query = jsonObject.toString())
            .map { response ->
                return@map response.mapToHotelDisclaimerDomain(input.labels[0])
            }
            .onErrorResumeNext {
                Single.just(HotelDisclaimerDomain(EMPTY_STRING))
            }
    }
}
