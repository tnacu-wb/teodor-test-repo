package com.whitbread.premierinn.data.graphql

import android.util.Log
import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.error.handleError
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelPreferenceDomain
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLHotelPreferencesRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationPreferencesRequestBody
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

private val TAG = GraphQLHotelPreferencesRepository::class.simpleName
private const val PREFERENCE_GROUPS_CODE = "EVENTS"

class GraphQLHotelPreferencesRepositoryImpl @Inject constructor(
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val dispatchers: AppDispatchers
) : GraphQLHotelPreferencesRepository {
    override suspend fun getHotelPreferences(hotelId: String, language: String) =
        flow {
            jsonObject.apply {
                put("query", fileDataProvider.loadFileFromAssetGQL("graphql/GetHotelPreferencesQueryGQL.txt"))
                put("variables", JSONObject().apply {
                    put("hotelId", hotelId)
                    put("preferenceGroupsCodes", PREFERENCE_GROUPS_CODE)
                    put("language", language)
                })
            }

            runCatching {
                nonRxGraphQLServicesApi.getHotelPreferences(jsonObject.toString())
            }.onFailure { error ->
                emit(Result.Error(error.handleError()))
            }.onSuccess { hotelPreferences ->
                hotelPreferences.errors?.firstOrNull()?.let { error ->
                    Log.e(TAG, "Error: $error")
                    emit(Result.Error(DataError.Network.BaseError(error.message)))
                } ?: run {
                    emit(Result.Success(
                        hotelPreferences.data?.hotelPreferencesContainer?.hotelPreferences?.map { it.mapToHotelPreferenceDomain() })
                    )
                }
            }
        }.flowOn(dispatchers.io)

    override suspend fun updateReservationPreferences(input: UpdateReservationPreferencesRequestBody) = flow {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/UpdateReservationPreferencesMutationGQL.txt")
        val createVariableJsonObj = input.constructVariables()
        jsonObject.put("query", query)
        jsonObject.put("variables", createVariableJsonObj)

        runCatching {
            nonRxGraphQLServicesApi.updateReservationPreferences(jsonObject.toString())
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { updateReservationPreferences ->
            updateReservationPreferences.errors?.firstOrNull()?.let { error ->
                Log.e(TAG, "Error: $error")
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                emit(Result.Success(Unit))
            }
        }
    }.flowOn(dispatchers.io)

    private fun UpdateReservationPreferencesRequestBody.constructVariables() = JSONObject().apply {
        put("hotelId", hotelId)
        put("reservationsIds", JSONArray(Gson().toJson(reservationsIds)))
        put("preferencesCollections", JSONArray(Gson().toJson(preferencesCollections)))
    }
}
