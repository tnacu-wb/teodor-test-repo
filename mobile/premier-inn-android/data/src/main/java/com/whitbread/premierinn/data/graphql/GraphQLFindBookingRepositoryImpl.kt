package com.whitbread.premierinn.data.graphql

import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToFindBookingDomain
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain
import com.whitbread.premierinn.domain.graphql.findBooking.repository.GraphQLFindBookingRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import io.reactivex.Single
import org.json.JSONObject
import javax.inject.Inject

class GraphQLFindBookingRepositoryImpl @Inject constructor(private val wbGraphQLServicesApi: WBGraphQLServicesApi,
        private val fileDataProvider: FileDataProvider,
        private val jsonObject: JSONObject,
        private val jsonObjectForVariablesFindBooking: JSONObject): GraphQLFindBookingRepository {

    override fun findBooking(input: FindBookingRequestBody): Single<FindBookingDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/FindBookingQueryGQL.txt")

        val createVariableJsonObj = input.constructVariables(jsonObjectForVariablesFindBooking)
        jsonObject.put("query", query)
        jsonObject.put("variables", createVariableJsonObj)

        return wbGraphQLServicesApi.findBookingGraphQL(
                query = jsonObject.toString())
                .onGraphQLError()
                .map { response ->
                    return@map response.mapToFindBookingDomain(input.resNo)
                }.doOnError { throwable ->
                    println("GraphQl not able to connect: " + throwable.localizedMessage)
                }    }


    private fun FindBookingRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("findBookingCriteria", JSONObject().apply {
                put("resNo", this@constructVariables.resNo)
                put("lastName", this@constructVariables.lastName)
                put("arrivalDate", this@constructVariables.arrivalDate)
                put("language", this@constructVariables.language)
                put("country", this@constructVariables.country)
                put(
                    "bookingChannel",
                    JSONObject(Gson().toJson(this@constructVariables.bookingChannel))
                )
            })
        }
    }
}