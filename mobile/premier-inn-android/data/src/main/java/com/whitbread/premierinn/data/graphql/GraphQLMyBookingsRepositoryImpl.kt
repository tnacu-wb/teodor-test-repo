package com.whitbread.premierinn.data.graphql

import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToBookingDomainGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.graphql.myBookings.repository.GraphQLMyBookingsRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingHistoryRequestBody
import io.reactivex.Single
import org.json.JSONObject
import javax.inject.Inject

class GraphQLMyBookingsRepositoryImpl @Inject constructor(private val wbGraphQLServicesApi: WBGraphQLServicesApi,
                                                          private val fileDataProvider: FileDataProvider,
                                                          private val jsonObject: JSONObject,
                                                          private val jsonObjectForVariablesBookingHistory: JSONObject): GraphQLMyBookingsRepository {

    override fun getBookingHistory(input: BookingHistoryRequestBody, token: String): Single<List<Booking>> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/BookingHistoryQueryGQL.txt")

        val createVariableJsonObj = input.constructVariables(jsonObjectForVariablesBookingHistory)
        jsonObject.put("query", query)
        jsonObject.put("variables", createVariableJsonObj)

        return wbGraphQLServicesApi.getBookingHistoryGraphQL(
                "$AUTHORIZATION_BEARER $token",
                query = jsonObject.toString())
                .onGraphQLError()
                .map { response ->
                    return@map response.mapToBookingDomainGQL(input.business)
                }
            .doOnError { throwable ->
                    println("GraphQl not able to connect: " + throwable.localizedMessage)
                }    }


    private fun BookingHistoryRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("bookingHistoryRequest", JSONObject().apply {
                put("business", this@constructVariables.business)
                put("includeCheckInBookings", this@constructVariables.includeCheckInBookings)
                put("sortOrder", this@constructVariables.sortOrder)
                put("continuationToken", this@constructVariables.continuationToken)
                put("pageSize", this@constructVariables.pageSize)
                put("pageIndex", this@constructVariables.pageIndex)
                put("bookingChannel", this@constructVariables.bookingChannel)
                put("bookingChannel", JSONObject(Gson().toJson(this@constructVariables.bookingChannel)))
            })
        }
    }
}