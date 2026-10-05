package com.whitbread.premierinn.domain.graphql.ciol.repository

import com.whitbread.premierinn.domain.graphql.ciol.usecase.GetPaymentMethodsResult
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import kotlinx.coroutines.flow.Flow

interface GraphQLPaymentMethodsRepository {
    suspend fun getPaymentMethods(input: PaymentMethodsRequestBody): Flow<GetPaymentMethodsResult>
}