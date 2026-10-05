package com.whitbread.premierinn.data.graphql

import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.error.handleError
import com.whitbread.premierinn.data.graphql.mapper.mapToCreateReservationGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToCreateReservationGuestGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToPaymentMethodsGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToSaveReservationGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveReservationWithAncillariesRequestBody
import com.whitbread.premierinn.domain.graphql.summary.repository.GraphQLSummaryRepository
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.domain.utils.await
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class GraphQLSummaryRepositoryImpl @Inject constructor(
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariablesCreateReservation: JSONObject,
    private val jsonObjectForVariablesSaveReservation: JSONObject,
    private val jsonObjectForVariablesForPaymentMethodsAndBookingConfirmation: JSONObject,
    private val authenticationRepository: AuthenticationRepository,
    private val isCustomerLoggedIn: IsCustomerLoggedIn,
    private val dispatchers: AppDispatchers
): GraphQLSummaryRepository {

    override suspend fun createReservation(input: CreateReservationRequestBody) = flow {
        jsonObject.apply {
            put(
                "query",
                fileDataProvider.loadFileFromAssetGQL("graphql/CreateReservationMutationGQL.txt")
            )
            put("variables", input.constructVariables(jsonObjectForVariablesCreateReservation))
        }

        runCatching {
            nonRxGraphQLServicesApi.createReservationGraphQL(
                getIdToken()?.let { token -> "$AUTHORIZATION_BEARER $token" },
                jsonObject.toString()
            )
        }.onFailure { error ->
            error.handleError().let { convertedError ->
                if (convertedError is DataError.Network.UnauthorizedCustomerError) {
                    throw UnAuthorizedCustomerError
                }
                emit(Result.Error(convertedError))
            }
        }.onSuccess { response ->
            response.errors?.firstOrNull()?.let { error ->
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                response.mapToCreateReservationGQL().basketReference.let {
                    emit(Result.Success(it))
                }
            }
        }
    }.flowOn(dispatchers.io)

    override suspend fun saveReservationWithAncillaries(
        input: SaveReservationWithAncillariesRequestBody
    ) = flow {
        jsonObject.apply {
            put(
                "query",
                fileDataProvider.loadFileFromAssetGQL("graphql/SaveReservationMutationWithAncillariesGQL.txt")
            )
            put("variables", input.constructVariables(jsonObjectForVariablesSaveReservation))
        }

        runCatching {
            nonRxGraphQLServicesApi.saveReservationWithAncillariesGraphQL(
                query = jsonObject.toString()
            )
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { response ->
            response.errors?.firstOrNull()?.let { error ->
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                if (response.data != null &&
                    response.data.saveReservation != null
                ) {
                    emit(Result.Success(response.mapToSaveReservationGQL()))
                } else {
                    emit(Result.Error(DataError.Network.BaseError("Data is null")))
                }
            }
        }
    }

    override suspend fun getPaymentMethodsAndBookingConfirmation(
        paymentInput: PaymentMethodsRequestBody, basketReference: String, country: String,
        language: String, bookingChannel: String
    ) = flow {

        val query =
            fileDataProvider.loadFileFromAssetGQL("graphql/PaymentMethodsAndBookingConfirmationQueryGQL.txt")

        val constructVariablesJsonObject = constructVariables(
            paymentInput,
            basketReference,
            country,
            language,
            bookingChannel,
            jsonObjectForVariablesForPaymentMethodsAndBookingConfirmation
        )

        jsonObject.apply {
            put("query", query)
            put("variables", constructVariablesJsonObject)
        }

        runCatching {
            nonRxGraphQLServicesApi.getPaymentMethods(
                getIdToken()?.let { "$AUTHORIZATION_BEARER $it" },
                query = jsonObject.toString()
            )
        }.mapCatching { response ->
            // Check for GraphQL-level errors
            response.errors?.firstOrNull()?.message?.let { message ->
                if (message.contains("Session expired", ignoreCase = true)) {
                    throw UnAuthorizedCustomerError
                } else {
                    throw Exception("GraphQL error: $message")
                }
            } ?: response.mapToPaymentMethodsGQL()
        }.onSuccess { result ->
            emit(Result.Success(result))
        }.onFailure { error ->
            val isSessionExpired =
                error.message?.contains("Session expired", ignoreCase = true) == true
            if (isSessionExpired || error is UnAuthorizedCustomerError) {
                throw UnAuthorizedCustomerError
            } else {
                emit(Result.Error(error.handleError()))
            }
        }
    }.flowOn(dispatchers.io)

    private suspend fun getIdToken(): String? =
        runCatching {
            if (isCustomerLoggedIn().await()) {
                authenticationRepository.getIdToken().await()
            } else {
                null
            }
        }.getOrElse {
            // Do not handle exception, as null comes whenever user is not logged in
            null
        }

    override suspend fun createReservationGuest(
        input: CreateReservationGuestRequestBody
    ) = flow {
        jsonObject.apply {
            put(
                "query",
                fileDataProvider.loadFileFromAssetGQL("graphql/CreateReservationGuestMutationGQL.txt")
            )
            put("variables", input.constructVariables(jsonObjectForVariablesCreateReservation))
        }

        runCatching {
            nonRxGraphQLServicesApi.createReservationGuestGraphQL(
                getIdToken()?.let { "$AUTHORIZATION_BEARER $it" },
                jsonObject.toString()
            )
        }.onFailure { error ->
            error.handleError().let { convertedError ->
                if (convertedError is DataError.Network.UnauthorizedCustomerError) {
                    throw UnAuthorizedCustomerError
                }
                emit(Result.Error(convertedError))
            }
        }.onSuccess { response ->
            emit(Result.Success(response.mapToCreateReservationGuestGQL()))
        }
    }.flowOn(dispatchers.io)

    private fun CreateReservationRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("createReservationCriteria", JSONObject().apply {
                put("reservations", JSONArray(Gson().toJson(this@constructVariables.reservations)))
                put(
                    "bookingChannel",
                    JSONObject(Gson().toJson(this@constructVariables.bookingChannel))
                )
            })
        }
    }

    private fun SaveReservationWithAncillariesRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("ancillariesCriteria", JSONObject().apply {
                put("basketReferenceId", this@constructVariables.basketReferenceId)
                put("hotelId", this@constructVariables.hotelId)
                put("arrivalDate", this@constructVariables.arrival)
                put("departureDate", this@constructVariables.departure)
                put(
                    "roomsSelections",
                    JSONArray(Gson().toJson(this@constructVariables.roomsSelections))
                )
                put(
                    "previousRoomsSelections",
                    JSONArray(Gson().toJson(this@constructVariables.previousRoomsSelections))
                )
            })
        }
    }

    private fun constructVariables(
        paymentInput: PaymentMethodsRequestBody,
        basketReference: String, country: String,
        language: String, bookingChannel: String,
        jsonObject: JSONObject
    ): JSONObject {
        return jsonObject.apply {
            put("paymentMethodsCriteria", JSONObject().apply {
                put("basketReference", paymentInput.basketReference)
                put("language", paymentInput.language)
                put("country", paymentInput.country)
                put("clientChannel", paymentInput.clientChannel)
                put("userType", paymentInput.userType)
            })
            put("basketReference", basketReference)
            put("country", country)
            put("language", language)
            put("bookingChannel", bookingChannel)
        }
    }

    private fun CreateReservationGuestRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
        return jsonObject.apply {
            put("createReservationGuestCriteria", JSONObject().apply {
                put("basketReference", this@constructVariables.basketReference)
                put("hotelId", this@constructVariables.hotelId)
                put("reasonForStay", this@constructVariables.reasonForStay)
                put("booker", JSONObject(Gson().toJson(this@constructVariables.booker)))
                put(
                    "stayingGuests",
                    JSONArray(Gson().toJson(this@constructVariables.stayingGuests))
                )
            })
        }
    }
}