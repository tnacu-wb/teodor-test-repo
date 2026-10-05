package com.whitbread.premierinn.data.graphql

import android.util.Log
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.error.handleError
import com.whitbread.premierinn.data.graphql.mapper.toPreCheckInConfirmationDomain
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreCheckInConfirmationRepository
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject

private val TAG = GraphQLPreCheckInConfirmationRepositoryImpl::class.simpleName

class GraphQLPreCheckInConfirmationRepositoryImpl @Inject constructor(
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val dispatchers: AppDispatchers
) : GraphQLPreCheckInConfirmationRepository {
    override suspend fun confirmPreCheckIn(basketReferenceId: String, isPibaCnp: Boolean) = flow {
        jsonObject.apply {
            put("query", fileDataProvider.loadFileFromAssetGQL("graphql/PreCheckInConfirmationMutationGQL.txt"))
            put("variables", JSONObject().apply {
                put("basketReference", basketReferenceId)
                if (isPibaCnp) {
                    put("isCiol", isPibaCnp)
                }
            })
        }

        runCatching {
            nonRxGraphQLServicesApi.confirmPreCheckIn(jsonObject.toString())
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { preCheckInConfirmation ->
            preCheckInConfirmation.errors?.firstOrNull()?.let { error ->
                Log.e(TAG, "Error: $error")
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                emit(Result.Success(preCheckInConfirmation.data.confirmPreCheckIn.toPreCheckInConfirmationDomain()))
            }
        }
    }.flowOn(dispatchers.io)
}
