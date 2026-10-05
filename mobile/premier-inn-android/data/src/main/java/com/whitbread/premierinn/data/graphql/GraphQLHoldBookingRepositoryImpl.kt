package com.whitbread.premierinn.data.graphql

import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToBookingInformationGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToCreateReservationGQL
import com.whitbread.premierinn.data.graphql.mapper.toDataPackagesDomain
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.graphql.common.repository.GraphQLHoldBookingRepository
import com.whitbread.premierinn.domain.graphql.hdp.entity.BookingInformationDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HoldBookingRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.summary.entity.CreateReservationDomain
import io.reactivex.Single
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class GraphQLHoldBookingRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariablesCreateReservation: JSONObject,
    private val jsonObjectForBookingInformation: JSONObject,
    private val jsonObjectForVariablesPackages: JSONObject,
    @Named("AkamaiSensorData") private val sensorData: Provider<String>

) : GraphQLHoldBookingRepository {

    override fun holdBooking(
        holdBookingRequestBody: HoldBookingRequestBody, token: String?
    ): Single<Pair<DataPackagesDomain, String>>{
        return createReservation(
            holdBookingRequestBody.createReservationRequestBody,
            token
        ).flatMap { createReservationResp ->
                if (createReservationResp.basketReference.isNotEmpty()) {
                    getBookingInformation(
                        createReservationResp.basketReference,
                        holdBookingRequestBody.country,
                        holdBookingRequestBody.language,
                        holdBookingRequestBody.bookingChannel
                    ).flatMap { res ->
                            if (res.bookingFlowId.isNotEmpty()) {
                                getPackages(
                                    holdBookingRequestBody.packagesRequestBody.copy(
                                        basketReferenceId = createReservationResp.basketReference,
                                        bookingFlowId = res.bookingFlowId
                                    ), createReservationResp.basketReference
                                )
                            } else {
                                Single.error(BookingInformationException(createReservationResp.basketReference))
                            }
                        }
                } else {
                    Single.error(CreateReservationException())
                }
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }


    fun createReservation(
        createReservationRequestBody: CreateReservationRequestBody, token: String?
    ): Single<CreateReservationDomain> {

        val mutation =
            fileDataProvider.loadFileFromAssetGQL("graphql/CreateReservationMutationGQL.txt")

        val createVariableJsonObj =
            createReservationRequestBody.constructVariables(jsonObjectForVariablesCreateReservation)
        jsonObject.put("query", mutation)
        jsonObject.put("variables", createVariableJsonObj)

        return wbGraphQLServicesApi.createReservationGraphQL(
            token?.let { "$AUTHORIZATION_BEARER $token" },
            sensorData.get(), query = jsonObject.toString()
        ).onGraphQLError().map { response ->
                return@map response.mapToCreateReservationGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    fun getPackages(hotelPackagesRequestBody: HotelPackagesRequestBody, basketReference: String): Single<Pair<DataPackagesDomain, String>> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/PackagesQueryGQL.txt")

        val createJsonObjectForVariables =
            hotelPackagesRequestBody.constructVariables(jsonObjectForVariablesPackages)

        jsonObject.put("query", query)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.packagesGraphQL(query = jsonObject.toString()).onGraphQLError()
            .flatMap { response ->
                if (response.errors != null) {
                    Single.just(Pair(DataPackagesDomain.createDefault(), basketReference))
                } else {
                    Single.just(Pair(response.data?.packages.toDataPackagesDomain(),
                        basketReference))
                }
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    private fun getBookingInformation(
        basketReference: String,
        country: String,
        language: String,
        bookingChannelDetails: BookingChannelDetails
    ): Single<BookingInformationDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/BookingInformationQuery.txt")

        val constructVariablesJsonObject = constructVariables(
            jsonObjectForBookingInformation,
            basketReference,
            country,
            language,
            bookingChannelDetails
        )

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject)

        return wbGraphQLServicesApi.getBookingInformationGraphQL(query = jsonObject.toString())
            .onGraphQLError().map { response ->
                return@map response.mapToBookingInformationGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    private fun HoldBookingRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("createReservationCriteria", JSONObject().apply {
                put(
                    "reservations",
                    JSONArray(Gson().toJson(this@constructVariables.createReservationRequestBody.reservations))
                )
                put(
                    "bookingChannel",
                    JSONObject(Gson().toJson(this@constructVariables.createReservationRequestBody.bookingChannel))
                )
            })
        }
    }

    private fun CreateReservationRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("createReservationCriteria", JSONObject().apply {
                put(
                    "reservations", JSONArray(Gson().toJson(this@constructVariables.reservations))
                )
                put(
                    "bookingChannel",
                    JSONObject(Gson().toJson(this@constructVariables.bookingChannel))
                )
            })
        }
    }

    private fun constructVariables(
        jsonObject: JSONObject,
        basketReference: String,
        country: String,
        language: String,
        bookingChannelDetails: BookingChannelDetails
    ): JSONObject {
        return jsonObject.apply {
            put("basketReference", basketReference)
            put("country", country)
            put("language", language)
            put("bookingChannelCriteria", JSONObject(Gson().toJson(bookingChannelDetails)))
        }
    }
}

data class BookingInformationException(val basketReference: String) : Exception()
class CreateReservationException : Exception()
