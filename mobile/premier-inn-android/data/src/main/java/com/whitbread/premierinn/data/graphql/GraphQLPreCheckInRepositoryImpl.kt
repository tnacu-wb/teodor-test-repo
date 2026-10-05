package com.whitbread.premierinn.data.graphql

import android.util.Log
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.error.handleError
import com.whitbread.premierinn.data.graphql.mapper.toPreCheckInStatusDomain
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreCheckInRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PreCheckInRequestBody
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject

private val TAG = GraphQLPreCheckInRepositoryImpl::class.simpleName
private const val PRE_CHECK_IN_STATUS_ERROR = "Error"

class GraphQLPreCheckInRepositoryImpl @Inject constructor(
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val dispatchers: AppDispatchers
) : GraphQLPreCheckInRepository {

    override suspend fun preCheckIn(input: PreCheckInRequestBody) = flow {
        jsonObject.apply {
            put("query", fileDataProvider.loadFileFromAssetGQL("graphql/PreCheckInMutationGQL.txt"))
            put("variables", JSONObject().apply {
                put("arrivalTime", input.arrivalTime)
                put("reservationId", input.reservationId)
                put("hotelId", input.hotelId)
            })
        }

        runCatching {
            nonRxGraphQLServicesApi.preCheckIn(jsonObject.toString())
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { preCheckInStatus ->
            preCheckInStatus.errors?.firstOrNull()?.let { error ->
                Log.e(TAG, "Error: $error")
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                if (PRE_CHECK_IN_STATUS_ERROR.equals(preCheckInStatus.data?.preCheckInStatus?.status, true)) {
                    emit(Result.Error(DataError.Network.BaseError(preCheckInStatus.data?.preCheckInStatus?.message)))
                } else {
                    emit(Result.Success(preCheckInStatus.data?.preCheckInStatus?.toPreCheckInStatusDomain()))
                }
            }
        }

    }.flowOn(dispatchers.io)
}
