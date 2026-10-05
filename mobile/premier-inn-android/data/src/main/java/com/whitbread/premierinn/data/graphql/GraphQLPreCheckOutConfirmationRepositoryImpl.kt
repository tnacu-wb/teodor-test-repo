package com.whitbread.premierinn.data.graphql

import android.util.Log
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.error.handleError
import com.whitbread.premierinn.data.graphql.mapper.toPreCheckOutConfirmationDomain
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreCheckOutConfirmationRepository
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject

private val TAG = GraphQLPreCheckOutConfirmationRepositoryImpl::class.simpleName

class GraphQLPreCheckOutConfirmationRepositoryImpl @Inject constructor(
        private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
        private val fileDataProvider: FileDataProvider,
        private val jsonObject: JSONObject,
        private val dispatchers: AppDispatchers
) : GraphQLPreCheckOutConfirmationRepository {

    override suspend fun confirmPreCheckOut(basketReference: String) = flow {
        jsonObject.apply {
            put("query", fileDataProvider.loadFileFromAssetGQL("graphql/ConfirmPreCheckOutMutationGQL.txt"))
            put("variables", JSONObject().apply {
                put("basketReference", basketReference)
            })
        }

        runCatching {
            nonRxGraphQLServicesApi.confirmPreCheckOut(jsonObject.toString())
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { preCheckOutConfirmation ->
            preCheckOutConfirmation.errors?.firstOrNull()?.let { error ->
                Log.e(TAG, "Error: $error")
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                emit(Result.Success(preCheckOutConfirmation.data?.preCheckOutConfirmation?.toPreCheckOutConfirmationDomain()))
            }
        }
    }.flowOn(dispatchers.io)
}
