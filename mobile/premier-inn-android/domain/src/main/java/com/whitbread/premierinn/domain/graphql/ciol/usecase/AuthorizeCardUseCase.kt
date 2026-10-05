package com.whitbread.premierinn.domain.graphql.ciol.usecase

import com.whitbread.premierinn.domain.error.DataError
import com.whitbread.premierinn.domain.graphql.ciol.entity.AuthorizeCardDomain
import com.whitbread.premierinn.domain.graphql.ciol.repository.GraphQLAuthorizeCardRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AuthorizeCardRequestBody
import com.whitbread.premierinn.domain.result.Result
import javax.inject.Inject

typealias AuthorizeCardResult = Result<AuthorizeCardDomain?, DataError.Network>

class AuthorizeCardUseCase @Inject constructor(
    private val graphQLAuthorizeCardRepository: GraphQLAuthorizeCardRepository
) {

    suspend operator fun invoke(input: AuthorizeCardRequestBody) = graphQLAuthorizeCardRepository.authorizeCard(input)
}
