package com.whitbread.premierinn.data.graphql

import com.google.gson.Gson
import com.whitbread.premierinn.data.booking.dao.BookingDao
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToBookingConfirmationDomain
import com.whitbread.premierinn.data.graphql.mapper.mapToBookingDomain
import com.whitbread.premierinn.data.graphql.mapper.mapToCancelReservationGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToDomain
import com.whitbread.premierinn.data.graphql.mapper.mapToPackagesGraphQL
import com.whitbread.premierinn.data.graphql.mapper.mapToRoomKeyInstructionsGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.entity.BookingConfirmation
import com.whitbread.premierinn.domain.countries.repository.CountriesRepository
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.CancelReservationDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.ResendInvoiceDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.RoomKeyInstructionsDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.repository.GraphQLBookingDetailsRepository
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ResendInvoiceRequestBody
import io.reactivex.Single
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class GraphQLBookingDetailsRepositoryImpl @Inject constructor(
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariablesBookingConfirmationAndManageBooking: JSONObject,
    private val jsonObjectForVariablesBookingConfirmation: JSONObject,
    private val jsonObjectForVariablesCancelReservation: JSONObject,
    private val jsonObjectForVariablesRoomKey: JSONObject,
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    val dao: BookingDao,
    private val countriesRepository: CountriesRepository,
    private val jsonObjectForVariablesPackages: JSONObject
) : GraphQLBookingDetailsRepository {

    override fun bookingConfirmationAndManageBooking(basketReference: String, country: String, language: String, bookingChannel: String, hotelName: String?,
                                                     cancelInformationRequestBody: CancelInformationRequestBody, token: String?): Single<Booking> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/BookingConfirmationAndManageBookingQueryGQL.txt")
        val constructVariablesJsonObject = constructVariables(jsonObjectForVariablesBookingConfirmationAndManageBooking,
            basketReference, country, language, bookingChannel, cancelInformationRequestBody)

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject )

        return wbGraphQLServicesApi.bookingConfirmationGraphQL(
            bearerToken = token?.let { "$AUTHORIZATION_BEARER $it" },
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToBookingDomain(
                    hotelName = hotelName,
                    isBusinessBooking = bookingChannel != Channel.PI.name,
                    countries = countriesRepository.getCountriesFromSharedPref()
                )
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    override fun bookingConfirmationForFindBooking(uuidBasketReference: String, country: String, language: String, hotelName: String?): Single<BookingConfirmation> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/BookingConfirmationQueryGQL.txt")
        val constructVariablesJsonObject = constructVariables(jsonObjectForVariablesBookingConfirmation,
                uuidBasketReference, country, language)

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject )

        return wbGraphQLServicesApi.bookingConfirmationGraphQL(
                bearerToken = null,
                query = jsonObject.toString())
                .onGraphQLError()
                .map { response ->
                    return@map  response.data.bookingConfirmation!!.mapToBookingConfirmationDomain()
                }.doOnError { throwable ->
                    println("GraphQl not able to connect: " + throwable.localizedMessage)
                }
    }

    override fun getRoomKeyInstructions(categoryLabelsRequestBody: CategoryLabelsRequestBody): Single<RoomKeyInstructionsDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/GetCategoryLabelsQueryGQL.txt")
        val constructVariablesJsonObject = categoryLabelsRequestBody.constructVariables(jsonObjectForVariablesRoomKey)

        jsonObject.put("query", query)
        jsonObject.put("variables", constructVariablesJsonObject)

        return wbGraphQLServicesApi.getRoomKeyInstructionsGraphQL(
            query = jsonObject.toString())
                .onGraphQLError()
            .map { response ->
                return@map response.mapToRoomKeyInstructionsGQL()
            }
            .doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    override fun cancelReservation(
        cancelReservationRequestBody: CancelReservationRequestBody,
        reference: String
    ): Single<CancelReservationDomain> {

        val mutation =
            fileDataProvider.loadFileFromAssetGQL("graphql/CancelReservationMutationGQL.txt")

        val createVariableJsonObj = cancelReservationRequestBody.constructVariables(jsonObjectForVariablesCancelReservation)
        jsonObject.put("query", mutation)
        jsonObject.put("variables", createVariableJsonObj)

        return wbGraphQLServicesApi.cancelReservationGraphQL(
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToCancelReservationGQL()
            }.doAfterSuccess {
                dao.updateAsCanceled(reference)
            }
            .doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    override fun packages(packagesRequestBody: HotelPackagesRequestBody): Single<DataPackagesDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/PackagesQueryGQL.txt")

        val createJsonObjectForVariables =
            packagesRequestBody.constructVariables(jsonObjectForVariablesPackages)

        jsonObject.put("query", query)
        jsonObject.put("variables", createJsonObjectForVariables)

        return wbGraphQLServicesApi.packagesGraphQL(query = jsonObject.toString())
            .onGraphQLError()
            .map { response -> return@map response.data?.packages.mapToPackagesGraphQL(packagesRequestBody.nightsNumber)
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    override fun resendInvoiceEmail(body: ResendInvoiceRequestBody): Single<ResendInvoiceDomain> {
        jsonObject.apply {
            put(
                "query",
                fileDataProvider.loadFileFromAssetGQL("graphql/ResendInvoiceMutationGQL.txt")
            )
            put("variables", JSONObject().apply {
                put("resendInvoiceRequest", JSONObject(Gson().toJson(body)))
            })
        }

        return wbGraphQLServicesApi.resendInvoiceEmail(jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToDomain()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    private fun constructVariables(jsonObject: JSONObject,
                                   basketReference: String,
                                   country: String,
                                   language: String,
                                   bookingChannel: String?,
                                   cancelInformationRequestBody: CancelInformationRequestBody) : JSONObject {
        return jsonObject.apply {
            put("basketReference", basketReference)
            put("country", country)
            put("language", language)
            bookingChannel?.let { put("bookingChannel", it) }
            put("cancelInformationCriteria", JSONObject().apply {
                put("basketReference", cancelInformationRequestBody.basketReference)
                put("hotelId", cancelInformationRequestBody.hotelId)
                put("userDateTime", cancelInformationRequestBody.userDateTime)
                put("token", cancelInformationRequestBody.token)
                put("bookingChannel", JSONObject(Gson().toJson(cancelInformationRequestBody.bookingChannel)))
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

    private fun constructVariables(jsonObject: JSONObject,
                                   basketReference: String,
                                   country: String,
                                   language: String) : JSONObject {
        return jsonObject.apply {
            put("basketReference", basketReference)
            put("country", country)
            put("language", language)
        }
    }
    private fun CancelReservationRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("cancellationCriteria", JSONObject().apply {
                put("basketReference", this@constructVariables.basketReference)
                put("hotelId", this@constructVariables.hotelId)
                put("token", this@constructVariables.token)
            })
        }
    }
}