package com.whitbread.premierinn.data.graphql

import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelAvailabilitiesGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.graphql.promotions.repository.GraphQLPromotionsInformationRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilitiesRequestBody
import com.whitbread.premierinn.domain.graphql.srp.entity.HotelAvailabilitiesDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.HotelAvailabilitiesWithPromotionsDomain
import com.whitbread.premierinn.domain.graphql.srp.repository.GraphQLSRPRepository
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import io.reactivex.Single
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class GraphQLSRPRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariablesHotelAvailabilities: JSONObject,
    private val promotionsRepository: GraphQLPromotionsInformationRepository
) : GraphQLSRPRepository {

    override fun getHotelAvailabilities(input: HotelAvailabilitiesRequestBody, token: String?): Single<HotelAvailabilitiesDomain> {

        val query = fileDataProvider.loadFileFromAssetGQL("graphql/HotelAvailabilitiesQueryGQL.txt")
        val constructVariablesJsonObject = input.constructVariables(jsonObjectForVariablesHotelAvailabilities)

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject)

        return wbGraphQLServicesApi.getHotelAvailabilitiesGraphQL(
            token?.let { "$AUTHORIZATION_BEARER $token" },
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToHotelAvailabilitiesGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    private fun HotelAvailabilitiesRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("availabilitiesSearchCriteria", JSONObject().apply {
                put("place", JSONObject(Gson().toJson(this@constructVariables.place)))
                put("startDate", this@constructVariables.startDate)
                put("endDate", this@constructVariables.endDate)
                put("rooms", JSONArray(Gson().toJson(this@constructVariables.rooms)))
                this@constructVariables.ratePlanCodes?.let {
                    put("ratePlanCodes", JSONArray(Gson().toJson(it)))
                }
                put("country", this@constructVariables.country)
                put("language", this@constructVariables.language)
                put("oldWorldChannel", this@constructVariables.oldWorldChannel)
                put("channel", this@constructVariables.channel)
                put("subChannel", this@constructVariables.subChannel)
                put("sort", this@constructVariables.sort)
                put("page", this@constructVariables.page)
                put("initialPageSize", this@constructVariables.initialPageSize)
                put("lazyLoadPageSize", this@constructVariables.lazyLoadPageSize)
                this@constructVariables.companyId?.takeIf { it.isNotEmpty() }?.let {
                    put("companyId", it)
                }
            })
        }
    }

    override fun getHotelAvailabilitiesWithPromotions(
        input: HotelAvailabilitiesRequestBody,
        token: String?
    ): Single<HotelAvailabilitiesWithPromotionsDomain> {
        // Chain the calls: first get availabilities, then promotions, but always emit availabilities even if promotions fails
        return getHotelAvailabilities(input, token)
            .flatMap { availabilitiesDomain ->
                val brand = availabilitiesDomain.multiHotelAvailabilities
                    ?.firstNotNullOfOrNull { it.hotelInformation.brand }
                    ?.let { if (it == Hotel.Brand.PID.name) it else Hotel.Brand.PI.name }
                    ?: Hotel.Brand.PI.name

                promotionsRepository.getPromotionsInformation(
                    input.country,
                    input.language,
                    input.channel,
                    brand,
                    input.startDate,
                    input.endDate
                )
                .map { promotions ->
                    HotelAvailabilitiesWithPromotionsDomain(availabilitiesDomain, promotions)
                }
                .onErrorReturn { error ->
                    HotelAvailabilitiesWithPromotionsDomain(availabilitiesDomain, null)
                }
            }
    }
}