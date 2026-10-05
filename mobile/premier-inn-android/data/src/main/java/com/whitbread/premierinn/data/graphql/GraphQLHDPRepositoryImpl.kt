package com.whitbread.premierinn.data.graphql

import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToCancelOnHoldReservationGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelAvailabilityGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelInformationSlugGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToRatesInformationGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.graphql.hdp.entity.CancelOnHoldReservationDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelInformationSlugDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RatesInformationDomain
import com.whitbread.premierinn.domain.graphql.hdp.repository.GraphQLHDPRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import io.reactivex.Single
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class GraphQLHDPRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariablesHotelInfoSlug: JSONObject,
    private val jsonObjectForVariablesHotelAvailabilityRatesInfoAndRoomTypeInfo: JSONObject,
    private val jsonObjectForVariablesRatesInformation: JSONObject,
    private val jsonObjectForVariablesCancelOnHoldRes: JSONObject): GraphQLHDPRepository {

    override fun getHotelInfoBySlug(
        slug: String,
        country: String,
        language: String
    ): Single<HotelInformationSlugDomain> {

        val query = fileDataProvider.loadFileFromAssetGQL("graphql/HotelInformationSlugQueryGQL.txt")
        val createJsonObjectForVariables = jsonObjectForVariablesHotelInfoSlug.apply {
            put("slug", slug)
            put("country", country)
            put("language", language)
        }

        jsonObject.put("query", query)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.getHotelInformationBySlugGraphQL(
            query = jsonObject.toString()
        )
            .onGraphQLError()
            .map { response ->
                return@map response.mapToHotelInformationSlugGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }.onErrorReturn {
                HotelInformationSlugDomain.createEmptyDomain()
            }
    }

    override fun getHotelAvailabilityAndRatesInfoAndRoomTypeInfo(
        availabilityRequest: HotelAvailabilityRequestBody,
        language: String,
        country: String,
        hotelId: String,
        channel: String,
        token: String?,
        promoCode: String?,
        promoKind: String?
    ): Single<HotelAvailabilityDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/HotelAvailabilityRatesInfoAndRoomTypeInfoQueryGQL.txt")
        val constructVariablesJsonObject = constructVariables(jsonObjectForVariablesHotelAvailabilityRatesInfoAndRoomTypeInfo,
                availabilityRequest, language, country, hotelId, channel, promoCode, promoKind)

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject )

        return wbGraphQLServicesApi.getHotelAvailabilityGraphQL(
            token?.let { "$AUTHORIZATION_BEARER $token" },
            query = jsonObject.toString())
                .onGraphQLError()
                .map { response ->
                    return@map response.mapToHotelAvailabilityGQL(promoKind)
                }.doOnError { throwable ->
                    println("GraphQl not able to connect: "+throwable.localizedMessage)
                }
    }

    override fun getRatesInformation(brand: String, channel: String, ratePlans: List<String>, language: String,
                                     country: String, hotelId: String): Single<RatesInformationDomain> {

        val query = fileDataProvider.loadFileFromAssetGQL("graphql/RatesInformationV2QueryGQL.txt")
        val constructVariablesJsonObject = constructVariablesRatesInformation(jsonObjectForVariablesRatesInformation,
            brand, channel, ratePlans, language, country, hotelId)

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject )

        return wbGraphQLServicesApi.getRatesInformationGraphQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToRatesInformationGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }


    }

    override fun cancelOnHoldReservation(
        basketReference: String,
        hotelId: String
    ): Single<CancelOnHoldReservationDomain> {

        val mutation =
            fileDataProvider.loadFileFromAssetGQL("graphql/CancelOnHoldReservationMutationGQL.txt")

       val createJsonObjectForVariables = jsonObjectForVariablesCancelOnHoldRes.apply {
            put("basketReference", basketReference)
            put("hotelId", hotelId)
        }

        jsonObject.put("query", mutation)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.cancelOnHoldReservationGraphQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToCancelOnHoldReservationGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    private fun constructVariablesRatesInformation(jsonObject: JSONObject, brand: String,
                                                   channel: String,
                                                   ratePlans: List<String>, language: String,
                                                   country: String, hotelId: String): JSONObject {
        return jsonObject.apply {
            put("brand", brand)
            put("channel", channel)
            put("ratePlans", JSONArray(ratePlans))
            put("language", language)
            put("country", country)
            put("hotelId", hotelId)
        }
    }

    private fun constructVariables(
        jsonObject: JSONObject,
        availabilityRequest: HotelAvailabilityRequestBody,
        language: String,
        country: String,
        hotelId: String,
        channel: String,
        promoCode: String?,
        promoKind: String?
    ): JSONObject {
        return jsonObject.apply {
            put("availabilitySearchCriteria", JSONObject().apply {
                put("arrival", availabilityRequest.arrival)
                put("departure", availabilityRequest.departure)
                put("hotel", JSONObject(Gson().toJson(availabilityRequest.hotel)))
                put("rooms", JSONArray(Gson().toJson(availabilityRequest.rooms)))
                put("bookingChannel", JSONObject(Gson().toJson(availabilityRequest.bookingChannel)))
                promoCode?.let { put("promotionCode", it) }
                promoKind?.let { put("promoKind", it) }
                availabilityRequest.ratePlanCodes?.let {
                    put("ratePlanCodes", JSONArray(Gson().toJson(it)))
                }
                availabilityRequest.companyId?.takeIf { it.isNotEmpty() }?.let {
                    put("companyId", it)
                }


            })
            put("language", language)
            put("country", country)
            put("hotelId", hotelId)
            put("channel", channel)
            put("brand", availabilityRequest.brand)
        }
    }
}