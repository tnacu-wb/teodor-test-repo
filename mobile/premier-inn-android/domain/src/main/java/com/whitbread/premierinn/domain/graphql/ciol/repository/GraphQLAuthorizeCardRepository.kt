package com.whitbread.premierinn.domain.graphql.ciol.repository

import com.whitbread.premierinn.domain.graphql.ciol.usecase.AuthorizeCardResult
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AuthorizeCardRequestBody
import kotlinx.coroutines.flow.Flow

interface GraphQLAuthorizeCardRepository {
    suspend fun authorizeCard(input: AuthorizeCardRequestBody): Flow<AuthorizeCardResult>
}
