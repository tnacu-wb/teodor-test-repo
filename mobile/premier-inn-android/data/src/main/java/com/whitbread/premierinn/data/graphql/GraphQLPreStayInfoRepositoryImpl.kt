package com.whitbread.premierinn.data.graphql

import android.util.Log
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.error.handleError
import com.whitbread.premierinn.data.graphql.mapper.mapToUpdateReservationGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPreStayInfoRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRegCardRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdatePreStayInfoRequestBody
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject

private val TAG = GraphQLPreStayInfoRepositoryImpl::class.simpleName

class GraphQLPreStayInfoRepositoryImpl(
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val variablesJson: JSONObject,
    private val dispatchers: AppDispatchers
) : GraphQLPreStayInfoRepository {

    override fun updatePreStayInfo(token: String?, input: UpdatePreStayInfoRequestBody) = flow {
        jsonObject.apply {
            put("query", fileDataProvider.loadFileFromAssetGQL("graphql/UpdatePreStayInfoQueryGQL.txt"))
            put("variables", input.constructVariables(variablesJson))
        }

        runCatching {
            nonRxGraphQLServicesApi.updatePreStayInfo(
                token?.let { "$AUTHORIZATION_BEARER $token" },
                jsonObject.toString()
            )
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { preStayInfoData ->
            preStayInfoData.errors?.firstOrNull()?.let { error ->
                Log.e(TAG, "updatePreStayInfo() Error: $error")
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                preStayInfoData.mapToUpdateReservationGQL().basketReference.let {
                    emit(Result.Success(Unit))
                }
            }
        }
    }.flowOn(dispatchers.io)

    override fun createReservationGuestForRegCard(token: String?, input: CreateReservationGuestRegCardRequestBody) = flow {
        val query = JSONObject().apply {
            put("query", fileDataProvider.loadFileFromAssetGQL("graphql/UpdatePreStayInfoQueryGQL.txt"))
            put("variables", input.constructVariables(variablesJson))
        }

        runCatching {
            nonRxGraphQLServicesApi.updatePreStayInfo(
                token?.let { "$AUTHORIZATION_BEARER $token" },
                query.toString()
            )
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { response ->
            response.errors?.firstOrNull()?.let { error ->
                Log.e(TAG, "createReservationGuestForRegCard() Error: $error")
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                response.mapToUpdateReservationGQL().basketReference.let {
                    emit(Result.Success(Unit))
                }
            }
        }
    }
}
