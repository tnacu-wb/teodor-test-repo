package com.whitbread.premierinn.data.graphql

import android.util.Log
import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.error.handleError
import com.whitbread.premierinn.data.graphql.mapper.toDomain
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPackagesRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationPackagesRequestBody
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

private val TAG = GraphQLPackagesRepositoryImpl::class.simpleName

class GraphQLPackagesRepositoryImpl @Inject constructor(
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val packagesVariablesJson: JSONObject,
    private val dispatchers: AppDispatchers
) : GraphQLPackagesRepository {

    override suspend fun getPackages(input: HotelPackagesRequestBody) = flow {
        jsonObject.apply {
            put("query", fileDataProvider.loadFileFromAssetGQL("graphql/GetPackagesQueryGQL.txt"))
            put("variables", input.constructVariables(packagesVariablesJson))
        }


        runCatching {
            nonRxGraphQLServicesApi.getPackages(jsonObject.toString())
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { packagesData ->
            packagesData.errors?.firstOrNull()?.let { error ->
                Log.e(TAG, "Error: $error")
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                emit(Result.Success(packagesData.data?.packages.toDomain(input.nightsNumber)))
            }

            Log.w(TAG, "packagesData: $packagesData")
        }
    }.flowOn(dispatchers.io)

    override suspend fun updateReservationPackages(input: UpdateReservationPackagesRequestBody) = flow {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/UpdateReservationPackagesByReservationMutationGQL.txt")
        val createVariableJsonObj = input.constructVariables()

        jsonObject.put("query", query)
        jsonObject.put("variables", createVariableJsonObj)

        runCatching {
            nonRxGraphQLServicesApi.updateReservationPackages(jsonObject.toString())
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { updateReservationPackages ->
            updateReservationPackages.errors?.firstOrNull()?.let { error ->
                Log.e(TAG, "Error: $error")
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                emit(Result.Success(Unit))
            }
        }
    }.flowOn(dispatchers.io)

    private fun UpdateReservationPackagesRequestBody.constructVariables() = JSONObject().apply {
        put("updateReservationPackagesRequest", JSONObject().apply {
            put("basketReferenceId", basketReferenceId)
            put("hotelId", hotelId)
            put("arrivalDate", arrivalDate)
            put("departureDate", departureDate)
            put("roomsSelections", JSONArray(Gson().toJson(roomSelections)))
            put("previousRoomsSelections", JSONArray(Gson().toJson(deletedRoomSelections)))
        })
    }
}
