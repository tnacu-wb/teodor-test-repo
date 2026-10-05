package com.whitbread.premierinn.data.graphql

import android.util.Log
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.error.handleError
import com.whitbread.premierinn.data.graphql.mapper.toAuthorizeCardDomain
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLAuthorizeCardRepository
import com.whitbread.premierinn.domain.graphql.ciol.usecase.AuthorizeCardResult
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AuthorizeCardRequestBody
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject

private val TAG = GraphQLAuthorizeCardRepositoryImpl::class.simpleName

class GraphQLAuthorizeCardRepositoryImpl @Inject constructor(
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val dispatchers: AppDispatchers
) : GraphQLAuthorizeCardRepository {

    override suspend fun authorizeCard(input: AuthorizeCardRequestBody): Flow<AuthorizeCardResult> = flow {

        jsonObject.apply {
            put("query", fileDataProvider.loadFileFromAssetGQL("graphql/AuthorizeCardMutationGQL.txt"))
            put("variables", JSONObject().apply {
                put("requestId", input.requestId)
                put("environment", input.environment)
                put("language", input.language)
                put("country", input.country)
            })
        }

        runCatching {
            nonRxGraphQLServicesApi.authorizeCard(jsonObject.toString())
        }.onFailure { error ->
            emit(Result.Error(error.handleError()))
        }.onSuccess { authorizeCard ->
            authorizeCard.errors?.firstOrNull()?.let { error ->
                Log.e(TAG, "Error: $error")
                emit(Result.Error(DataError.Network.BaseError(error.message)))
            } ?: run {
                emit(Result.Success(authorizeCard.data?.authorizeCard?.toAuthorizeCardDomain()))
            }
        }
    }.flowOn(dispatchers.io)
}
