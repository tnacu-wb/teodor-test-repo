package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.graphql.error.handleError
import com.whitbread.premierinn.data.graphql.mapper.mapToPaymentMethodsGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.NonRxGraphQLServicesApi
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLPaymentMethodsRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.domain.utils.await
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject

private val TAG = GraphQLPaymentMethodsRepositoryImpl::class.simpleName

class GraphQLPaymentMethodsRepositoryImpl @Inject constructor(
    private val nonRxGraphQLServicesApi: NonRxGraphQLServicesApi,
    private val authenticationRepository: AuthenticationRepository,
    private val isCustomerLoggedIn: IsCustomerLoggedIn,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val variablesJson: JSONObject,
    private val dispatchers: AppDispatchers
) : GraphQLPaymentMethodsRepository {

    override suspend fun getPaymentMethods(input: PaymentMethodsRequestBody) =
        flow {
            jsonObject.apply {
                put(
                    "query",
                    fileDataProvider.loadFileFromAssetGQL("graphql/SimplePaymentMethodsQueryGQL.txt")
                )
                put("variables", input.constructVariables(variablesJson))
            }

            runCatching {
                nonRxGraphQLServicesApi.getPaymentMethods(
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
            }.onSuccess { paymentMethodsData ->
                paymentMethodsData.errors?.firstOrNull()?.let { error ->
                    emit(Result.Error(DataError.Network.BaseError(error.message)))
                } ?: run {
                    paymentMethodsData.mapToPaymentMethodsGQL().paymentMethods?.let {
                        emit(Result.Success(it))
                    } ?: run {
                        emit(Result.Error(DataError.Network.Unknown(EMPTY_STRING)))
                    }
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
}
